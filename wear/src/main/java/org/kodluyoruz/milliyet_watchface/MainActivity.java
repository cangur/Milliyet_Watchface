package org.kodluyoruz.milliyet_watchface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView title;
    private TextView summary;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("WEAR", "ONCREATE()");

        imageView = findViewById(R.id.image_view);
        title = findViewById(R.id.title);
        summary = findViewById(R.id.summary);

        initEvent();

        // Enables Always-on
        setAmbientEnabled();
    }

    private void initEvent() {
        if (getIntent().hasExtra("bitmap")) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("bitmap"),
                    0,
                    getIntent().getByteArrayExtra("bitmap").length);
            imageView.setImageBitmap(bitmap);
            title.setText(getIntent().getStringExtra("title"));
            summary.setText(getIntent().getStringExtra("summary"));
        }
    }


}
