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

package com.mooc.music.service.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.session.MediaButtonReceiver;
import com.mooc.music.R;
import com.mooc.music.manager.MusicPlayerManager;
import com.mooc.music.service.MusicService;
import com.mooc.music.util.ConstantUtils;
import com.mooc.common.global.AppGlobals;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Keeps track of a notification and updates it automatically for a given MediaSession. This is
 * required so that the music service don't get killed during playback.
 */
public class MediaNotificationManager {
    private final PlayNotifyReceiver playNotifyReceiver;
    public static final int NOTIFICATION_ID = 458;

    private static final String TAG = MediaNotificationManager.class.getSimpleName();
    private static final String CHANNEL_ID = "com.new.mooc.android.musicplayer.channel";
    private static final int REQUEST_CODE = 501;
    public static final String PLAY_NOTIFY = "play_notify_new_mooc";
    public static final String PLAY_NOTIFY_CODE = "play_new_mooc_code";
    public static final int PLAY_NOTIFY_CLOSE = 4;
    private final MusicService mService;
    private final NotificationManagerCompat manager;
    //    private final NotificationCompat.Action mPlayAction;
//    private final NotificationCompat.Action mPauseAction;
//    private final NotificationCompat.Action mNextAction;
//    private final NotificationCompat.Action mPrevAction;
    private final NotificationManager mNotificationManager;

