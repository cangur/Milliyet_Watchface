package org.kodluyoruz.milliyet_watchface;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.wearable.complications.ProviderUpdateRequester;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class DataLayerListenerService extends WearableListenerService {

    public static final String TITLE_KEY = "title";
    public static final String SUMMARY_KEY = "summary";
    public static final String IMAGE_KEY = "photo";
    public static final String ID_KEY = "id";
    private static final String TAG = "DataLayerListenerService";
    private static final String BUNDLE_PATH = "/bundle";
    private Context mContext;


    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        Log.d(TAG, "onDataChanged");

        mContext = getApplicationContext();

        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                String path = event.getDataItem().getUri().getPath();

                if (path.equalsIgnoreCase(BUNDLE_PATH)) {
                    Log.d(TAG, "BUNDLE_PATH -> 1");

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    Asset photoAsset = dataMapItem.getDataMap().getAsset("photo");

                    String title = dataMapItem.getDataMap().getString(TITLE_KEY);
                    String summary = dataMapItem.getDataMap().getString(SUMMARY_KEY);
                    String newsId = dataMapItem.getDataMap().getString(ID_KEY);
                    Bitmap bitmap = loadBitmapFromAsset(photoAsset);

                    String bitmapArray = convertBitmapToString(bitmap);

                    SharedPreferences sharedPreferences =
                            mContext.getSharedPreferences(CustomComplicationProviderService.PREFERENCES_NAME, 0);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TITLE_KEY, title);
                    editor.putString(SUMMARY_KEY, summary);
                    editor.putString(IMAGE_KEY, bitmapArray);
                    editor.putString(ID_KEY, newsId);

                    editor.apply();

                    sendDataToComplication();
                }
            }
        }
    }

    private String convertBitmapToString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); //bm is the bitmap object
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    private void sendDataToComplication() {
        SharedPreferences preferences = getSharedPreferences(CustomComplicationProviderService.PREFERENCES_NAME, 0);
        int complicationId = preferences.getInt("complication_id", 0);

        Log.d(TAG, "updated complication id: " + complicationId);

        ComponentName provider = new ComponentName("org.kodluyoruz.milliyet_watchface", "org.kodluyoruz.milliyet_watchface.CustomComplicationProviderService");
        ProviderUpdateRequester requester = new ProviderUpdateRequester(this, provider);
        requester.requestUpdate(complicationId);
    }

    private Bitmap loadBitmapFromAsset(Asset asset) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();

        InputStream ınputStream = Wearable.DataApi.getFdForAsset(googleApiClient, asset).await().getInputStream();
        googleApiClient.disconnect();

        return BitmapFactory.decodeStream(ınputStream);

    }

}
