package com.mooc.commonbusiness.manager;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.mooc.common.ktextends.AnyExtentionKt;

/**
 * 新的摇一摇反馈算法
 * （和军职老版本同步）
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ShakeDetector2 implements SensorEventListener {
    private static final String TAG = "ShakeDetector";
    /**
     * 检测的时间间隔
     */
    static final int UPDATE_INTERVAL = 100;
    /**
     * 上一次检测的时间
     */
    long mLastUpdateTime;
    /**
     * 上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
     */
    float mLastX, mLastY, mLastZ;

    /**
     * 摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。
     */
    public int shakeThreshold = 4000;


    public interface ShakeListener {
        void onShake();
    }

    private final ShakeListener mShakeListener;

    private SensorManager mSensorManager;

    public ShakeDetector2(ShakeListener listener) {
        mShakeListener = listener;
    }

    /**
     * Start listening for shakes.
     */
    public void start(SensorManager manager) {
        Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            mSensorManager = manager;
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            AnyExtentionKt.loge(this, "错误，当前手机无法获取：ACCELEROMETER传感器，摇一摇功能异常。");
        }
    }

    /**
     * Stop listening for shakes.
     */
    public void stop() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - mLastUpdateTime;
        if (diffTime < UPDATE_INTERVAL) {
            return;
        }
        mLastUpdateTime = currentTime;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float deltaX = x - mLastX;
        float deltaY = y - mLastY;
        float deltaZ = z - mLastZ;
        mLastX = x;
        mLastY = y;
        mLastZ = z;
        float delta = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000);

        // 当加速度的差值大于指定的阈值，认为这是一个摇晃
        if (delta > shakeThreshold && mShakeListener!=null) {
            mShakeListener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

}