    public MediaNotificationManager(MusicService service) {
        playNotifyReceiver = new PlayNotifyReceiver();
        mService = service;
        this.manager = NotificationManagerCompat.from(service);
        mNotificationManager =
                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);

//
//        mPlayAction =
//                new NotificationCompat.Action(
//                        R.drawable.ic_play_arrow_white_24dp,
//                        mService.getString(R.string.label_play),
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(
//                                mService,
//                                PlaybackStateCompat.ACTION_PLAY));
//        mPauseAction =
//                new NotificationCompat.Action(
//                        R.drawable.ic_pause_white_24dp,
//                        mService.getString(R.string.label_pause),
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(
//                                mService,
//                                PlaybackStateCompat.ACTION_PAUSE));
//        mNextAction =
//                new NotificationCompat.Action(
//                        R.drawable.ic_skip_next_white_24dp,
//                        mService.getString(R.string.label_next),
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(
//                                mService,
//                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
//        mPrevAction =
//                new NotificationCompat.Action(
//                        R.drawable.ic_skip_previous_white_24dp,
//                        mService.getString(R.string.label_previous),
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(
//                                mService,
//                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
    }

    public NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public Notification getNotification(MediaMetadataCompat metadata,
                                        @NonNull PlaybackStateCompat state,
                                        MediaSessionCompat.Token token) {
        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
        MediaDescriptionCompat description = metadata.getDescription();
        NotificationCompat.Builder builder =
                buildNotification(state, token, isPlaying, description);
        return builder.build();
    }

    public void initBroadcastReceivers(Context context) {
        IntentFilter filter = new IntentFilter(PLAY_NOTIFY);
        context.registerReceiver(playNotifyReceiver, filter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(playNotifyReceiver);
    }

    private NotificationCompat.Builder buildNotification(@NonNull PlaybackStateCompat state,
                                                         MediaSessionCompat.Token token,
                                                         boolean isPlaying,
                                                         MediaDescriptionCompat description) {

        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOOrHigher()) {
            createChannel();
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mService, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.iv_logo_round);
        RemoteViews remoteViews = new RemoteViews(AppGlobals.INSTANCE.getApplication().getPackageName(), R.layout.item_notice);
        remoteViews.setTextViewText(R.id.tv_title, description.getTitle());
//        remoteViews.setTextViewText(R.id.tv_subtitle, description.getSubtitle());
//        remoteViews.setImageViewBitmap(R.id.iv_img, MusicPlayerManager.currentNotifyBitmap);

//        remoteViews.setOnClickPendingIntent(R.id.ll_content, createContentIntent());
        Intent close = new Intent(PLAY_NOTIFY);
        close.putExtra(PLAY_NOTIFY_CODE, PLAY_NOTIFY_CLOSE);
        PendingIntent p3 = PendingIntent.getBroadcast(mService, PLAY_NOTIFY_CLOSE, close, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.rl_close, p3);
//        remoteViews.setOnClickPendingIntent(R.id.iv_close, MediaButtonReceiver.buildMediaButtonPendingIntent(
//                mService,
//                PlaybackStateCompat.ACTION_STOP));
        remoteViews.setOnClickPendingIntent(R.id.iv_next, MediaButtonReceiver.buildMediaButtonPendingIntent(
                mService,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.iv_pre, MediaButtonReceiver.buildMediaButtonPendingIntent(
                mService,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));


        // 设置播放
        if (isPlaying) {
            remoteViews.setImageViewBitmap(R.id.iv_play, ((BitmapDrawable) mService.getResources().getDrawable(R.mipmap.icon_play)).getBitmap());
            remoteViews.setOnClickPendingIntent(R.id.iv_play, MediaButtonReceiver.buildMediaButtonPendingIntent(
                    mService,
                    PlaybackStateCompat.ACTION_PAUSE));
        }
        if (!isPlaying) {
            remoteViews.setImageViewBitmap(R.id.iv_play, ((BitmapDrawable) mService.getResources().getDrawable(R.mipmap.icon_stop)).getBitmap());
            remoteViews.setOnClickPendingIntent(R.id.iv_play, MediaButtonReceiver.buildMediaButtonPendingIntent(
                    mService,
                    PlaybackStateCompat.ACTION_PLAY));
        }


        builder.setCustomContentView(remoteViews);
        //设置通知栏的点击事件
        builder.setContentIntent(createContentIntent());
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setAutoCancel(true);
        builder.setCustomBigContentView(remoteViews);

//                .setStyle(
//                new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(token)
//                        .setShowActionsInCompactView(0, 1, 2)
//                        // For backwards compatibility with Android L and earlier.
////                        .setShowCancelButton(true)
//                        .setCancelButtonIntent(
//                                MediaButtonReceiver.buildMediaButtonPendingIntent(
//                                        mService,
//                                        PlaybackStateCompat.ACTION_STOP)))
//                .setColor(ContextCompat.getColor(mService, R.color.color_F))
//                .setSmallIcon(R.mipmap.iv_logo_round)
//                // Pending intent that is fired when user clicks on notification.
//                .setContentIntent(createContentIntent())
//                // Title - Usually Song name.
//                .setContentTitle(description.getTitle())
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                // Subtitle - Usually Artist name.
////                .setContentText(description.getSubtitle())
//                .setLargeIcon(MusicPlayerManager.currentNotifyBitmap)
//                // When notification is deleted (when playback is paused and notification can be
//                // deleted) fire MediaButtonPendingIntent with ACTION_STOP.
//                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
//                        mService, PlaybackStateCompat.ACTION_STOP))
//                // Show controls on lock screen even when user hides sensitive content.
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // If skip to next action is enabled.
//        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
//            builder.addAction(mPrevAction);
//        }

//        builder.addAction(isPlaying ? mPauseAction : mPlayAction);

        // If skip to prev action is enabled.
//        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
//            builder.addAction(mNextAction);
//        }

        return builder;
    }

    // Does nothing on versions of Android earlier than O.
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = "MediaSession mooc";
            // The user-visible description of the channel.
            String description = "mooc MediaSession and MediaPlayer";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(
                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
            Log.d(TAG, "createChannel: New channel created");
        } else {
            Log.d(TAG, "createChannel: Existing channel reused");
        }
    }

    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent();
        MediaMetadataCompat currentPlayMusic = MusicPlayerManager.getInstance().getCurrentPlayMusic();
        if(currentPlayMusic!=null){
            openUI.putExtra("audio_params_id", currentPlayMusic.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID));
            openUI.putExtra("album_params_id", currentPlayMusic.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
            String string = currentPlayMusic.getString(MediaMetadataCompat.METADATA_KEY_WRITER); //临时使用writer字段区分，音频来源（喜马拉雅或者自建音频）
            openUI.putExtra("audio_type", string);
        }
        openUI.setAction(ConstantUtils.ACTION_CLICK_NITIFICATION_CONTENT);
        return PendingIntent.getBroadcast(
                mService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private class PlayNotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context activity, Intent intent) {

            int code = intent.getIntExtra(PLAY_NOTIFY_CODE, -1);

            Log.i(this.getClass().getSimpleName(), "onReceive " + code);
            switch (code) {
                case PLAY_NOTIFY_CLOSE:
                    MusicPlayerManager.getInstance().pause();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            manager.cancel(NOTIFICATION_ID);
                            timer.cancel();
                        }
                    }, 500);
                    break;
                case 7:
                    MusicPlayerManager.getInstance().pause();
                    Timer timer1 = new Timer();
                    timer1.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            manager.cancel(NOTIFICATION_ID);
                            timer1.cancel();
                        }
                    }, 300);
                    break;
                default:
                    break;
            }
        }


    }
}