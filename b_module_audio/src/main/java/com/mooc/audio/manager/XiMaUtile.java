package com.mooc.audio.manager;


import static com.mooc.common.ktextends.AnyExtentionKt.loge;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mooc.audio.AudioApi;
import com.mooc.audio.ui.XimaAudioActivity;
import com.mooc.common.bus.LiveDataBus;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.base.BaseApplication;
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;
import com.mooc.commonbusiness.manager.studylog.StudyLogManager;
import com.mooc.commonbusiness.model.audio.BaseAudioModle;
import com.mooc.commonbusiness.net.ApiService;
import com.mooc.commonbusiness.utils.format.RequestBodyUtil;
import com.mooc.commonbusiness.utils.incpoints.FirstAddPointManager;
import com.mooc.audio.receiver.XMLYPlayerReceiver;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerConfig;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by huangzuoliang on 2017/11/21.
 */

public class XiMaUtile {
    public static XiMaUtile xiMaUtile = null;
//    public List<Track> tracks;
//    public List<TrackBean> mtrackBeans;  //app中使用的模型
    public List<BaseAudioModle> mtrackBeans;  //app中使用的模型
    boolean canPlay = false;
    CopyOnWriteArrayList<WeakReference<StatusListener>> statusLisrenerList = new CopyOnWriteArrayList<>();
    XmPlayerManager mPlayerManager;
    int currentPlayPosition = 0;         //当前音频在音频列表中的位置
    int currentDuration = 0;        //当前的播放进度
    int totalDuration = -1;        //总进度
    public float currentSpeed = 1.0f;

    //记录当前自建音频id，对应的学习项目id，<key：自建音频id,value学习项目id>
    private HashMap<String, String> ownTrackToStudyProject = new HashMap<>();
    private AudioPointManager audioPointManager; //音频播放打点
    TrackAddScoreTimerManager mTrackScoreTimer;    //音频播放加分记时
    TrackCloseTimerManager mTrackCloseTimer;       //音频定时关闭记时

    private static Disposable mAudioPointTimer = null;    //音频打点定时器

    public int currentCloseTimerIndex = -1;  //当前倒计时选中索引,-1代码不开启，或者时间停止

    private boolean bottomFloatClose = true;     //用户手动点击关闭,如果音频未播放，默认关闭

    public TrackAddScoreTimerManager getmTrackScoreTimer() {
        return mTrackScoreTimer;
    }

    public AudioPointManager getAudioPointManager() {
        return audioPointManager;
    }

    public TrackCloseTimerManager getmTrackCloseTimer() {
        return mTrackCloseTimer;
    }

    private XiMaUtile() {
        mTrackScoreTimer = new TrackAddScoreTimerManager();
        audioPointManager = new AudioPointManager();
        createHeatTimerScheduler();
    }

