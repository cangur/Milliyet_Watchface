package org.kodluyoruz.milliyet_watchface;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.kodluyoruz.milliyet_watchface.api.model.Images;
import org.kodluyoruz.milliyet_watchface.api.model.SNODataClass;
import org.kodluyoruz.milliyet_watchface.api.service.SNOClient;
import org.kodluyoruz.milliyet_watchface.api.service.ServiceGenerator;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BigTextMainActivity extends AppCompatActivity {

    private static final int RC_CHECKTTSDATA = 100;
    int result;
    private ImageView imageView;
    private TextView tvTitle;
    private TextView tvSummary;
    private TextView tvID;
    private String mID;
    private String summary;
    private String title;
    private String imageUrl;
    private Bitmap bitmapImage;
    private boolean isVoice;
    private Context mContext;
    private String TAG = "BigTextMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_big_text_main );

        NotificationManager notificationManager =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

        notificationManager.cancel( MainActivity.NOTIFICATION_ID );

        imageView = findViewById(R.id.imageView);
        tvTitle = findViewById(R.id.title);
        tvSummary = findViewById(R.id.summary);
        tvID = findViewById(R.id.mainTextView);

        mContext = getApplicationContext();

        initEvent();
    }

    private void initEvent() {
        mID = getIntent().getStringExtra(ListenerService.ID_KEY);
        isVoice = getIntent().getBooleanExtra(ListenerService.VOICE_KEY, false);

        sendRequest();
    }

    private void sendRequest() {
        SNOClient client = ServiceGenerator.createService(SNOClient.class);
        Call<SNODataClass> call = client.reposForUser(mID);

        call.enqueue(new Callback<SNODataClass>() {
            @Override
            public void onResponse(Call<SNODataClass> call, Response<SNODataClass> response) {
                SNODataClass snoDataClass = response.body();
                List<Images> imagesResult = Arrays.asList(response.body().getData().getImages());
                imageUrl = imagesResult.get(0).getBaseUrl() + imagesResult.get(0).getName();
                title = snoDataClass.getData().getTitle();
                summary = snoDataClass.getData().getSummary();

                tvTitle.setText(title);
                tvSummary.setText(summary);
                tvID.setText(mID);
                Picasso.with(mContext).load(imageUrl).into(imageView);

                bitmapImage = bitmapFromUrl(imageUrl);

                if (isVoice) {
                    String text = title + summary;
                    Intent intent = new Intent(BigTextMainActivity.this, ttsActivity.class);
                    intent.putExtra("text", text);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<SNODataClass> call, Throwable t) {

            }
        });
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
}
