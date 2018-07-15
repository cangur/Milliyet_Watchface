package org.kodluyoruz.milliyet_watchface;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.rendering.ComplicationDrawable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class MyWatchFace extends CanvasWatchFaceService {

    private static final int LEFT_COMPLICATION_ID = 0;
    private static final int RIGHT_COMPLICATION_ID = 1;
    private static final int TOP_COMPLICATION_ID = 2;
    private static final int BOTTOM_COMPLICATION_ID = 3;

    private static final int[] COMPLICATION_IDS = {LEFT_COMPLICATION_ID, RIGHT_COMPLICATION_ID, TOP_COMPLICATION_ID, BOTTOM_COMPLICATION_ID};

    // Left and right dial supported types.
    private static final int[][] COMPLICATION_SUPPORTED_TYPES = {
            {
                    ComplicationData.TYPE_RANGED_VALUE,
                    ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE,
                    ComplicationData.TYPE_LONG_TEXT
            },
            {
                    ComplicationData.TYPE_RANGED_VALUE,
                    ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE,
                    ComplicationData.TYPE_LONG_TEXT
            }
    };

    // Used by {@link ComplicationConfigActivity} to retrieve id for complication locations and
    // to check if complication location is supported.
    static int getComplicationId(
            ComplicationConfigActivity.ComplicationLocation complicationLocation) {
        switch (complicationLocation) {
            case LEFT:
                return LEFT_COMPLICATION_ID;
            case RIGHT:
                return RIGHT_COMPLICATION_ID;
            case TOP:
                return TOP_COMPLICATION_ID;
            case BOTTOM:
                return BOTTOM_COMPLICATION_ID;
            default:
                return -1;
        }
    }

    // Used by {@link ComplicationConfigActivity} to retrieve all complication ids.
    static int[] getComplicationIds() {
        return COMPLICATION_IDS;
    }

    // Used by {@link ComplicationConfigActivity} to retrieve complication types supported by
    // location.
    static int[] getSupportedComplicationTypes(
            ComplicationConfigActivity.ComplicationLocation complicationLocation) {
        switch (complicationLocation) {
            case LEFT:
                return COMPLICATION_SUPPORTED_TYPES[0];
            case RIGHT:
                return COMPLICATION_SUPPORTED_TYPES[1];
            case TOP:
                return COMPLICATION_SUPPORTED_TYPES[1];
            case BOTTOM:
                return COMPLICATION_SUPPORTED_TYPES[1];
            default:
                return new int[]{};
        }
    }

    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis( 1 );

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;
    private static final String TAG = "Milliyet Watch Face";

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>( reference );
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private static final float HOUR_STROKE_WIDTH = 5f;
        private static final float MINUTE_STROKE_WIDTH = 3f;
        private static final float SECOND_TICK_STROKE_WIDTH = 2f;

        private static final float CENTER_GAP_AND_CIRCLE_RADIUS = 4f;

        private static final int SHADOW_RADIUS = 6;

        private final float LONG_TEXT_COMPLICATION_RADIUS = 7f;

        /* Handler to update the time once a second in interactive mode. */
        private final Handler mUpdateTimeHandler = new EngineHandler( this );

        private Calendar mCalendar;

        private SparseArray<ComplicationData> mActiveComplicationDataSparseArray;
        private SparseArray<ComplicationDrawable> mComplicationDrawableSparseArray;

        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone( TimeZone.getDefault() );
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        private float mCenterX;
        private float mCenterY;
        private float mSecondHandLength;
        private float sMinuteHandLength;
        private float sHourHandLength;
        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        private int mWatchHandColor;
        private int mWatchHandHighlightColor;
        private int mWatchHandShadowColor;
        private Paint mHourPaint;
        private Paint mMinutePaint;
        private Paint mSecondPaint;
        private Paint mTickAndCirclePaint;
        private Paint mBackgroundPaint;
        private Bitmap mBackgroundBitmap;
        private Bitmap mGrayBackgroundBitmap;
        private boolean mAmbient;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;

        private boolean mUnreadNotificationsPreference;
        private int mNumberOfUnreadNotifications = 0;
        SharedPreferences mSharedPref;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate( holder );

            setWatchFaceStyle( new WatchFaceStyle.Builder( MyWatchFace.this )
                    .setStatusBarGravity( Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL
                            | Gravity.TOP )
                    .setViewProtectionMode( WatchFaceStyle.PROTECT_STATUS_BAR
                            | WatchFaceStyle.PROTECT_HOTWORD_INDICATOR )
                    .setHideNotificationIndicator( true )
                    .setAcceptsTapEvents( true )
                    .build() );

            mCalendar = Calendar.getInstance();

            Context context = getApplicationContext();
            mSharedPref =
                    context.getSharedPreferences(
                            getString( R.string.analog_complication_preference_file_key ),
                            Context.MODE_PRIVATE );

            loadSavedPreferences();
            initializeBackground();
            initializeWatchFace();
            initializeComplications();

        }

        private void loadSavedPreferences() {
            String unreadNotificationPreferenceResourceName =
                    getApplicationContext().getString( R.string.saved_unread_notifications_pref );

            mUnreadNotificationsPreference =
                    mSharedPref.getBoolean( unreadNotificationPreferenceResourceName, true );
        }

        private void initializeComplications() {
            Log.d( TAG, "initializeComplications()" );

            mActiveComplicationDataSparseArray = new SparseArray<>( COMPLICATION_IDS.length );

            ComplicationDrawable leftComplicationDrawable =
                    (ComplicationDrawable) getDrawable( R.drawable.custom_complication_styles );
            if (leftComplicationDrawable != null) {
                leftComplicationDrawable.setContext( getApplicationContext() );
            }

            ComplicationDrawable rightComplicationDrawable =
                    (ComplicationDrawable) getDrawable( R.drawable.custom_complication_styles );
            if (rightComplicationDrawable != null) {
                rightComplicationDrawable.setContext( getApplicationContext() );
            }

            ComplicationDrawable topComplicationDrawable =
                    (ComplicationDrawable) getDrawable( R.drawable.custom_complication_styles );
            if (topComplicationDrawable != null) {
                topComplicationDrawable.setContext( getApplicationContext() );
            }

            ComplicationDrawable bottomComplicationDrawable =
                    (ComplicationDrawable) getDrawable( R.drawable.custom_complication_styles );
            if (bottomComplicationDrawable != null) {
                bottomComplicationDrawable.setContext( getApplicationContext() );
            }

            mComplicationDrawableSparseArray = new SparseArray<>( COMPLICATION_IDS.length );
            mComplicationDrawableSparseArray.put( LEFT_COMPLICATION_ID, leftComplicationDrawable );
            mComplicationDrawableSparseArray.put( RIGHT_COMPLICATION_ID, rightComplicationDrawable );
            mComplicationDrawableSparseArray.put( TOP_COMPLICATION_ID, topComplicationDrawable );
            mComplicationDrawableSparseArray.put( BOTTOM_COMPLICATION_ID, bottomComplicationDrawable );

            setActiveComplications( COMPLICATION_IDS );
        }

        private void initializeBackground() {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor( Color.BLACK );
            mBackgroundBitmap = BitmapFactory.decodeResource( getResources(), R.drawable.milliyet_background );

            /* Extracts colors from background image to improve watchface style. */
        }

        private void initializeWatchFace() {
            /* Set defaults for colors */
            mWatchHandColor = Color.WHITE;
            mWatchHandHighlightColor = Color.RED;
            mWatchHandShadowColor = Color.BLACK;

            mHourPaint = new Paint();
            mHourPaint.setColor( mWatchHandColor );
            mHourPaint.setStrokeWidth( HOUR_STROKE_WIDTH );
            mHourPaint.setAntiAlias( true );
            mHourPaint.setStrokeCap( Paint.Cap.ROUND );
            mHourPaint.setShadowLayer( SHADOW_RADIUS, 1, 1, mWatchHandShadowColor );
            mHourPaint.setStyle( Paint.Style.STROKE );

            mMinutePaint = new Paint();
            mMinutePaint.setColor( mWatchHandColor );
            mMinutePaint.setStrokeWidth( MINUTE_STROKE_WIDTH );
            mMinutePaint.setAntiAlias( true );
            mMinutePaint.setStrokeCap( Paint.Cap.ROUND );
            mMinutePaint.setShadowLayer( SHADOW_RADIUS, 1, 1, mWatchHandShadowColor );

            mSecondPaint = new Paint();
            mSecondPaint.setColor( mWatchHandHighlightColor );
            mSecondPaint.setStrokeWidth( SECOND_TICK_STROKE_WIDTH );
            mSecondPaint.setAntiAlias( true );
            mSecondPaint.setStrokeCap( Paint.Cap.ROUND );
            mSecondPaint.setShadowLayer( SHADOW_RADIUS, 1, 1, mWatchHandShadowColor );

            mTickAndCirclePaint = new Paint();
            mTickAndCirclePaint.setColor( mWatchHandColor );
            mTickAndCirclePaint.setStrokeWidth( SECOND_TICK_STROKE_WIDTH );
            mTickAndCirclePaint.setAntiAlias( true );
            mTickAndCirclePaint.setStyle( Paint.Style.STROKE );
            mTickAndCirclePaint.setShadowLayer( SHADOW_RADIUS, 0, 0, mWatchHandShadowColor );
        }

        @Override
        public void onComplicationDataUpdate(int complicationId, ComplicationData complicationData) {
            Log.d( TAG, "onComplicationDataUpdate() id: " + complicationId );

            // Adds/updates active complication data in the array.
            mActiveComplicationDataSparseArray.put( complicationId, complicationData );

            // Updates correct ComplicationDrawable with updated data.
            ComplicationDrawable complicationDrawable =
                    mComplicationDrawableSparseArray.get( complicationId );
            complicationDrawable.setComplicationData( complicationData );

            invalidate();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages( MSG_UPDATE_TIME );
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged( properties );
            mLowBitAmbient = properties.getBoolean( PROPERTY_LOW_BIT_AMBIENT, false );
            mBurnInProtection = properties.getBoolean( PROPERTY_BURN_IN_PROTECTION, false );
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged( inAmbientMode );
            mAmbient = inAmbientMode;

            updateWatchHandStyle();

            ComplicationDrawable complicationDrawable;

            for (int COMPLICATION_ID : COMPLICATION_IDS) {
                complicationDrawable = mComplicationDrawableSparseArray.get( COMPLICATION_ID );
                complicationDrawable.setInAmbientMode( mAmbient );
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void updateWatchHandStyle() {
            if (mAmbient) {
                mHourPaint.setColor( Color.WHITE );
                mMinutePaint.setColor( Color.WHITE );
                mSecondPaint.setColor( Color.WHITE );
                mTickAndCirclePaint.setColor( Color.WHITE );

                mHourPaint.setAntiAlias( false );
                mMinutePaint.setAntiAlias( false );
                mSecondPaint.setAntiAlias( false );
                mTickAndCirclePaint.setAntiAlias( false );

                mHourPaint.clearShadowLayer();
                mMinutePaint.clearShadowLayer();
                mSecondPaint.clearShadowLayer();
                mTickAndCirclePaint.clearShadowLayer();

            } else {
                mHourPaint.setColor( mWatchHandColor );
                mMinutePaint.setColor( mWatchHandColor );
                mSecondPaint.setColor( mWatchHandHighlightColor );
                mTickAndCirclePaint.setColor( mWatchHandColor );

                mHourPaint.setAntiAlias( true );
                mMinutePaint.setAntiAlias( true );
                mSecondPaint.setAntiAlias( true );
                mTickAndCirclePaint.setAntiAlias( true );

                mHourPaint.setShadowLayer( SHADOW_RADIUS, 0, 0, mWatchHandShadowColor );
                mMinutePaint.setShadowLayer( SHADOW_RADIUS, 0, 0, mWatchHandShadowColor );
                mSecondPaint.setShadowLayer( SHADOW_RADIUS, 0, 0, mWatchHandShadowColor );
                mTickAndCirclePaint.setShadowLayer( SHADOW_RADIUS, 0, 0, mWatchHandShadowColor );
            }
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged( interruptionFilter );
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);

            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode;
                mHourPaint.setAlpha( inMuteMode ? 100 : 255 );
                mMinutePaint.setAlpha( inMuteMode ? 100 : 255 );
                mSecondPaint.setAlpha( inMuteMode ? 80 : 255 );
                invalidate();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged( holder, format, width, height );

            mCenterX = width / 2f;
            mCenterY = height / 2f;

            mSecondHandLength = (float) (mCenterX * 0.875);
            sMinuteHandLength = (float) (mCenterX * 0.75);
            sHourHandLength = (float) (mCenterX * 0.5);


            /* Scale loaded background image (more efficient) if surface dimensions change. */
            float scale_width = ((float) width) / (float) mBackgroundBitmap.getWidth();
            float scale_height = ((float) height) / (float) mBackgroundBitmap.getHeight();

            mBackgroundBitmap = Bitmap.createScaledBitmap( mBackgroundBitmap,
                    (int) (mBackgroundBitmap.getWidth() * scale_width),
                    (int) (mBackgroundBitmap.getHeight() * scale_height), true );

            if (!mBurnInProtection && !mLowBitAmbient) {
                initGrayBackgroundBitmap();
            }

            int sizeOfComplication = width / 5;
            int midpointOfScreen = width / 2;

            int horizontalOffset = (midpointOfScreen - sizeOfComplication) / 2;
            int verticalOffset = midpointOfScreen - (sizeOfComplication / 2);

            Rect leftBounds =

                    new Rect(
                            horizontalOffset,
                            verticalOffset,
                            (horizontalOffset + sizeOfComplication),
                            (verticalOffset + sizeOfComplication) );

            final ComplicationDrawable leftComplicationDrawable =
                    mComplicationDrawableSparseArray.get( LEFT_COMPLICATION_ID );
            leftComplicationDrawable.setBounds( leftBounds );

            Rect rightBounds =
                    // Left, Top, Right, Bottom
                    new Rect(
                            (midpointOfScreen + horizontalOffset),
                            verticalOffset,
                            (midpointOfScreen + horizontalOffset + sizeOfComplication),
                            (verticalOffset + sizeOfComplication) );

            final ComplicationDrawable rightComplicationDrawable =
                    mComplicationDrawableSparseArray.get( RIGHT_COMPLICATION_ID );
            rightComplicationDrawable.setBounds( rightBounds );

            final float offset = 6f; //offset for TOP & BOTTOM complications

            final Rect topBounds =
                    new Rect(
                            verticalOffset,
                            horizontalOffset,
                            verticalOffset + sizeOfComplication,
                            horizontalOffset + sizeOfComplication
                    );
            final ComplicationDrawable topComplicationDrawable =
                    mComplicationDrawableSparseArray.get( TOP_COMPLICATION_ID );
            topComplicationDrawable.setBounds( topBounds );

            final Rect bottomBounds = createComplicationRect( mCenterX, mCenterY * 1.5f + offset,
                    LONG_TEXT_COMPLICATION_RADIUS );
            final ComplicationDrawable bottomComplicationDrawable =
                    mComplicationDrawableSparseArray.get( BOTTOM_COMPLICATION_ID );
            bottomComplicationDrawable.setBounds( bottomBounds );
        }

        private Rect createComplicationRect(float centerX, float centerY, float desiredRadius) {
            final int radius = Math.round( mCenterX / desiredRadius );

            final int centerXInt = Math.round( centerX );
            final int centerYInt = Math.round( centerY );

            //creates the width to the Rect
            final int magicNumber = Math.round( scalePosition( mCenterX, 4f ) );

            return new Rect( centerXInt - radius - magicNumber,
                    centerYInt - radius,
                    centerXInt + radius + magicNumber,
                    centerYInt + radius );
        }

        private float scalePosition(float centerCoord, float scale) {
            return ((centerCoord * 2) / scale);
        }

        private void initGrayBackgroundBitmap() {
            mGrayBackgroundBitmap = Bitmap.createBitmap(
                    mBackgroundBitmap.getWidth(),
                    mBackgroundBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888 );
            Canvas canvas = new Canvas( mGrayBackgroundBitmap );
            Paint grayPaint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation( 0 );
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter( colorMatrix );
            grayPaint.setColorFilter( filter );
            canvas.drawBitmap( mBackgroundBitmap, 0, 0, grayPaint );
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:

                    break;
                case TAP_TYPE_TOUCH_CANCEL:

                    break;
                case TAP_TYPE_TAP:
                    int tappedComplicationId = getTappedComplicationId( x, y );
                    if (tappedComplicationId != -1) {
                        onComplicationTap( tappedComplicationId );
                    }
                    break;
            }
            invalidate();
        }

        @Override
        public void onUnreadCountChanged(int count) {
            Log.d( TAG, "onUnreadCountChanged(): " + count );

            if (mUnreadNotificationsPreference) {

                if (mNumberOfUnreadNotifications != count) {
                    mNumberOfUnreadNotifications = count;
                    invalidate();
                }
            }
        }

        private int getTappedComplicationId(int x, int y) {
            int complicationId;
            ComplicationData complicationData;
            ComplicationDrawable complicationDrawable;

            long currentTimeMillis = System.currentTimeMillis();

            for (int COMPLICATION_ID : COMPLICATION_IDS) {
                complicationId = COMPLICATION_ID;
                complicationData = mActiveComplicationDataSparseArray.get( complicationId );

                if ((complicationData != null)
                        && (complicationData.isActive( currentTimeMillis ))
                        && (complicationData.getType() != ComplicationData.TYPE_NOT_CONFIGURED)
                        && (complicationData.getType() != ComplicationData.TYPE_EMPTY)) {

                    complicationDrawable = mComplicationDrawableSparseArray.get( complicationId );
                    Rect complicationBoundingRect = complicationDrawable.getBounds();

                    if (complicationBoundingRect.width() > 0) {
                        if (complicationBoundingRect.contains( x, y )) {
                            return complicationId;
                        }
                    } else {
                        Log.e( TAG, "Not a recognized complication id." );
                    }
                }
            }
            return -1;
        }

        private void onComplicationTap(int complicationId) {
            Log.d( TAG, "onComplicationTap()" );

            ComplicationData complicationData =
                    mActiveComplicationDataSparseArray.get( complicationId );

            if (complicationData != null) {

                if (complicationData.getTapAction() != null) {
                    try {
                        complicationData.getTapAction().send();
                    } catch (PendingIntent.CanceledException e) {
                        Log.e( TAG, "onComplicationTap() tap action error: " + e );
                    }

                } else if (complicationData.getType() == ComplicationData.TYPE_NO_PERMISSION) {

                    // Watch face does not have permission to receive complication data, so launch
                    // permission request.
                    ComponentName componentName = new ComponentName(
                            getApplicationContext(),
                            MyWatchFace.class );

                    Intent permissionRequestIntent =
                            ComplicationHelperActivity.createPermissionRequestHelperIntent(
                                    getApplicationContext(), componentName );

                    startActivity( permissionRequestIntent );
                }

            } else {
                Log.d( TAG, "No PendingIntent for complication " + complicationId + "." );
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis( now );

            drawBackground( canvas );
            drawComplications( canvas, now );
            drawWatchFace( canvas );
            drawUnreadNotificationIcon( canvas );

        }

        private void drawUnreadNotificationIcon(Canvas canvas) {
            if (mUnreadNotificationsPreference && (mNumberOfUnreadNotifications > 0)) {
                int width = canvas.getWidth();
                int height = canvas.getHeight();

                canvas.drawCircle( width / 2, height - 40, 10, mTickAndCirclePaint );

                if (!mAmbient) {
                    Paint mSecondAndHighlightPaint = new Paint();
                    mSecondAndHighlightPaint.setColor( getResources().getColor( R.color.white, getTheme() ) );
                    canvas.drawCircle( width / 2, height - 40, 4, mSecondAndHighlightPaint );
                }
            }
        }

        private void drawComplications(Canvas canvas, long currentTimeMillis) {
            int complicationId;
            ComplicationDrawable complicationDrawable;

            for (int COMPLICATION_ID : COMPLICATION_IDS) {
                complicationId = COMPLICATION_ID;
                complicationDrawable = mComplicationDrawableSparseArray.get( complicationId );

                complicationDrawable.draw( canvas, currentTimeMillis );
            }
        }

        private void drawBackground(Canvas canvas) {

            if (mAmbient && (mLowBitAmbient || mBurnInProtection)) {
                canvas.drawColor( Color.BLACK );
            } else if (mAmbient) {
                canvas.drawBitmap( mGrayBackgroundBitmap, 0, 0, mBackgroundPaint );
            } else {
                canvas.drawBitmap( mBackgroundBitmap, 0, 0, mBackgroundPaint );
            }
        }

        private void drawWatchFace(Canvas canvas) {

            float innerTickRadius = mCenterX - 10;
            float outerTickRadius = mCenterX;
            for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
                float tickRot  = (float) (tickIndex * Math.PI * 2 / 12);
                float innerX = (float) Math.sin( tickRot ) * innerTickRadius;
                float innerY = (float) -Math.cos( tickRot ) * innerTickRadius;
                float outerX = (float) Math.sin( tickRot ) * outerTickRadius;
                float outerY = (float) -Math.cos( tickRot ) * outerTickRadius;
                canvas.drawLine( mCenterX + innerX, mCenterY + innerY,
                        mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint );
            }

            final float seconds =
                    (mCalendar.get( Calendar.SECOND ) + mCalendar.get( Calendar.MILLISECOND ) / 1000f);
            final float secondsRotation = seconds * 6f;

            final float minutesRotation = mCalendar.get( Calendar.MINUTE ) * 6f;

            final float hourHandOffset = mCalendar.get( Calendar.MINUTE ) / 2f;
            final float hoursRotation = (mCalendar.get( Calendar.HOUR ) * 30) + hourHandOffset;

            canvas.save();

            canvas.rotate( hoursRotation, mCenterX, mCenterY );
            canvas.drawRoundRect(
                    mCenterX - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterY - sHourHandLength,
                    mCenterX + CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterY + CENTER_GAP_AND_CIRCLE_RADIUS,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    mHourPaint );

            canvas.rotate( minutesRotation - hoursRotation, mCenterX, mCenterY );
            canvas.drawRoundRect(
                    mCenterX - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterY - sMinuteHandLength,
                    mCenterX + CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterY + CENTER_GAP_AND_CIRCLE_RADIUS,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    mHourPaint );

            canvas.drawCircle(
                    mCenterX,
                    mCenterY,
                    CENTER_GAP_AND_CIRCLE_RADIUS + 2,
                    mTickAndCirclePaint );

            if (!mAmbient) {
                canvas.rotate( secondsRotation - minutesRotation, mCenterX, mCenterY );
                canvas.drawLine(
                        mCenterX,
                        mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
                        mCenterX,
                        mCenterY - mSecondHandLength,
                        mSecondPaint );

            }

            canvas.drawCircle(
                    mCenterX,
                    mCenterY,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    mSecondPaint );

            canvas.restore();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged( visible );

            if (visible) {
                registerReceiver();

                mCalendar.setTimeZone( TimeZone.getDefault() );
                invalidate();
            } else {
                unregisterReceiver();
            }

            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter( Intent.ACTION_TIMEZONE_CHANGED );
            MyWatchFace.this.registerReceiver( mTimeZoneReceiver, filter );
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver( mTimeZoneReceiver );
        }

        /**
         * Starts/stops the {@link #mUpdateTimeHandler} timer based on the state of the watch face.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages( MSG_UPDATE_TIME );
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage( MSG_UPDATE_TIME );
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed( MSG_UPDATE_TIME, delayMs );
            }
        }
    }
}
