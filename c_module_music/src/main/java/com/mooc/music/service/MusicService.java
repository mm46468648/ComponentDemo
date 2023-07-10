/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mooc.music.service;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.bumptech.glide.Glide;
import com.mooc.music.R;
import com.mooc.music.model.MusicData;
import com.mooc.music.intf.GetNotifyBitmapSuc;
import com.mooc.music.manager.MusicPlayerManager;
import com.mooc.music.service.contentcatalogs.MusicLibrary;
import com.mooc.music.service.notifications.MediaNotificationManager;
import com.mooc.music.service.players.ExoPlayerAdapter;
import com.mooc.music.util.ConstantUtils;
import com.mooc.common.global.AppGlobals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MusicService extends MediaBrowserServiceCompat {

    private static final String TAG = MusicService.class.getSimpleName();

    public MediaSessionCompat mSession;
    public PlayerAdapter mPlayback;
    public MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCallback mCallback;
    private volatile boolean mServiceInStartedState;
    public static final Integer REPEAT_ALL = 0;
    public static final Integer REPEAT_SINGLE = 1;
    public static final Integer REPEAT_SHUFFLE = 2;
    public static Integer currentRepeatMode = REPEAT_ALL;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new MediaSession.
        mSession = new MediaSessionCompat(this, "MusicService");
        mCallback = new MediaSessionCallback();
        mSession.setCallback(mCallback);
        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());

        mMediaNotificationManager = new MediaNotificationManager(this);
        mMediaNotificationManager.initBroadcastReceivers(MusicService.this);
        mPlayback = new ExoPlayerAdapter(this, new MediaPlayerListener());
        Log.d(TAG, "onCreate: MusicService creating MediaSession, and MediaNotificationManager");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        mMediaNotificationManager.onDestroy();
        mPlayback.stop();
        mSession.release();
        Log.d(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 Bundle rootHints) {
        return new BrowserRoot(MusicLibrary.getRoot(), null);
    }

    @Override
    public void onLoadChildren(
            @NonNull final String parentMediaId,
            @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(MusicLibrary.getMediaItems());
    }

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    public class MediaSessionCallback extends MediaSessionCompat.Callback {
        private final List<MediaSessionCompat.QueueItem> mPlaylist = new ArrayList<>();
        private int mQueueIndex = -1;
        private MediaMetadataCompat mPreparedMedia;

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            mPlaylist.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mQueueIndex == -1) ? 0 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            mPlaylist.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mPlaylist.isEmpty()) ? -1 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }


        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            if (action.equals(ConstantUtils.DATA_CLEAR_MUSICS)) {
                mPlaylist.clear();
                mSession.setQueue(mPlaylist);
            }

            if (action.equals(ConstantUtils.DATA_SET_SPEED_MUSICS)) {
                float speed = extras.getFloat(ConstantUtils.DATA_SET_SPEED_MUSICS);
                mPlayback.setPlaySpeed(speed);
            }
        }

        @Override
        public void onPrepare() {
            if (mQueueIndex < 0 || mQueueIndex>=mPlaylist.size()) {
                // Nothing to play.
                return;
            }

            final String mediaId = mPlaylist.get(mQueueIndex).getDescription().getMediaId();
            mPreparedMedia = MusicLibrary.getMetadata(MusicService.this, mediaId);
            mSession.setMetadata(mPreparedMedia);
            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            if (!isReadyToPlay()) {
                // Nothing to play.
                return;
            }
            mQueueIndex = MusicLibrary.getMetaDataPosition(mediaId);
            mPreparedMedia = MusicLibrary.getMetadata(MusicService.this, mediaId);
            if (mPreparedMedia == null) {
                onPrepare();
            }
            mSession.setMetadata(mPreparedMedia);
            Log.d(TAG, "onPlayFromMediaId: MediaSession active");
            mPlayback.playFromMedia(mPreparedMedia);
//            sendUpdateUiBroadCast();

        }

