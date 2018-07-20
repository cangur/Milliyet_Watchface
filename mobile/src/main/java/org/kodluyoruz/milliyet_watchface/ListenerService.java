package org.kodluyoruz.milliyet_watchface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.kodluyoruz.milliyet_watchface.api.model.Data;
import org.kodluyoruz.milliyet_watchface.api.model.DataList;
import org.kodluyoruz.milliyet_watchface.api.model.Images;
import org.kodluyoruz.milliyet_watchface.api.service.SNOClient;
import org.kodluyoruz.milliyet_watchface.api.service.ServiceGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListenerService extends WearableListenerService {

    public static final String ID_KEY = "newsID";
    public static final String VOICE_KEY = "voice";
    private final String URL_KEY = "url";
    private final String TITLE_KEY = "title";
    private final String SUMMARY_KEY = "summary";
    private final String CONTENT_KEY = "content";
    private final String WEAR_LASTNEWS_PATH = "/last_news_path";
    private final String ID_PATH = "/id";
    private final String SEND_ID_PATH = "/sending_id";
    private final String VOICE_PATH = "/voice";
    private final String TAG = "ListenerService";
    Context mContext;
    private int index = 0;

    private int failedCount = 0;

    public static Asset createAssetFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();

                if (path.equalsIgnoreCase(ID_PATH)) {
                    Log.d(TAG, ID_PATH);
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    String id = dataMapItem.getDataMap().getString(ID_KEY);

                    Intent intent = new Intent(this, BigTextMainActivity.class);
                    intent.putExtra(ID_KEY, id);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (path.equalsIgnoreCase(VOICE_PATH)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    String id = dataMapItem.getDataMap().getString(ID_KEY);
                    Boolean isVoice = dataMapItem.getDataMap().getBoolean(VOICE_KEY);

                    Intent intent = new Intent(this, BigTextMainActivity.class);
                    intent.putExtra(ID_KEY, id);
                    intent.putExtra(VOICE_KEY, isVoice);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (path.equalsIgnoreCase(SEND_ID_PATH)) {
                    Log.d(TAG, SEND_ID_PATH);
                    sendingID();
                }
            }
        }
    }

    private void sendingID() {
        SNOClient client = ServiceGenerator.createService(SNOClient.class);

        final List<Data> lastNewsArrayList = new ArrayList<>();

        final ArrayList<String> sendID = new ArrayList<>();
        final ArrayList<String> sendTitle = new ArrayList<>();
        final ArrayList<String> sendSummary = new ArrayList<>();
        final ArrayList<String> sendContent = new ArrayList<>();
        final ArrayList<String> sendURL = new ArrayList<>();

        final List<Images>[] images = new List[25];

        final ArrayList<DataMap> allNewsData = new ArrayList<>(25);

        Call<DataList> repoCall = client.reposForLastNews();
        repoCall.enqueue(new Callback<DataList>() {
            @Override
            public void onResponse(Call<DataList> call, Response<DataList> response) {
                lastNewsArrayList.addAll(response.body().getData());
                for (int i = 0; i < 25; i++) {

                    index = i;

                    try {

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                        StrictMode.setThreadPolicy(policy);

                        URL url = new URL(response.body().getData().get(i).getImages()[0].getBaseUrl().split("images")[0] + "320x320/images/"
                                + response.body().getData().get(i).getImages()[0].getName());

                        DataMap newsData = new DataMap();

                        newsData.putString("title", lastNewsArrayList.get(index).getTitle());
                        newsData.putString("summary", lastNewsArrayList.get(index).getSummary());
                        newsData.putString("content", lastNewsArrayList.get(index).getContent());
                        newsData.putString("id", lastNewsArrayList.get(index).getId());
                        newsData.putAsset("image", createAssetFromBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream())));

                        allNewsData.add(newsData);

                        if (index == 24) {
                            Log.d(TAG, "dsdsadasd");
                            sendLastNewsData(allNewsData);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "allNewsData: " + allNewsData.size());


//                    Picasso.with(mContext).load(response.body().getData().get(i).getImages()[0].getBaseUrl().split("images")[0]+"100x100/images/"
//                            + response.body().getData().get(i).getImages()[0].getName())
//                            .into(new Target() {
//                                @Override
//                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                    DataMap newsData = new DataMap();
//
//                                    newsData.putString("title", lastNewsArrayList.get(index).getTitle());
//                                    newsData.putString("summary", lastNewsArrayList.get(index).getSummary());
//                                    newsData.putString("content", lastNewsArrayList.get(index).getContent());
//                                    newsData.putString("id", lastNewsArrayList.get(index).getId());
//                                    newsData.putAsset("image", createAssetFromBitmap(bitmap));
//
//                                    allNewsData.add(newsData);
//
//                                    if(allNewsData.size() == 25 - failedCount){
//                                        Log.d(TAG, "dsdsadasd");
//                                        //sendLastNewsData(sendID, sendContent, sendSummary, sendTitle, sendURL);
//                                    }
//                                }
//
//                                @Override
//                                public void onBitmapFailed(Drawable errorDrawable) {
//
//                                    failedCount++;
//                                }
//
//                                @Override
//                                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                                    Log.d(TAG, "22222");
//                                }
//                            });

                }
            }

            @Override
            public void onFailure(Call<DataList> call, Throwable t) {

            }
        });
    }

    private void sendLastNewsData(ArrayList<DataMap> allNewsData) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WEAR_LASTNEWS_PATH);
        putDataMapRequest.getDataMap().putDataMapArrayList("news_datamap", allNewsData);
        putDataMapRequest.getDataMap().putLong("time", new Date().getTime());

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        putDataMapRequest.setUrgent();

        Task<DataItem> putDataTask = Wearable.getDataClient(this).putDataItem(putDataRequest);
        putDataTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                Log.d(TAG, "Last news send: " + dataItem);
            }
        });
    }
}