    private void createHeatTimerScheduler() {
        if (mAudioPointTimer == null || mAudioPointTimer.isDisposed()) {
            mAudioPointTimer = Observable.interval(0, 5, TimeUnit.SECONDS).compose(RxUtils.applySchedulers()).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    if (canPlay && isPlaying()) {
                        audioPointManager.postEtState(AudioPointManager.STATUS_HEART);
                    }
                }
            });
        }
    }

    public HashMap<String, String> getOwnTrackToStudyProject() {
        return ownTrackToStudyProject;
    }

    public static XiMaUtile getInstance() {
        if (xiMaUtile == null) {
            synchronized (XiMaUtile.class) {
                if (xiMaUtile == null) {
                    xiMaUtile = new XiMaUtile();
                }
            }
        }
        return xiMaUtile;
    }


    public void setBottomFloatClose(boolean bottomFloatClose) {
        this.bottomFloatClose = bottomFloatClose;
    }

    public boolean isBottomFloatClose() {
        return bottomFloatClose;
    }

    public void setStatusListener(StatusListener statusListener) {
        statusLisrenerList.add(new WeakReference<>(statusListener));
    }

    public void removeStatusLIstener(StatusListener statusListener) {
        for (int j = statusLisrenerList.size() - 1; j >= 0; j--) {
            WeakReference<StatusListener> statusListenerWeakReference = statusLisrenerList.get(j);
            StatusListener statusListener1 = statusListenerWeakReference.get();
            if (statusListener1 == statusListener || statusListener1 == null) {
                statusLisrenerList.remove(statusListenerWeakReference);
            }
        }
    }

    private void initXimalaya(Context context) {
//        this.context = context;
        XmNotificationCreater instanse = XmNotificationCreater.getInstanse(context);
        Notification mNotification = instanse.initNotification(context.getApplicationContext(), XimaAudioActivity.class);
        XmPlayerConfig.getInstance(context).setUseSystemLockScreen(true);
        mPlayerManager = XmPlayerManager.getInstance(context);
        mPlayerManager.init((int) System.currentTimeMillis(), mNotification);
        mPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                mPlayerManager.removeOnConnectedListerner(this);
                mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                canPlay = true;
                playTrackList();
            }
        });
        if (xmPlayerStatusListener != null) {
            mPlayerManager.addPlayerStatusListener(xmPlayerStatusListener);
        }
    }

    private IXmPlayerStatusListener xmPlayerStatusListener = new IXmPlayerStatusListener() {
        @Override
        public void onPlayStart() {
            invokeStatusDispatch(playStartState);

            audioPointManager.postEtState(AudioPointManager.STATUS_PLAY);
            createHeatTimerScheduler();
        }

        @Override
        public void onPlayPause() {
            invokeStatusDispatch(playPauseState);

            BaseAudioModle appTrackBean = getAppTrackBean(mPlayerManager.getCurrSound().getDataId());
            if(appTrackBean == null) return;
            postCurrentPlayDuration(appTrackBean.getId(),Long.valueOf(currentDuration));

            audioPointManager.postEtState(AudioPointManager.STATUS_PAUSE);

        }

        @Override
        public void onPlayStop() {
            invokeStatusDispatch(playStopState);
        }

        @Override
        public void onSoundPlayComplete() {
            invokeStatusDispatch(playSoundPlayCompleteState);

            BaseAudioModle appTrackBean = getAppTrackBean(mPlayerManager.getCurrSound().getDataId());
            if(appTrackBean == null) return;
            postCurrentPlayDuration(appTrackBean.getId(),Long.valueOf(totalDuration));

            audioPointManager.postEtState(AudioPointManager.STATUS_END);

        }

        @Override
        public void onSoundPrepared() {

            invokeStatusDispatch(playSoundPreparedState);

            //进度大于98%从头开始播
            seekToLearnRead();

        }

        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
            if(playableModel1 == null) return;
            onTrackChange(playableModel, playableModel1);

            for (int j = 0; j < statusLisrenerList.size(); j++) {
                WeakReference<StatusListener> statusListenerWeakReference = statusLisrenerList.get(j);
                StatusListener statusListener = statusListenerWeakReference.get();
                if (statusListener == null) continue;
                statusListener.onSoundSwitch(playableModel, playableModel1);
            }
        }


        @Override
        public void onBufferingStart() {
//            if (statusListener != null) {
//                statusListener.onBufferingStart();
//            }
            invokeStatusDispatch(playBufferingStartState);
        }

        @Override
        public void onBufferingStop() {
//            if (statusListener != null) {
//                statusListener.onBufferingStop();
//            }
            invokeStatusDispatch(playBufferingStopState);
        }

        @Override
        public void onBufferProgress(int i) {
//            if (statusListener != null) {
//                statusListener.onBufferProgress(i);
//            }
            for (int j = 0; j < statusLisrenerList.size(); j++) {
                WeakReference<StatusListener> statusListenerWeakReference = statusLisrenerList.get(j);
                StatusListener statusListener = statusListenerWeakReference.get();
                if (statusListener == null) continue;
                statusListener.onBufferProgress(i);
            }
        }

        @Override
        public void onPlayProgress(int i, final int i1) {
            currentDuration = i / 1000;
            totalDuration = i1 / 1000;

            for (int j = 0; j < statusLisrenerList.size(); j++) {
                WeakReference<StatusListener> statusListenerWeakReference = statusLisrenerList.get(j);
                StatusListener statusListener = statusListenerWeakReference.get();
                if (statusListener == null) continue;
                statusListener.onPlayProgress(i, i1);
            }

        }

        @Override
        public boolean onError(XmPlayerException e) {

            for (int j = 0; j < statusLisrenerList.size(); j++) {
                WeakReference<StatusListener> statusListenerWeakReference = statusLisrenerList.get(j);
                StatusListener statusListener = statusListenerWeakReference.get();
                if (statusListener == null) continue;
                statusListener.onError(e);
            }
            return false;
        }
    };


    /**
     * 音频切换
     * 记录播放位置
     * 上传学习记录等。。
     */
    private void onTrackChange(PlayableModel last, PlayableModel current) {
        long dataId = current.getDataId();
        BaseAudioModle appTrackBean = getAppTrackBean(dataId);
        if (appTrackBean == null) return;

        audioPointManager.setCurrentTrackId(appTrackBean.getId());

        loge("发送需要定位的音频: " + dataId);
        LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_LISTEN_TRACK_ID).postStickyData(appTrackBean.getId());

        //切换音频发送上传学习记录接口
        postStudyLog(appTrackBean.getResourceType(), appTrackBean.getId(), appTrackBean.getTrackTitle());
        //记录播放位置
        postCurrentPlayDuration(appTrackBean.getId(), 0L);
    }

    /**
     * 获取app的音频模型
     */
    public BaseAudioModle getAppTrackBean(Long trackId) {
        loge("查找播放的id:" + trackId);
        BaseAudioModle find = null;
        for (int i = 0; i < mtrackBeans.size(); i++) {
            BaseAudioModle trackBean = mtrackBeans.get(i);
            if (trackBean.getId().equals(String.valueOf(trackId))) {
                find = trackBean;
                break;
            }
        }
        return find;
    }

    private void seekToLearnRead() {
        if (getCurrentTrack() == null) {
            return;
        }
        //听完的音频从零开始播
        BaseAudioModle track = getCurrentTrack();
        if(track == null) return;
        if (Math.abs(track.getTotalDuration() - track.getLastDuration()) < 3) {
            xiMaUtile.getmPlayerManager().seekToByPercent(0);
        } else {
            xiMaUtile.getmPlayerManager().seekToByPercent((float) track.getLastDuration() / (float) track.getTotalDuration());
        }

    }


    /**
     * 设置倍放速度
     */
    public void setSpeed(Float speed) {
        if (currentSpeed == speed) {
            return;
        }

        if(!canPlay) return; //未初始化完成不能设置
        currentSpeed = speed;
        mPlayerManager.setTempo(speed);
    }

    public float getPlaySpeed() {
        return currentSpeed;
    }


    public BaseAudioModle getCurrentTrack() {
        if(mPlayerManager == null ||  mPlayerManager.getCurrSound() == null) return null;
        PlayableModel currSound = mPlayerManager.getCurrSound();

        return  getAppTrackBean(currSound.getDataId());
    }



    public void setList(List<BaseAudioModle> list, int currentPlayPosition) {
        mtrackBeans = list;
        this.currentPlayPosition = currentPlayPosition;
    }

    public void playTrack(Context context) {
        if (canPlay) {
            playTrackList();
        } else {
            initXimalaya(context);
        }

    }

    public XmPlayerManager getmPlayerManager() {
        return mPlayerManager;
    }


    private void playTrackList() {
        if (mPlayerManager == null || !canPlay) {
            initXimalaya(BaseApplication.Companion.getInstance());
            return;
        }
        if (mtrackBeans != null && mtrackBeans.size() > 0) {
            mPlayerManager.setTempo(currentSpeed);
            if (currentPlayPosition < 0 || currentPlayPosition >= mtrackBeans.size()) {
                currentPlayPosition = 0;
            }

            List<Track> tracks = XimaPlayManger.INSTANCE.convertoXimaTrack(mtrackBeans);
            mPlayerManager.playList(tracks, currentPlayPosition);
            bottomFloatClose = false;
        }
    }


    public boolean isPlaying() {
        if (mPlayerManager != null && mPlayerManager.isPlaying()) {
            return true;
        }
        return false;
    }

    public void play() {
        if (mPlayerManager != null && canPlay) {
            mPlayerManager.play();
        }
    }

    public void pause() {
        if (mPlayerManager != null && canPlay) {
            mPlayerManager.pause();
        }
    }

    public void seekTo(int progress) {
        if (mPlayerManager != null) {
            mPlayerManager.seekTo(progress);
        }
    }

    public void release() {
        canPlay = false;
        bottomFloatClose = true;
        if(mTrackScoreTimer!=null){
            mTrackScoreTimer.stopTime();
        }
        if(mTrackCloseTimer!=null){
            mTrackCloseTimer.stopTime();
        }
        //清空播放列表
        if(mtrackBeans!=null){
            mtrackBeans.clear();
        }
        ownTrackToStudyProject.clear();
        statusLisrenerList.clear();
        mAudioPointTimer.dispose();
        XmPlayerManager.release();
    }




    public static final int playStartState = 0;
    public static final int playPauseState = 1;
    public static final int playStopState = 2;
    public static final int playSoundPlayCompleteState = 3;
    public static final int playSoundPreparedState = 4;
    public static final int playSoundSwitchState = 5;
    public static final int playProgressState = 6;
    public static final int playBufferingStartState = 7;
    public static final int playBufferingStopState = 8;
    public static final int playErrorState = 9;

    private void invokeStatusDispatch(int status) {
        for (int j = 0; j < statusLisrenerList.size(); j++) {
            WeakReference<StatusListener> statusListenerWeakReference = statusLisrenerList.get(j);
            StatusListener statusListener = statusListenerWeakReference.get();
            if (statusListener == null) continue;
            switch (status) {
                case playStartState:
                    statusListener.onPlayStart();
                    break;
                case playPauseState:
                    statusListener.onPlayPause();
                    break;
                case playStopState:
                    statusListener.onPlayStop();
                    break;
                case playSoundPlayCompleteState:
                    statusListener.onSoundPlayComplete();
                    break;
                case playSoundPreparedState:
                    statusListener.onSoundPrepared();
                    break;
                case playBufferingStartState:
                    statusListener.onBufferingStart();
                    break;
                case playBufferingStopState:
                    statusListener.onBufferingStop();
                    break;
            }
        }
    }

    public interface StatusListener {
        void onPlayStart();

        void onPlayPause();

        void onPlayStop();

        void onSoundPlayComplete();

        void onSoundPrepared();

        void onSoundSwitch(PlayableModel var1, PlayableModel var2);

        void onBufferingStart();

        void onBufferingStop();

        void onBufferProgress(int var1);

        void onPlayProgress(int var1, int var2);

        boolean onError(XmPlayerException var1);
    }


    /**
     * 上传学习记录
     * 以及增加学习积分，等
     */
    void postStudyLog(int type, String id, String title) {
        //如果是自建音频，其实可以只通过activity中发送
//        val resourceType = if(isOwnTrack) ResourceTypeConstans.TYPE_ONESELF_TRACK else ResourceTypeConstans.TYPE_TRACK


        JSONObject request = new JSONObject();
        try {
            request.put("type", type);
            request.put("url", id);
            request.put("title", title);
            StudyLogManager.Companion.postStudyLog(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //学习资源增加积分
        postAddScore(type, id, title);
        //每日首次增加积分


        //定时器回调是空的情况代表后台播放
//        if(mTrackScoreTimer.getCurrentTimeCallBack() == null){
            FirstAddPointManager.Companion.startAddFirstPointsTask(
                    null, ResourceTypeConstans.Companion.getTypeAliasName().get(type), id, 0);
//        }
    }

    /**
     * 上传当前播放音频id
     * 和音频播放进度
     *
     * @param trackId  音频id
     * @param duration 时长 (单位s)
     */
    @SuppressLint("CheckResult")
    void postCurrentPlayDuration(String trackId, Long duration) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("track_id", trackId);
            requestData.put("duration", duration);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getRetrofit().create(AudioApi.class)
                .postAudioHistory(RequestBodyUtil.Companion.fromJson(requestData))
                .compose(RxUtils.applySchedulers())
                .subscribe(objectHttpResponse -> {

                }, throwable -> {
                    loge(
                            "error msg: " + throwable.toString(),
                            "currentMusic progress:" + currentPlayPosition);
                });
    }

    /**
     * 在播放30s到90s之前随机一个时间，倒计时后上报积分
     */
    void postAddScore(int type, String id, String title) {
        if (mTrackScoreTimer != null) {
            mTrackScoreTimer.startTime(title, String.valueOf(type), id);
        }

    }

    /**
     * 设置定时器定时播放
     * 定时时间
     */
    public void setTimer(Long time, TrackCloseTimerManager.TimeCloseListener timeCloseCallBack) {
        if (mTrackCloseTimer == null) {
            mTrackCloseTimer = new TrackCloseTimerManager();
        }

        if (time == 10 * 60L) {
            currentCloseTimerIndex = 0;
        } else if (time == 20 * 60L) {
            currentCloseTimerIndex = 1;
        } else if (time == 30 * 60L) {
            currentCloseTimerIndex = 2;
        } else if (time == 60 * 60L) {
            currentCloseTimerIndex = 3;
        } else if (time == 90 * 60L) {
            currentCloseTimerIndex = 4;
        } else {
            currentCloseTimerIndex = -1;
        }

//        mTrackCloseTimer.currentTimeCallBack = timeCallBack
        mTrackCloseTimer.setMTimeCloseListener(timeCloseCallBack);
        mTrackCloseTimer.startTime(time);
    }

    /**
     * 定时关闭音频播放
     * 停止计时
     */
    public void stopTimer() {
        if(mTrackCloseTimer!=null){
            mTrackCloseTimer.stopTime();
            mTrackCloseTimer = null;
        }
    }

}