//        private void sendUpdateUiBroadCast() {
//            Intent intent = new Intent();
//            intent.setAction(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_PLAY);
//            intent.putExtra(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_PLAY, mPreparedMedia);
//            sendBroadcast(intent);
//        }


        @Override
        public void onPlay() {
            if (!isReadyToPlay()) {
                // Nothing to play.
                return;
            }

            if (mPreparedMedia == null) {
                onPrepare();
            }
            Log.d(TAG, "onPlay: MediaSession active");
            mPlayback.playFromMedia(mPreparedMedia);
//            sendUpdateUiBroadCast();
        }

        @Override
        public void onPause() {
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mSession.setActive(false);
            mMediaNotificationManager.unregisterReceiver(MusicService.this);

        }

        @Override
        public void onSkipToNext() {
            if (mPlaylist.size() <= 1) {
                return;
            }

            if (mQueueIndex != mPlaylist.size() - 1) {
                mQueueIndex = (++mQueueIndex % mPlaylist.size());
                mPreparedMedia = null;
                onPlay();
            }
        }

        @Override
        public void onSkipToPrevious() {
            if (mPlaylist.size() == 1) {
                return;
            }

            if (mQueueIndex > 0) {
                mQueueIndex = mQueueIndex > 0 ? mQueueIndex - 1 : mPlaylist.size() - 1;
                mPreparedMedia = null;
                onPlay();
            }
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        private boolean isReadyToPlay() {
            return (!mPlaylist.isEmpty());
        }
    }

    // MediaPlayerAdapter Callback: MediaPlayerAdapter state -> MusicService.

    public class MediaPlayerListener implements PlaybackInfoListener {//这个是通过exoplayer的callback然后来通过BroadCastReveicer updateUI的

        private final ServiceManager mServiceManager;


        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }

        /**
         *  只要状态发生改变就会回调这个
         *  最新的mediaId
         * @param newMediaId
         */
        @Override
        public void updateUI(MediaMetadataCompat newMediaId) {
//            Log.d(TAG,"musicService UpdateUI");

            sendUpdateUiBroadCast(newMediaId);
        }

        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            // Report the state to the MediaSession.
            mSession.setPlaybackState(state);
            String cover = "";

            MediaMetadataCompat metadata = mSession.getController().getMetadata();
            if(metadata == null) return;

            cover = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI);

            // Manage the started state of this service.
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
//                    MusicPlayerManager.canPlay.setValue(true);
                    mServiceManager.moveServiceToStartedState(state, cover);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mServiceManager.updateNotificationForPause(state, cover);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mServiceManager.moveServiceOutOfStartedState(state);
                    break;
            }


            Intent intent = new Intent();
            intent.setAction(ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE);
            intent.putExtra(ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE, state);
            sendBroadcast(intent);

        }

        @Override
        public void onSeekTo(long progress, long bufferPosition, long max) {
            if (max < 0) {  //播放出错时
                return;
            }
            if (progress > max) {
                progress = max;
            }
            MusicData music = new MusicData();
            music.setProgress(progress);
            music.setBuffer((int) bufferPosition);
            music.setDuration(max);

            //发送更新时间广播
            Intent intent = new Intent();
            intent.setAction(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_UPDATE_TIME);
            intent.putExtra(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_UPDATE_TIME, music);
            sendBroadcast(intent);

        }

        @Override
        public void onPlaybackComplete() {
            Log.d(TAG, "onPlaybackComplete: SKIPPING TO NEXT.");
            //发送播放完成广播
            Intent intent = new Intent();
            intent.setAction(ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE_COMPLETE);
            sendBroadcast(intent);

            if (currentRepeatMode.equals(MusicService.REPEAT_SINGLE)) {
                mSession.getController().getTransportControls().play();
            } else {
                //单音频不播放下一条
                if (MusicLibrary.music.size() > 1) {
                    mSession.getController().getTransportControls().skipToNext();
                }
            }


        }

        @Override
        public void onPlayReady(boolean isReady) {
            Intent intent = new Intent();
            intent.setAction(ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE_READY);
            sendBroadcast(intent);
        }


    }

    class ServiceManager {

        private void moveServiceToStartedState(PlaybackStateCompat state, String cover) {

            getNotifyBitmap(cover
                    , new GetNotifyBitmapSuc() {
                        @Override
                        public void getNotifyBitmapSuc() {
                            Notification notification =
                                    mMediaNotificationManager.getNotification(
                                            mPlayback.getCurrentMedia(), state, getSessionToken());

                            if (!mServiceInStartedState) {
                                ContextCompat.startForegroundService(
                                        MusicService.this,
                                        new Intent(MusicService.this, MusicService.class));
                                mServiceInStartedState = true;
                            }

                            startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);
                        }
                    });


        }

        private void updateNotificationForPause(PlaybackStateCompat state, String cover) {
            getNotifyBitmap(cover
                    , new GetNotifyBitmapSuc() {
                        @Override
                        public void getNotifyBitmapSuc() {
                            stopForeground(false);
                            Notification notification =
                                    mMediaNotificationManager.getNotification(
                                            mPlayback.getCurrentMedia(), state, getSessionToken());
                            mMediaNotificationManager.getNotificationManager()
                                    .notify(MediaNotificationManager.NOTIFICATION_ID, notification);                      }
                    });


        }

        private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
            stopForeground(true);
            stopSelf();
            mServiceInStartedState = false;
        }


        private void getNotifyBitmap(String url, GetNotifyBitmapSuc getNotifyBitmapSuc) {
            Observable<Bitmap> observable = Observable
                    .create(new ObservableOnSubscribe<Bitmap>() {

                        @Override
                        public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                            File file = Glide.with(AppGlobals.INSTANCE.getApplication())
                                    .load(url)
                                    .downloadOnly(85, 85)
                                    .get();
                            emitter.onNext(BitmapFactory.decodeFile(file.getPath()));
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            observable.subscribe(new io.reactivex.Observer<Bitmap>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Bitmap bitmap) {
                    MusicPlayerManager.currentNotifyBitmap = bitmap;
                    if (getNotifyBitmapSuc != null) {
                        getNotifyBitmapSuc.getNotifyBitmapSuc();
                    }


                }

                @Override
                public void onError(Throwable e) {
                    MusicPlayerManager.currentNotifyBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.iv_logo);
                    if (getNotifyBitmapSuc != null) {
                        getNotifyBitmapSuc.getNotifyBitmapSuc();
                    }

                }

                @Override
                public void onComplete() {

                }
            });


        }
    }


    private void sendUpdateUiBroadCast(MediaMetadataCompat mPreparedMedia) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_PLAY);
        intent.putExtra(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_PLAY, mPreparedMedia);
        sendBroadcast(intent);
    }

}