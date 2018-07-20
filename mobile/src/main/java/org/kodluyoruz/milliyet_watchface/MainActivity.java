package org.kodluyoruz.milliyet_watchface;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.kodluyoruz.milliyet_watchface.api.model.Images;
import org.kodluyoruz.milliyet_watchface.api.model.SNODataClass;
import org.kodluyoruz.milliyet_watchface.api.service.SNOClient;
import org.kodluyoruz.milliyet_watchface.api.service.ServiceGenerator;
import org.kodluyoruz.milliyet_watchface.mock.MockDatabase;
import org.kodluyoruz.milliyet_watchface.util.NotificationUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final int NOTIFICATION_ID = 888;

    private static final String MESSAGE_TITLE = "title";
    private static final String MESSAGE_SUMMARY = "summary";
    private static final String BUNDLE_PATH = "/bundle";
    private static final String IMAGE_KEY = "photo";
    private static final String TIME_KEY = "time";
    private static final String NEWSID_KEY = "id";

    private Context mContext;

    private NotificationManagerCompat mNotificationManagerCompat;

    private String repoId;
    private String summary;
    private String title;
    private String imageUrl;
    private Bitmap bitmapImage;

    private TextView textTitle;
    private TextView textSummary;
    private ImageView imageView;

    private static Asset toAsset(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private Bitmap bitmapFromUrl(String imageUrl) {

        Picasso.with(mContext).load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.i(TAG, "The image was obtained correctly");
                bitmapImage = bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "The image was not obtained");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG, "Getting ready to get the image");
            }
        });

        return bitmapImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        imageView = findViewById(R.id.main_activity_imageview);
        textSummary = findViewById(R.id.summary);
        textTitle = findViewById(R.id.title);

        repoId = "39866";

        sendRequest();
    }

    private void sendRequest() {
        SNOClient client = ServiceGenerator.createService(SNOClient.class);
        Call<SNODataClass> call = client.reposForUser(repoId);

        call.enqueue(new Callback<SNODataClass>() {
            @Override
            public void onResponse(Call<SNODataClass> call, Response<SNODataClass> response) {
                SNODataClass snoDataClass = response.body();
                List<Images> imagesResult = Arrays.asList(response.body().getData().getImages());

                imageUrl = imagesResult.get(0).getBaseUrl() + imagesResult.get(0).getName();
                title = snoDataClass.getData().getTitle();
                summary = snoDataClass.getData().getSummary();

                textTitle.setText(title);
                textSummary.setText(summary);
                Picasso.with(mContext).load(imageUrl).into(imageView);

                bitmapImage = bitmapFromUrl(imageUrl);
            }

            @Override
            public void onFailure(Call<SNODataClass> call, Throwable t) {

            }
        });

//        final List<Data> lastNewsArrayList = new ArrayList<>();
//        final ArrayList<String> sendID = new ArrayList<>();
//
//        Call<DataList> repoCall = client.reposForLastNews();
//        repoCall.enqueue(new Callback<DataList>() {
//            @Override
//            public void onResponse(Call<DataList> call, Response<DataList> response) {
//                lastNewsArrayList.addAll(response.body().getData());
//                for (int i = 0; i < 25; i++) {
//                    sendID.add(i, lastNewsArrayList.get(i).getId());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataList> call, Throwable t) {
//
//            }
//        });
    }

//        final List<Data> lastNewsArrayList = new ArrayList<>();
//        final ArrayList<SendData> sendData = new ArrayList<>();
//
//        Call<DataList> repoCall = client.reposForLastNews();
//        repoCall.enqueue(new Callback<DataList>() {
//            @Override
//            public void onResponse(Call<DataList> call, Response<DataList> response) {
//                lastNewsArrayList.addAll(response.body().getData());
//                List<Images> images;
//                for (int i = 0; i < 25; i++) {
//                    images = Arrays.asList(response.body().getData().get(i).getImages());
//                    sendData.add(i, new SendData(
//                            lastNewsArrayList.get(i).getId(),
//                            lastNewsArrayList.get(i).getTitle(),
//                            lastNewsArrayList.get(i).getSummary(),
//                            images.get(0).getBaseUrl() + images.get(0).getName(),
//                            lastNewsArrayList.get(i).getContent()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataList> call, Throwable t) {
//
//            }
//        });



    private void sendData(Asset asset, String title, String summary) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(BUNDLE_PATH);
        putDataMapRequest.getDataMap().putAsset(IMAGE_KEY, asset);
        putDataMapRequest.getDataMap().putLong(TIME_KEY, new Date().getTime());
        putDataMapRequest.getDataMap().putString(MESSAGE_TITLE, title);
        putDataMapRequest.getDataMap().putString(MESSAGE_SUMMARY, summary);
        putDataMapRequest.getDataMap().putString(NEWSID_KEY, repoId);

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        putDataRequest.setUrgent();

        Task<DataItem> putDataTask = Wearable.getDataClient(this).putDataItem(putDataRequest);

        putDataTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d(TAG, "Sending bundle successful:" + dataItem);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Sending bundle was unsuccess:" + e.getMessage());
                    }
                });
    }

    public void onClick(View view) {
        boolean areNotificationsEnabled = mNotificationManagerCompat.areNotificationsEnabled();

        if (!areNotificationsEnabled) {
            openNotificationSettingsForApp();
        }

        sendData(toAsset(bitmapImage), title, summary);

        generateBigTextStyleNotification();
    }


    private void generateBigTextStyleNotification() {
        Log.d(TAG, "generateBigTextStyleNotification()");

        MockDatabase.BigTextStyleReminderAppData bigTextStyleReminderAppData = MockDatabase.getBigTextStyleData();
        String notificationChannelId = NotificationUtil.createNotificationChannel(this, bigTextStyleReminderAppData);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(bigTextStyleReminderAppData.getBigText())
                .setBigContentTitle(bigTextStyleReminderAppData.getBigContentTitle())
                .setSummaryText(bigTextStyleReminderAppData.getSummaryText());

        Intent notifyIntent = new Intent(this, BigTextMainActivity.class);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), notificationChannelId);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);


        NotificationCompat.Action mainAction = new NotificationCompat.Action.Builder(R.drawable.ic_launcher, "Open on App", notifyPendingIntent).build();

        Notification notification = notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle(bigTextStyleReminderAppData.getContentTitle())
                .setContentText(bigTextStyleReminderAppData.getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_launcher_foreground))
                .setContentIntent(notifyPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setCategory(Notification.CATEGORY_REMINDER)
                .setPriority(bigTextStyleReminderAppData.getPriority())
                .setVisibility(bigTextStyleReminderAppData.getChannelLockscreenVisibility())
                .addAction(mainAction)
                .build();

        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    private void openNotificationSettingsForApp() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        startActivity(intent);
    }
}
