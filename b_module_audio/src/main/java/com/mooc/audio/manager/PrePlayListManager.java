package com.mooc.audio.manager;

import android.annotation.SuppressLint;

import com.mooc.audio.AudioApi;
import com.mooc.common.global.AppGlobals;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.commonbusiness.model.audio.BaseAudioModle;
import com.mooc.commonbusiness.model.search.TrackBean;
import com.mooc.commonbusiness.net.ApiService;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 预播放列表管理类
 */
public class PrePlayListManager implements XiMaUtile.StatusListener {
    private static PrePlayListManager instance = null;
    private XiMaUtile xiMaUtile;
    private String albumId;
    private Boolean offlineMode;

    public Boolean getOfflineMode() {
        return offlineMode;
    }

    private PrePlayListManager() {
        xiMaUtile = XiMaUtile.getInstance();
        xiMaUtile.setStatusListener(this);
    }

    public static PrePlayListManager getInstance() {
        if (instance == null) {
            synchronized (PrePlayListManager.class) {
                if (instance == null) {
                    instance = new PrePlayListManager();
                }
            }
        }
        return instance;
    }

    private List<TrackBean> mPretrackBeans = new ArrayList<>();  //app中使用的模型

    int currentPlayPosition = 0;

    /**
     * 设置预播放列表
     *
     * @param list
     */
    public void setPreList(List<TrackBean> list, String albumId, int playPosition) {
        offlineMode = false;
        if (list.isEmpty()) return;
        if (playPosition < 0 || playPosition >= list.size()) return;

        mPretrackBeans.clear();
        mPretrackBeans.addAll(list);
        this.albumId = albumId;
        currentPlayPosition = playPosition;
        prePlay();
    }

    /**
     * 设置本地的预播放队列
     *
     * @param list
     * @param albumId
     * @param playPosition
     */
    public void setLocalPreList(List<TrackBean> list, String albumId, int playPosition) {
        offlineMode = true;

        mPretrackBeans.clear();
        mPretrackBeans.addAll(list);

        this.albumId = albumId;
        currentPlayPosition = playPosition;
        prePlay();

//        XiMaUtile.getInstance().setList((List<BaseAudioModle>) (List) list, playPosition);
//        XiMaUtile.getInstance().playTrack(AppGlobals.INSTANCE.getApplication());
    }

    /**
     * 准备播放下一个
     */
    @SuppressLint("CheckResult")
    void prePlay() {
        if (currentPlayPosition >= mPretrackBeans.size()) return;
        BaseAudioModle baseAudioModle = mPretrackBeans.get(currentPlayPosition);

        if (offlineMode) {      //直接让ximautil播放
            ArrayList<BaseAudioModle> objects = new ArrayList<>();
            objects.add(baseAudioModle);
            xiMaUtile.setList(objects, 0);
            XiMaUtile.getInstance().playTrack(AppGlobals.INSTANCE.getApplication());
            return;
        }
        //请求播放地址,交给XUtil播放
        ApiService.getRetrofit().create(AudioApi.class)
                .getTrackInfo2(baseAudioModle.getId())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<TrackBean>() {
                    @Override
                    public void accept(TrackBean trackBean) throws Exception {

                        ArrayList<BaseAudioModle> objects = new ArrayList<>();
                        objects.add(trackBean);
                        xiMaUtile.setList(objects, 0);
                        xiMaUtile.playTrack(AppGlobals.INSTANCE.getApplication());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });


    }


    @Override
    public void onPlayStart() {

    }

    @Override
    public void onPlayPause() {

    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onSoundPlayComplete() {

//        if (offlineMode) {     //离线模式不处理,ximaUtil去处理
//            return;
//        }
        currentPlayPosition++;
        AnyExtentionKt.loge(this, "播放完毕切换下一首" + currentPlayPosition);
        prePlay();
    }

    @Override
    public void onSoundPrepared() {

    }

    @Override
    public void onSoundSwitch(PlayableModel var1, PlayableModel var2) {

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int var1) {

    }

    @Override
    public void onPlayProgress(int var1, int var2) {

    }

    @Override
    public boolean onError(XmPlayerException var1) {
        return false;
    }

    /**
     * 查看队列中是否包含音频
     */
    public Boolean checkContainTrack(@NotNull String trackId) {
        for (int i = 0; i < mPretrackBeans.size(); i++) {
            BaseAudioModle baseAudioModle = mPretrackBeans.get(i);
            if (baseAudioModle.getId().equals(trackId)) {
                return true;
            }
        }
        return false;
    }

    public void playTrackId(@NotNull String trackId) {

        int findIndex = -1;
        for (int i = 0; i < mPretrackBeans.size(); i++) {
            BaseAudioModle baseAudioModle = mPretrackBeans.get(i);
            if (baseAudioModle.getId().equals(trackId)) {
                findIndex = i;
            }
        }

        if (findIndex != -1) {
            currentPlayPosition = findIndex;
            prePlay();
        }
    }

    public void playNext() {
        if(currentPlayPosition<mPretrackBeans.size()){
            currentPlayPosition++;
            prePlay();
        }
    }

    public void playPre() {
        if(currentPlayPosition>0){
            currentPlayPosition--;
            prePlay();
        }
    }

    public void clearList(){
        mPretrackBeans.clear();
        currentPlayPosition = 0;
    }
}
