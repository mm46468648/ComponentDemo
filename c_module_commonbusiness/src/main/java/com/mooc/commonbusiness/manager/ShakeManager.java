package com.mooc.commonbusiness.manager;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.commonbusiness.constants.SpConstants;
import com.mooc.common.global.AppGlobals;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.sharepreference.SpDefaultUtils;
import com.mooc.commonbusiness.route.Paths;

import java.lang.ref.WeakReference;

/**
 * ShakeManager
 *
 * @author xl
 * @version V1.0
 * @since 23/12/2016
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class ShakeManager implements ShakeDetector2.ShakeListener {
    private static final String TAG = "ShakeManager";

    //    private ShakeDetector mShakeDetector;
    private ShakeDetector2 mShakeDetector;
    private final Context mContext;
    private WeakReference<Activity> mGetActivity;

    private long mLastShakeTime = -1;
    private static final long SHAKE_DELAY = 1000;

    private static ShakeManager instance;
    private Vibrator mVibrator;

    private ShakeManager(Context context) {

        if (context == null) {
            throw new NullPointerException("context can not be null");
        }
        mContext = context.getApplicationContext();
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static ShakeManager getInstance() {
        if (instance == null) {
            instance = new ShakeManager(AppGlobals.INSTANCE.getApplication());
        }
        return instance;
    }

    public void unregisterShakeDetector() {
        if (mShakeDetector != null) {
            mShakeDetector.stop();
            mGetActivity = null;
        }
    }

    public void registerShakeDetector(Activity p0) {
        boolean open = SpDefaultUtils.getInstance().getBoolean(SpConstants.SHAKE_FEEDBACK, false);
        if (!open) {       //如果没打开，直接返回
            return;
        }
        //设置页面,反馈弹窗，反馈页面直接返回，不注册
        if ("com.mooc.setting.ui.SettingActivity".equals(p0.getLocalClassName()) ||
                "com.mooc.commonbusiness.module.report.ShakeFeekbackDialogActivity".equals(p0.getLocalClassName())) {
            return;
        }
        if (mShakeDetector == null) {
            mShakeDetector = new ShakeDetector2(this);
        }

        mGetActivity = new WeakReference<Activity>(p0);
        mShakeDetector.start((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE));
    }

    @Override
    public void onShake() {

        boolean aBoolean = SpDefaultUtils.getInstance().getBoolean(SpConstants.SHAKE_FEEDBACK, false);
        if (!aBoolean) {
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        if (mLastShakeTime != -1 && currentTimeMillis - mLastShakeTime < SHAKE_DELAY) {
            return;
        }
        mLastShakeTime = currentTimeMillis;
        final Activity activity = mGetActivity.get();
        if (activity == null) {
            new Exception("Shake fail, getActivity is null").printStackTrace();
            return;
        }
        AnyExtentionKt.loge(this, activity.getLocalClassName());
        if ("com.example.setting.ui.SettingActivity".equals(activity.getLocalClassName()) ||
                "com.example.commonbusiness.module.report.ShakeFeekbackDialogActivity".equals(activity.getLocalClassName())) {
            //设置页面,反馈弹窗，反馈页面直接返回
            return;
        }
        mVibrator.vibrate(300);
        //todo 生成当前activity的视图，保存在本地，然后跳转
        ARouter.getInstance().build(Paths.PAGE_SHAKE_DIALOG).navigation();

    }
}
