package org.kodluyoruz.milliyet_watchface;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.kodluyoruz.milliyet_watchface.mock.MockDatabase;
import org.kodluyoruz.milliyet_watchface.util.NotificationUtil;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final int NOTIFICATION_ID = 888;

    private NotificationManagerCompat mNotificationManagerCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mNotificationManagerCompat = NotificationManagerCompat.from( getApplicationContext() );
    }

    public void onClick(View view) {
        boolean areNotificationsEnabled = mNotificationManagerCompat.areNotificationsEnabled();

        // Open notification in Settings
        if (!areNotificationsEnabled) {
            openNotificationSettingsForApp();
        }

        generateBigTextStyleNotification();
    }

    private void generateBigTextStyleNotification() {
        Log.d( TAG, "generateBigTextStyleNotification()" );

        MockDatabase.BigTextStyleReminderAppData bigTextStyleReminderAppData = MockDatabase.getBigTextStyleData();
        String notificationChannelId = NotificationUtil.createNotificationChannel( this, bigTextStyleReminderAppData );

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText( bigTextStyleReminderAppData.getBigText() )
                .setBigContentTitle( bigTextStyleReminderAppData.getBigContentTitle() )
                .setSummaryText( bigTextStyleReminderAppData.getSummaryText() );

        Intent notifyIntent = new Intent( this, BigTextMainActivity.class );

        notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), notificationChannelId );

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance( notificationCompatBuilder );


        NotificationCompat.Action mainAction = new NotificationCompat.Action.Builder( R.drawable.ic_launcher, "Open on App", notifyPendingIntent ).build();

        Notification notification = notificationCompatBuilder
                .setStyle( bigTextStyle )
                .setContentTitle( bigTextStyleReminderAppData.getContentTitle() )
                .setContentText( bigTextStyleReminderAppData.getContentText() )
                .setSmallIcon( R.drawable.ic_launcher )
                .setLargeIcon( BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_launcher_foreground ) )
                .setContentIntent( notifyPendingIntent )
                .setDefaults( NotificationCompat.DEFAULT_ALL )
                .setColor( ContextCompat.getColor( getApplicationContext(), R.color.colorPrimary ) )
                .setCategory( Notification.CATEGORY_REMINDER )
                .setPriority( bigTextStyleReminderAppData.getPriority() )
                .setVisibility( bigTextStyleReminderAppData.getChannelLockscreenVisibility() )
                .addAction( mainAction )
                .build();

        mNotificationManagerCompat.notify( NOTIFICATION_ID, notification );


    }

    private void openNotificationSettingsForApp() {
        Intent intent = new Intent();
        intent.setAction( "android.settings.APP_NOTIFICATION_SETTINGS" );
        intent.putExtra( "app_package", getPackageName() );
        intent.putExtra( "app_uid", getApplicationInfo().uid );
        startActivity( intent );
    }
}
