package com.mooc.music.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.mooc.music.model.MusicData;
import com.mooc.music.client.MediaBrowserHelper;
import com.mooc.music.service.MusicService;
import com.mooc.music.service.contentcatalogs.MusicLibrary;
import com.mooc.music.util.ConstantUtils;
import com.mooc.common.global.AppGlobals;

import java.util.ArrayList;
import java.util.List;


public class MusicPlayerManager {

    private static MusicPlayerManager instance;

    private MediaBrowserHelper mMediaBrowserHelper;

//    public static MutableLiveData<List<TrackBean>> musics = new MutableLiveData<>();

//    public static MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<>();

    public static Bitmap currentNotifyBitmap = null;


    public static MutableLiveData<MediaMetadataCompat> metadataCompatMutableLiveData = new MutableLiveData<>();

    public static MutableLiveData<MusicData> musicProgressMutableLiveData = new MutableLiveData<>();

    public static MutableLiveData<Boolean> playIdleLiveData = new MutableLiveData<>();

    public static MutableLiveData<PlaybackStateCompat> musicStateLiveData = new MutableLiveData<>();

    public MediaMetadataCompat currentPlayData;

    public volatile boolean isConnect = false;

//    public static MutableLiveData<Boolean> canPlay = new MutableLiveData<>();

    public boolean isConnect() {
        return isConnect;
    }


    public MusicPlayerManager(Context context) {
//        mIsPlaying.setValue(false);
//        canPlay.setValue(false);
        mMediaBrowserHelper = new MediaBrowserConnection(context);
        //注册播放进度回调
//        initProgressBroadcastReceiver();
        //注册切换音频
//        initUpdateUIBroadcastReceiver();
        //注册音乐状态切换
//        initUpdateStateBroadcastReceiver();

        //注册音乐贮备好
//        initPlayReadyBroadcastReceiver();
        onStart();
    }


    public static MusicPlayerManager getInstance() {
        if (instance == null) {
            synchronized (MusicPlayerManager.class) {
                if (instance == null) {
                    instance = new MusicPlayerManager(AppGlobals.INSTANCE.getApplication());
                }
            }
        }
        return instance;
    }

    MediaControllerCompat controllerCompat;

    public MediaControllerCompat getContrllerCompat() {
        return controllerCompat;
    }

    private class MediaBrowserConnection extends MediaBrowserHelper {

        private MediaBrowserConnection(Context context) {
            super(context, MusicService.class);
        }

        @Override
        protected void onConnected(@NonNull MediaControllerCompat mediaController) {
            isConnect = true;
        }


        @Override
        protected void onChildrenLoaded(@NonNull String parentId,
                                        @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            controllerCompat = getMediaController();

            // Queue up all media items for this simple sample.
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                controllerCompat.addQueueItem(mediaItem.getDescription());
            }

            // Call prepare now so pressing play just works.
            controllerCompat.getTransportControls().prepare();
        }
    }


    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {

        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : MusicLibrary.music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }


    public void play() {
        if (mMediaBrowserHelper != null && mMediaBrowserHelper.getmMediaController() != null) {
            mMediaBrowserHelper.getTransportControls().play();
        }
    }


    public void pause() {
        if (mMediaBrowserHelper != null && mMediaBrowserHelper.getmMediaController() != null) {
            mMediaBrowserHelper.getTransportControls().pause();
        }
    }

    private void onStart() {
        mMediaBrowserHelper.onStart();
    }

    public void onStop() {
        mMediaBrowserHelper.onStop();
        isConnect = false;
    }

    public void clearMusics() {
        try {
            mMediaBrowserHelper.getTransportControls().sendCustomAction(ConstantUtils.DATA_CLEAR_MUSICS, null);
        }catch (Exception e){
        }
    }


    public void playMediaId(String mediaId) {
//        currentPlayData = MusicLibrary.
//                getTrackPlayWithId(mediaId + "");
        currentPlayData = MusicLibrary.
                getMetadata(AppGlobals.INSTANCE.getApplication(), mediaId + "");
        mMediaBrowserHelper.getTransportControls().
                playFromMediaId(mediaId, null);
    }


    public MediaMetadataCompat getCurrentPlayMusic() {
        return currentPlayData;
    }


    public void seekTo(long pos) {
        if (mMediaBrowserHelper != null && mMediaBrowserHelper.getmMediaController() != null) {
            mMediaBrowserHelper.getTransportControls().seekTo(pos);
        }
    }


    public void setSpeed(float speed) {
        Bundle bundle = new Bundle();
        bundle.putFloat(ConstantUtils.DATA_SET_SPEED_MUSICS, speed);
        mMediaBrowserHelper.getTransportControls().sendCustomAction(ConstantUtils.DATA_SET_SPEED_MUSICS, bundle);
    }


    public interface MusicPlayCallBack {

        void changeMusic(MediaMetadataCompat mediaMetadataCompat);

        void stateChange(PlaybackStateCompat stateCompat);

        void playReady(Boolean isReady);

        void onSeekTo(MusicData musicData);

        void isPlaying(boolean isPlaying);
    }


}
