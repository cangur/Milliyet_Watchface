package org.kodluyoruz.milliyet_watchface.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.util.Base64;
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

import org.kodluyoruz.milliyet_watchface.CustomComplicationProviderService;
import org.kodluyoruz.milliyet_watchface.DataLayerListenerService;
import org.kodluyoruz.milliyet_watchface.MyBounceInterpolator;
import org.kodluyoruz.milliyet_watchface.R;

import java.util.Date;

public class NotificationContentActivity extends WearableActivity implements View.OnClickListener, ViewTreeObserver.OnScrollChangedListener, DataClient.OnDataChangedListener {

    private final String TIME_KEY = "time";
    private final String ID_KEY = "newsID";
    private final String VOICE_KEY = "voice";
    private final String ID_PATH = "/id";
    private final String VOICE_PATH = "/voice";
    private final String TAG = "NotificationContentActivity";

    private String newsId;
    private Boolean isVoice;
    private Boolean scrollFlag = false;

    private ScrollView mScrollView;
    private TextView mTitle;
    private TextView mSummary;
    private ImageView mImageView;
    private ImageButton mVoiceImageButton;
    private ImageButton mOpenOnAppImageButton;

    private DataClient mDataClient;

    private String[] stateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationcontent_activity);

        mImageView = findViewById(R.id.notificationdesc_iv_newsImage);
        mTitle = findViewById(R.id.notificationdesc_tv_title);
        mSummary = findViewById(R.id.notificationdesc_tv_summary);
        mVoiceImageButton = findViewById(R.id.notificationdesc_btn_voice);
        mOpenOnAppImageButton = findViewById(R.id.notificationdesc_btn_openOnApp);
        mScrollView = findViewById(R.id.notification_activity_scroll_view);

        isVoice = false;

        initEvent();

        // Enables Always-on
        setAmbientEnabled();
    }

    private void initEvent() {

        mVoiceImageButton.setOnClickListener(this);
        mOpenOnAppImageButton.setOnClickListener(this);

        stateButton = new String[]{"vUp", "vDown"};
        mVoiceImageButton.setTag(stateButton[0]);

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        setWidgetInfo();
    }

    private void setWidgetInfo() {
        SharedPreferences preferences = getSharedPreferences(CustomComplicationProviderService.PREFERENCES_NAME, 0);

        String summary = preferences.getString(DataLayerListenerService.SUMMARY_KEY, "Bir hata meydana geldi.");
        String title = preferences.getString(DataLayerListenerService.TITLE_KEY, "Error: ");
        String encodedString = preferences.getString(DataLayerListenerService.IMAGE_KEY, "");
        newsId = preferences.getString(DataLayerListenerService.ID_KEY, "");
        Bitmap bitmap = null;
        Log.d(TAG, "ıd : " + newsId);

        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 400, 320, false));
            mTitle.setText(title);
            mSummary.setText(summary);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notificationdesc_btn_voice:
                final Animation myAnimOff = AnimationUtils.loadAnimation(this, R.anim.bounce);
                MyBounceInterpolator interpolatorOff = new MyBounceInterpolator(0.2, 20);
                myAnimOff.setInterpolator(interpolatorOff);

                if (mVoiceImageButton.getTag() == stateButton[0]) {
                    mVoiceImageButton.setTag(stateButton[1]);
                    mVoiceImageButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
                    mVoiceImageButton.startAnimation(myAnimOff);
                    isVoice = true;
                    sendMessage(isVoice);
                } else if (mVoiceImageButton.getTag() == stateButton[1]) {
                    mVoiceImageButton.setTag(stateButton[0]);
                    mVoiceImageButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
                    mVoiceImageButton.startAnimation(myAnimOff);
                    cutOffVoiceRequest();
                }
                break;

            case R.id.notificationdesc_btn_openOnApp:
                isVoice = false;
                showAnimation();
                sendMessage(isVoice);
                break;
        }
    }

    private void showAnimation() {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Uygulama akıllı telefondan açıldı.");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataClient = Wearable.getDataClient(this);
        mDataClient.addListener(this);
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

    private void sendMessage(Boolean isVoice) {
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
    public void onScrollChanged() {
        if (mScrollView.getScaleY() > 0.7) {
            if (!scrollFlag) {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                MyBounceInterpolator interpolatorOn = new MyBounceInterpolator(0.3, 20);
                myAnim.setInterpolator(interpolatorOn);
                mVoiceImageButton.startAnimation(myAnim);

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

                        mVoiceImageButton.setTag(stateButton[0]);
                        mVoiceImageButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
                        mVoiceImageButton.setAnimation(myAnim);
                    }
                }
            }
        }
    }
}
