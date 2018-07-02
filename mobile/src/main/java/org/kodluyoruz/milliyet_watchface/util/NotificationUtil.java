package org.kodluyoruz.milliyet_watchface.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import org.kodluyoruz.milliyet_watchface.mock.MockDatabase;

public class NotificationUtil {
    public static String createNotificationChannel(Context context, MockDatabase.MockNotificationData mockNotificationData) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = mockNotificationData.getChannelId();
            CharSequence channelName = mockNotificationData.getChannelName();
            String channelDescription = mockNotificationData.getChannelDescription();
            int channelImportance = mockNotificationData.getChannelImportance();
            boolean channelEnableVibrate = mockNotificationData.isChannelEnableVibrate();
            int channelLookscreenVisibility = mockNotificationData.getChannelLockscreenVisibility();

            NotificationChannel notificationChannel =
                    new NotificationChannel( channelId, channelName, channelImportance );
            notificationChannel.setDescription( channelDescription );
            notificationChannel.enableVibration( channelEnableVibrate );
            notificationChannel.setLockscreenVisibility( channelLookscreenVisibility );

            NotificationManager notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel( notificationChannel );
            }

            return channelId;
        } else {
            return null;
        }
    }
}
