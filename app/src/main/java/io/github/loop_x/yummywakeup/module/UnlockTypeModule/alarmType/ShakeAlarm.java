package io.github.loop_x.yummywakeup.module.UnlockTypeModule.alarmType;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import io.github.loop_x.yummywakeup.R;
import io.github.loop_x.yummywakeup.module.AlarmModule.AlarmAlertFullScreen;
import io.github.loop_x.yummywakeup.view.YummyTextView;

public class ShakeAlarm extends UnlockFragment implements SensorEventListener{

    private YummyTextView tvShakeProgress;
    private Button btnCloseAlarm;
    private ImageView ivWater;

    private OnAlarmAction mListener;
    private SensorManager mSensorManager;

    private int mShakeCount;
    private long mShakeTimestamp;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.5F;
    private static final int SHAKE_STOP_TIME_MS = 300;

    public ShakeAlarm() {}

    public static ShakeAlarm newInstance() {
        return new ShakeAlarm();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AlarmAlertFullScreen) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_unlock_shake_alarm;
    }

    @Override
    public void onViewInitial() {

        tvShakeProgress = (YummyTextView) findViewById(R.id.tv_shake_progress);
        btnCloseAlarm = (Button) findViewById(R.id.btn_shake_close_alarm);
        ivWater = (ImageView) findViewById(R.id.iv_water);

        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);

        mShakeTimestamp = System.currentTimeMillis();

        btnCloseAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.closeAlarm();
            }
        });
    }

    @Override
    public void onRefreshData() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean checkUnlockAlarm() {
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long currentTime = System.currentTimeMillis();
            // ignore shake events too close to each other (500ms)
            if (mShakeTimestamp + SHAKE_STOP_TIME_MS > currentTime ) {
                return;
            }

            mShakeTimestamp = currentTime;
            mShakeCount += (int) gForce * 2;

            TranslateAnimation animation = new TranslateAnimation(0, 0, ivWater.getY(), -100 );
            animation.setDuration(SHAKE_STOP_TIME_MS);
            animation.setFillAfter(true);
            ivWater.startAnimation(animation);

            if(mShakeCount >= 100) {
                mShakeCount = 100;
            }

            tvShakeProgress.setText(mShakeCount + "%");

            if(mShakeCount == 100) {
                btnCloseAlarm.setVisibility(View.VISIBLE);
                btnCloseAlarm.setEnabled(true);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }
}