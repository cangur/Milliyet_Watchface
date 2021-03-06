package org.kodluyoruz.milliyet_watchface.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.wear.widget.CircularProgressLayout;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.kodluyoruz.milliyet_watchface.NewsDTO;
import org.kodluyoruz.milliyet_watchface.R;
import org.kodluyoruz.milliyet_watchface.adapter.NewsListAdapter;

import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NewsListActivity extends WearableActivity implements DataClient.OnDataChangedListener {

    private final String TAG = "NewsListActivity";

    private final String LAST_NEWS_KEY = "news_datamap";
    private final String SEND_ID_KEY = "send_id";
    private final String SEND_ID_PATH = "/sending_id";
    private final String WEAR_LASTNEWS_PATH = "/last_news_path";

    NewsDTO[] newsDTO;

    private Context mContext;
    private RecyclerView recyclerView;
    private TextView mTextView;
    private CircularProgressLayout bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        mTextView = findViewById(R.id.text);
        recyclerView = findViewById(R.id.activity_newsdetail_rv_news);
        bar = findViewById(R.id.progress_bar);

        // Enables Always-on
        setAmbientEnabled();

        mContext = getApplicationContext();
        initEvent();
    }

    private void initEvent() {
        sendingRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Wearable.getDataClient(this).removeListener(this);
    }

    private void sendingRequest() {
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(SEND_ID_PATH);
        dataMapRequest.getDataMap().putString(SEND_ID_KEY, "send_id");
        dataMapRequest.getDataMap().putLong("time", new Date().getTime());
        PutDataRequest request = dataMapRequest.asPutDataRequest();
        request.setUrgent();

        Wearable.getDataClient(this).putDataItem(request)
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d(TAG, "Request send: " + dataItem);
                    }
                });
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                String path = event.getDataItem().getUri().getPath();
                if (path.equalsIgnoreCase(WEAR_LASTNEWS_PATH)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    List<DataMap> lastNewsData = dataMapItem.getDataMap().getDataMapArrayList(LAST_NEWS_KEY);

                    Iterator<DataMap> itr = lastNewsData.iterator();
                    newsDTO = new NewsDTO[25];
                    int index = 0;
                    while (itr.hasNext()) {
                        DataMap newsData = itr.next();

                        newsDTO[index] = new NewsDTO();
                        newsDTO[index].newsTitle = newsData.getString("title");
                        newsDTO[index].newsSummary = newsData.getString("summary");
                        newsDTO[index].newsContent = newsData.getString("content");
                        newsDTO[index].newsID = newsData.getString("id");

                        try {
                            newsDTO[index].newsBitmap = new LoadBitmapAsyncTask().execute(newsData.getAsset("image")).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                        index++;

                    }

                    toAdapter(newsDTO);
                }
            }
        }
    }

    private void toAdapter(NewsDTO[] newsDTO) {
        NewsListAdapter adapter = new NewsListAdapter(newsDTO, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        bar.setVisibility(View.INVISIBLE);


    }

    private class LoadBitmapAsyncTask extends AsyncTask<Asset, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Asset... params) {

            if (params.length > 0) {

                Asset asset = params[0];

                Task<DataClient.GetFdForAssetResponse> getFdForAssetResponseTask =
                        Wearable.getDataClient(getApplicationContext()).getFdForAsset(asset);

                try {
                    // Block on a task and get the result synchronously. This is generally done
                    // when executing a task inside a separately managed background thread. Doing
                    // this on the main (UI) thread can cause your application to become
                    // unresponsive.
                    DataClient.GetFdForAssetResponse getFdForAssetResponse =
                            Tasks.await(getFdForAssetResponseTask);

                    InputStream assetInputStream = getFdForAssetResponse.getInputStream();

                    if (assetInputStream != null) {
                        return BitmapFactory.decodeStream(assetInputStream);

                    } else {
                        Log.w(TAG, "Requested an unknown Asset.");
                        return null;
                    }

                } catch (ExecutionException exception) {
                    Log.e(TAG, "Failed retrieving asset, Task failed: " + exception);
                    return null;

                } catch (InterruptedException exception) {
                    Log.e(TAG, "Failed retrieving asset, interrupt occurred: " + exception);
                    return null;
                }

            } else {
                Log.e(TAG, "Asset must be non-null");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {
                Log.d(TAG, "Setting background image on second page..");
            }
        }
    }
}

