package org.kodluyoruz.milliyet_watchface;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.concurrent.Executors;

public class ComplicationConfigActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ConfigActivity";

    static final int COMPLICATION_CONFIG_REQUEST_CODE = 1001;

    private int mTopComplicationId;

    private int mLeftComplicationId;
    private int mRightComplicationId;
    private int mBottomComplicationId;
    private ImageView mTopComplicationBackground;

    // Selected complication id by user.
    private int mSelectedComplicationId;

    // ComponentName used to identify a specific service that renders the watch face.
    private ComponentName mWatchFaceComponentName;

    // Required to retrieve complication data from watch face for preview.
    private ProviderInfoRetriever mProviderInfoRetriever;

    private ImageView mLeftComplicationBackground;
    private ImageView mRightComplicationBackground;
    private ImageView mBottomComplicationBackground;
    private ImageButton mTopComplication;

    private ImageButton mLeftComplication;
    private ImageButton mRightComplication;
    private ImageButton mBottomComplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_complication_config );

        mDefaultAddComplicationDrawable = getDrawable( R.drawable.add_complication );

        mSelectedComplicationId = -1;

        mLeftComplicationId =
                MyWatchFace.getComplicationId( ComplicationLocation.LEFT );
        mRightComplicationId =
                MyWatchFace.getComplicationId( ComplicationLocation.RIGHT );
        mTopComplicationId =
                MyWatchFace.getComplicationId( ComplicationLocation.TOP );
        mBottomComplicationId =
                MyWatchFace.getComplicationId( ComplicationLocation.BOTTOM );

        mWatchFaceComponentName =
                new ComponentName( getApplicationContext(), MyWatchFace.class );

        // Sets up left complication preview.
        mLeftComplicationBackground = findViewById( R.id.left_complication_background );
        mLeftComplication = findViewById( R.id.left_complication );
        mLeftComplication.setOnClickListener( this );

        // Sets default as "Add Complication" icon.
        mLeftComplication.setImageDrawable( mDefaultAddComplicationDrawable );
        mLeftComplicationBackground.setVisibility( View.INVISIBLE );

        // Sets up right complication preview.
        mRightComplicationBackground = findViewById( R.id.right_complication_background );
        mRightComplication = findViewById( R.id.right_complication );
        mRightComplication.setOnClickListener( this );

        // Sets default as "Add Complication" icon.
        mRightComplication.setImageDrawable( mDefaultAddComplicationDrawable );
        mRightComplicationBackground.setVisibility( View.INVISIBLE );

        // Set Top
        mTopComplicationBackground = findViewById( R.id.top_complication_background );
        mTopComplication = findViewById( R.id.top_complication );
        mTopComplication.setOnClickListener( this );

        mTopComplication.setImageDrawable( mDefaultAddComplicationDrawable );
        mTopComplicationBackground.setVisibility( View.INVISIBLE );

        // Set Bottom
        mBottomComplicationBackground = findViewById( R.id.bottom_complication_background );
        mBottomComplication = findViewById( R.id.bottom_complication );
        mBottomComplication.setOnClickListener( this );

        mBottomComplication.setImageDrawable( mDefaultAddComplicationDrawable );
        mBottomComplicationBackground.setVisibility( View.INVISIBLE );

        mProviderInfoRetriever =
                new ProviderInfoRetriever( getApplicationContext(), Executors.newCachedThreadPool() );
        mProviderInfoRetriever.init();

        retrieveInitialComplicationsData();
    }

    private Drawable mDefaultAddComplicationDrawable;

    @Override
    public void onClick(View view) {
        if (view.equals( mLeftComplication )) {
            Log.d( TAG, "Left Complication click()" );
            launchComplicationHelperActivity( ComplicationLocation.LEFT );

        } else if (view.equals( mRightComplication )) {
            Log.d( TAG, "Right Complication click()" );
            launchComplicationHelperActivity( ComplicationLocation.RIGHT );
        } else if (view.equals( mTopComplication )) {
            Log.d( TAG, "Top Complication click()" );
            launchComplicationHelperActivity( ComplicationLocation.TOP );
        } else if (view.equals( mBottomComplication )) {
            Log.d( TAG, "Bottom Complication click()" );
            launchComplicationHelperActivity( ComplicationLocation.BOTTOM );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mProviderInfoRetriever.release();
    }

    public void retrieveInitialComplicationsData() {

        final int[] complicationIds = MyWatchFace.getComplicationIds();

        mProviderInfoRetriever.retrieveProviderInfo(
                new ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
                    @Override
                    public void onProviderInfoReceived(
                            int watchFaceComplicationId,
                            @Nullable ComplicationProviderInfo complicationProviderInfo) {

                        Log.d( TAG, "onProviderInfoReceived: " + complicationProviderInfo );

                        updateComplicationViews( watchFaceComplicationId, complicationProviderInfo );
                    }
                },
                mWatchFaceComponentName,
                complicationIds );
    }

    public void updateComplicationViews(
            int watchFaceComplicationId, ComplicationProviderInfo complicationProviderInfo) {
        Log.d( TAG, "updateComplicationViews(): id: " + watchFaceComplicationId );
        Log.d( TAG, "\tinfo: " + complicationProviderInfo );

        if (watchFaceComplicationId == mLeftComplicationId) {
            if (complicationProviderInfo != null) {
                mLeftComplication.setImageIcon( complicationProviderInfo.providerIcon );
                mLeftComplicationBackground.setVisibility( View.VISIBLE );

            } else {
                mLeftComplication.setImageDrawable( mDefaultAddComplicationDrawable );
                mLeftComplicationBackground.setVisibility( View.INVISIBLE );
            }

        } else if (watchFaceComplicationId == mRightComplicationId) {
            if (complicationProviderInfo != null) {
                mRightComplication.setImageIcon( complicationProviderInfo.providerIcon );
                mRightComplicationBackground.setVisibility( View.VISIBLE );

            } else {
                mRightComplication.setImageDrawable( mDefaultAddComplicationDrawable );
                mRightComplicationBackground.setVisibility( View.INVISIBLE );
            }
        } else if (watchFaceComplicationId == mTopComplicationId) {
            if (complicationProviderInfo != null) {
                mTopComplication.setImageIcon( complicationProviderInfo.providerIcon );
                mTopComplicationBackground.setVisibility( View.VISIBLE );

            } else {
                mTopComplication.setImageDrawable( mDefaultAddComplicationDrawable );
                mTopComplicationBackground.setVisibility( View.INVISIBLE );
            }
        } else if (watchFaceComplicationId == mBottomComplicationId) {
            if (complicationProviderInfo != null) {
                mBottomComplication.setImageIcon( complicationProviderInfo.providerIcon );
                mBottomComplicationBackground.setVisibility( View.VISIBLE );

            } else {
                mBottomComplication.setImageDrawable( mDefaultAddComplicationDrawable );
                mBottomComplicationBackground.setVisibility( View.INVISIBLE );
            }
        }
    }

    // Verifies the watch face supports the complication location, then launches the helper
    // class, so user can choose their complication data provider.
    private void launchComplicationHelperActivity(ComplicationLocation complicationLocation) {

        mSelectedComplicationId =
                MyWatchFace.getComplicationId( complicationLocation );

        if (mSelectedComplicationId >= 0) {

            int[] supportedTypes =
                    MyWatchFace.getSupportedComplicationTypes(
                            complicationLocation );

            startActivityForResult(
                    ComplicationHelperActivity.createProviderChooserHelperIntent(
                            getApplicationContext(),
                            mWatchFaceComponentName,
                            mSelectedComplicationId,
                            supportedTypes ),
                    ComplicationConfigActivity.COMPLICATION_CONFIG_REQUEST_CODE );

        } else {
            Log.d( TAG, "Complication not supported by watch face." );
        }
    }


    public enum ComplicationLocation {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == COMPLICATION_CONFIG_REQUEST_CODE && resultCode == RESULT_OK) {

            // Retrieves information for selected Complication provider.
            ComplicationProviderInfo complicationProviderInfo =
                    data.getParcelableExtra( ProviderChooserIntent.EXTRA_PROVIDER_INFO );
            Log.d( TAG, "Provider: " + complicationProviderInfo );

            if (mSelectedComplicationId >= 0) {
                updateComplicationViews( mSelectedComplicationId, complicationProviderInfo );
            }
        }
    }
}