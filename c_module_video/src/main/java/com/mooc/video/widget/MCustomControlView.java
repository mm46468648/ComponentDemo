package com.mooc.video.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.android.exoplayer2.ui.PlayerControlView;
import com.mooc.video.PlayerViewWrapper;
import com.mooc.video.R;
import com.mooc.video.util.VideoPlayUtils;
import com.mooc.common.utils.SystemUtils;


/**
 * 自定义布局
 * 常用控件（播放，暂停，进度条，和时间等）采用和源码中定义相同id
 * 其他扩展操作按钮采用自己定义的控件（标题栏，全屏，和播放状态提示等）
 */
public class MCustomControlView extends PlayerControlView {


    private Context mContext;
    final Activity activity;
    protected int setSystemUiVisibility = 0;
    private AppCompatCheckBox fullScreent;
    private ImageButton exo_play;
    private boolean isLand;
    private PlayerViewWrapper playerView;

    public MCustomControlView(Context context) {
        this(context, null);
    }

    public MCustomControlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCustomControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        activity = VideoPlayUtils.Companion.scanForActivity(context);
        setSystemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();


        _init();
    }

    private void _init() {

        fullScreent = findViewById(R.id.sexo_video_fullscreen);
        fullScreent.setOnClickListener(new OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext,"点击横屏",Toast.LENGTH_SHORT).show();

                if (VideoPlayUtils.Companion.isLand(getContext())) {
                    //  doOnConfigurationChanged(false);
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //切横屏landscape
                } else {
                    //  doOnConfigurationChanged(true);
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

                doOnConfigurationChanged(!isLand);
            }
        });


        exo_play = findViewById(R.id.exo_play);

//        exo_play.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        if(!NetUtils.getNetworkType().equals(NetType.WIFI)){
//                            getPlayerView().getControllerListener().showNotWifi();
//                            return true;
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//                return false;
//            }
//        });




//        setOnTouchListener(mOnTouchListener);

    }


    Boolean isTouching = false;
    protected final OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouching = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isTouching) {
                        show();
                        isTouching = false;
                    }
                    break;
            }
            return isTouching;
        }
    };

    public void setPlayerView(PlayerViewWrapper playerView) {
        this.playerView = playerView;
    }



    /**
     * 此处需要重写show，保留最外层布局，隐藏其他
     */
    @Override
    public void show() {
        super.show();
    }

    /***
     * 设置是横屏,竖屏
     *
     * @param island  是否横屏
     */
    public void doOnConfigurationChanged(boolean island) {
        //横屏
        if (island) {
//            if (isWGh()) {
//                getPlayerView().getVideoSurfaceView().doOnConfigurationChanged(270);
//            }
            VideoPlayUtils.Companion.hideActionBar(getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

//            mLockControlView.setLockCheck(false);
            //列表显示
//            showListBack(VISIBLE);
//            显示锁屏按钮
//            showLockState(VISIBLE);
        } else {//竖屏
//            if (isWGh()) {
//                getPlayerView().getVideoSurfaceView().doOnConfigurationChanged(0);
//            }
            activity.getWindow().getDecorView().setSystemUiVisibility(setSystemUiVisibility);
            VideoPlayUtils.Companion.showActionBar(activity);

            //列表播放
//            showListBack(GONE);
//            //隐藏锁屏按钮移除
//            showLockState(GONE);
        }
        //显更改全屏按钮选中，自动旋转屏幕
        getExoFullscreen().setChecked(island);
        setLand(island);
        playerView.scaleLayout();


//        if (getPlaybackControlView().isPlaying()){
//            getPlaybackControlView().setOutAnim();
//        }
    }


    /***
     * 设置内容横竖屏内容
     *
     */
    protected void scaleLayout() {
        if (playerView == null) return;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            mExoPlayerListener.land();
//        }
//        float layoutHeight = isLand ? screenWidth : verticalHeight;
//        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) layoutHeight);
//        layoutParams.gravity = Gravity.CENTER;
//        this.setLayoutParams(layoutParams);
//        getParent().requestLayout();

    }


    /**
     * 设置是否横屏
     *
     * @param land land  默认 false  true  横屏
     */
    protected void setLand(boolean land) {
        isLand = land;
    }

    public boolean isLand() {
        return isLand;
    }

    /***
     * 获取全屏按钮
     * @return boolean exo fullscreen
     */
    public AppCompatCheckBox getExoFullscreen() {
        return fullScreent;
    }

    public PlayerViewWrapper getPlayerView() {
        return playerView;
    }

    public void setCustomControllLayout(View view){
        addView(view,getChildCount());
    }
}
