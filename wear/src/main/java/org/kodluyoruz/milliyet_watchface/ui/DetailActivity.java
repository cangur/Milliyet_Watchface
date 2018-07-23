package org.kodluyoruz.milliyet_watchface.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.kodluyoruz.milliyet_watchface.R;

public class DetailActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    String name;
    GoogleApiClient googleApiClient;
    String wearable_data_path;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTextView = findViewById(R.id.activity_detail_txtTitle);
        Button btnOpenOnPhone = findViewById(R.id.activity_detail_btnOpenOnPhone);
        ImageButton imgButton = findViewById(R.id.activity_detail_imgVoice);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                //onConnected,onConnectedSuspended,onConnectionFailed metotlarını client ile ilişkilendirmek için iki satırı yazdık.
                //aski halde metotlar çalışmayacaktı ve veri senkronizasyonu gerçekleşmiyecekti.
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        btnOpenOnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Uygulama akıllı telefonda açıldı.");
                startActivity(intent);
                name = mTextView.getText().toString();
                Log.e("Tag", "dene" + name);
                wearable_data_path = "/wearable_data";
                googleApiClient.connect();

            }
        });

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = mTextView.getText().toString();
                wearable_data_path = "/wearable_data";
                googleApiClient.connect();
            }
        });
        // Enables Always-on
        setAmbientEnabled();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            //Saate göndermek istediğimiz verileri ekliyoruz.
            DataMap dataMap = new DataMap();
            dataMap.putString("name", name);
            Log.e("Tag", "deneme" + name);
            //Datamap oluştuktan sonra path ini belirtiyoruz.
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(wearable_data_path).setUrgent();
            putDataMapRequest.getDataMap().putAll(dataMap);

            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            //istek ağa gönderilir.
            Wearable.DataApi.putDataItem(googleApiClient, request);
            googleApiClient.disconnect();

        } catch (Exception exp) {

        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
