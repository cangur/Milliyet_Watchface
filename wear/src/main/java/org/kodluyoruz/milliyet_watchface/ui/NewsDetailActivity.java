package org.kodluyoruz.milliyet_watchface.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.kodluyoruz.milliyet_watchface.MyBounceInterpolator;
import org.kodluyoruz.milliyet_watchface.R;

import java.util.Date;

public class NewsDetailActivity extends WearableActivity implements View.OnClickListener, ViewTreeObserver.OnScrollChangedListener, DataClient.OnDataChangedListener {

    private final String TAG = "NewsDetailActivity";

    private final String TIME_KEY = "time";
    private final String ID_KEY = "newsID";
    private final String VOICE_KEY = "voice";
    private final String ID_PATH = "/id";
    private final String VOICE_PATH = "/voice";
    Boolean scrollFlag = false;
    private TextView mTitle;
    private TextView mSummary;
    private TextView mContent;
    private ImageView mImage;
    private ImageButton mBtnVoice;
    private ImageButton mBtnOpenOnApp;
    private ScrollView mScrollView;
    private String newsId;
    private boolean isVoice;

    private DataClient mDataClient;

    private String[] stateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        mTitle = findViewById(R.id.activity_newsdetail_title);
        mSummary = findViewById(R.id.activity_newsdetail_summary);
        mContent = findViewById(R.id.activity_newsdetail_content);
        mImage = findViewById(R.id.activity_newsdetail_imageview);
        mBtnVoice = findViewById(R.id.activity_newsdetail_btn_voice);
        mBtnOpenOnApp = findViewById(R.id.activity_newsdetail_btn_openOnApp);
        mScrollView = findViewById(R.id.activity_newsdetail_scroll_view);

        // Enables Always-on
        setAmbientEnabled();

        initEvent();
    }

    private void initEvent() {
        mBtnOpenOnApp.setOnClickListener(this);
        mBtnVoice.setOnClickListener(this);

        stateButton = new String[]{"vUp", "vDown"};
        mBtnVoice.setTag(stateButton[0]);

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(this);

        isVoice = false;

        setWidget();
    }

    private void setWidget() {
        byte[] byteArray = getIntent().getByteArrayExtra("bitmap");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        newsId = getIntent().getStringExtra("id");
        String content = getIntent().getStringExtra("content");

        if (content.contains("<img src=")) {
            content = deleteGifUrl(getIntent().getStringExtra("content"));
        }

        mTitle.setText(getIntent().getStringExtra("title"));
        mSummary.setText(getIntent().getStringExtra("summary"));
        mContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        mImage.setImageBitmap(bmp);
    }

    private String deleteGifUrl(String content) {
        String delStr = content.substring(content.indexOf("<p><img src="));
        delStr = delStr.substring(0, delStr.indexOf("/></p>") + 6);
        content = content.replace(delStr, "");
        content = content.replace("<hr />", "");

        return content;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_newsdetail_btn_voice:
                final Animation myAnimOff = AnimationUtils.loadAnimation(this, R.anim.bounce);
                MyBounceInterpolator interpolatorOff = new MyBounceInterpolator(0.2, 20);
                myAnimOff.setInterpolator(interpolatorOff);
                if (mBtnVoice.getTag() == stateButton[0]) {
                    mBtnVoice.setTag(stateButton[1]);
                    mBtnVoice.setImageResource(R.drawable.ic_volume_off_black_24dp);
                    mBtnVoice.startAnimation(myAnimOff);
                    isVoice = true;
                    sendMessage(isVoice);
                } else if (mBtnVoice.getTag() == stateButton[1]) {
                    mBtnVoice.setTag(stateButton[0]);
                    mBtnVoice.setImageResource(R.drawable.ic_volume_up_black_24dp);
                    mBtnVoice.startAnimation(myAnimOff);
                    cutOffVoiceRequest();
                }
                break;

            case R.id.activity_newsdetail_btn_openOnApp:
                isVoice = false;
                showAnimation();
                sendMessage(isVoice);
                break;
        }
    }

    private void cutOffVoiceRequest() {
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/cutoff_voice");
        dataMapRequest.getDataMap().putBoolean("cutoff_voice", true);
        dataMapRequest.getDataMap().putLong("time", new Date().getTime());
        PutDataRequest request = dataMapRequest.asPutDataRequest();
        request.setUrgent();

        Task<DataItem> dataItemTask = mDataClient.putDataItem(request);
        dataItemTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                Log.d(TAG, "Cut off request send: " + dataItem);
            }
        });
    }

    private void showAnimation() {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Uygulama akıllı telefondan açıldı.");
        startActivity(intent);
    }


    private void sendMessage(boolean isVoice) {
        if (!isVoice) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(ID_PATH);

            dataMapRequest.getDataMap().putString(ID_KEY, newsId);
            dataMapRequest.getDataMap().putLong(TIME_KEY, new Date().getTime());

            Log.d(TAG, "ID KEY & TIME KEY -- SEND ");

            PutDataRequest request = dataMapRequest.asPutDataRequest();
            request.setUrgent();

            Task<DataItem> dataItemTask = mDataClient.putDataItem(request);
            dataItemTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
                @Override
                public void onSuccess(DataItem dataItem) {
                    Log.d(TAG, "Sending message from wear device was succesful: " + dataItem);
                }
            });
        } else {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(VOICE_PATH);

            dataMapRequest.getDataMap().putBoolean(VOICE_KEY, true);
            dataMapRequest.getDataMap().putString(ID_KEY, newsId);
            dataMapRequest.getDataMap().putLong(TIME_KEY, new Date().getTime());

            Log.d(TAG, "VOICE KEY, ID KEY, TIME KEY -- SEND");

            PutDataRequest request = dataMapRequest.asPutDataRequest();
            request.setUrgent();

            Task<DataItem> dataItemTask = mDataClient.putDataItem(request);
            dataItemTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
                @Override
                public void onSuccess(DataItem dataItem) {
                    Log.d(TAG, "Sending message from wear device was succesful: " + dataItem);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataClient = Wearable.getDataClient(this);
        mDataClient.addListener(this);
    }

    @Override
    public void onScrollChanged() {
        if (mScrollView.getScaleY() > 0.7) {
            if (!scrollFlag) {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                MyBounceInterpolator interpolatorOn = new MyBounceInterpolator(0.3, 20);
                myAnim.setInterpolator(interpolatorOn);
                mBtnVoice.startAnimation(myAnim);

                scrollFlag = true;
            }
        }
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (path.equalsIgnoreCase("/tts_done")) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Boolean change = dataMapItem.getDataMap().getBoolean("tts_done");

                    if (change) {
                        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                        MyBounceInterpolator interpolatorOn = new MyBounceInterpolator(0.3, 20);
                        myAnim.setInterpolator(interpolatorOn);

                        mBtnVoice.setTag(stateButton[0]);
                        mBtnVoice.setImageResource(R.drawable.ic_volume_up_black_24dp);
                        mBtnVoice.setAnimation(myAnim);
                    }
                }
            }
        }
    }
}
