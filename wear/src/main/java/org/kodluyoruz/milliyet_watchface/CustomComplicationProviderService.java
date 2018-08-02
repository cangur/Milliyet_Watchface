package org.kodluyoruz.milliyet_watchface;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;
import android.support.wearable.complications.ComplicationProviderService;
import android.support.wearable.complications.ComplicationText;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class CustomComplicationProviderService extends ComplicationProviderService {

    public static final String PREFERENCES_NAME = "ComplicationTestSuite";
    private static final String TAG = "ComplicationProvider";

    @Override
    public void onComplicationActivated(
            int complicationId, int dataType, ComplicationManager complicationManager) {
        Log.d(TAG, "onComplicationActivated(): " + complicationId);

        SharedPreferences sharedPreferences =
                this.getSharedPreferences(CustomComplicationProviderService.PREFERENCES_NAME, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("complication_id", complicationId);
        editor.apply();
    }

    @Override
    public void onComplicationUpdate(
            int complicationId, int dataType, ComplicationManager complicationManager) {
        Log.d(TAG, "onComplicationUpdate() id: " + complicationId);

        SharedPreferences sharedPreferences =
                this.getSharedPreferences(CustomComplicationProviderService.PREFERENCES_NAME, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("complication_id", complicationId);
        editor.apply();

        if (dataType != ComplicationData.TYPE_LONG_TEXT) {
            complicationManager.noUpdateRequired(complicationId);
            return;
        }

        if (dataType == ComplicationData.TYPE_LONG_TEXT) {
            SharedPreferences preferences = getSharedPreferences(CustomComplicationProviderService.PREFERENCES_NAME, 0);

            String summary = preferences.getString(DataLayerListenerService.SUMMARY_KEY, "Bir hata meydana geldi.");
            String title = preferences.getString(DataLayerListenerService.TITLE_KEY, "Error: ");
            String encodedString = preferences.getString(DataLayerListenerService.IMAGE_KEY, "");

            Bitmap bitmap = null;

            try {
                byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                ComplicationData data = new ComplicationData.Builder(dataType)
                        .setLongText(
                                ComplicationText.plainText(title))
                        .setLongTitle(
                                ComplicationText.plainText(summary))
                        .setSmallImage(Icon.createWithBitmap(bitmap))
                        .build();

                complicationManager.updateComplicationData(complicationId, data);

            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            Toast.makeText(this, "Sadece alt widget için geçerli.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onComplicationDeactivated(int complicationId) {
        Log.d(TAG, "onComplicationDeactivated(): " + complicationId);
    }

}
