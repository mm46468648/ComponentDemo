package com.mooc.music.service.players;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.music.service.MusicService;
import com.mooc.music.service.PlaybackInfoListener;
import com.mooc.music.service.PlayerAdapter;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class ExoPlayerAdapter extends PlayerAdapter {


    private static final String TAG = "MediaPlayerAdapter";


    private final Context mContext;
    private MediaMetadataCompat mCurrentMedia;//正在播放
    private boolean mCurrentMediaPlayedToCompletion;
    private int mState;
    private long mStartTime;
    private PlaybackInfoListener mPlaybackInfoListener;


    // ExoPlayer objects
    private SimpleExoPlayer mExoPlayer;
    private TrackSelector mTrackSelector;//select the track in exoplayer
    private DefaultRenderersFactory mRenderersFactory;
    private DefaultHttpDataSourceFactory mDataSourceFactory;//create a media source object using a Uri
    private ProgressiveMediaSource.Factory mediaSourceFactory;  //create a media source object using for local file

    private ExoPlayerEventListener mExoPlayerEventListener;


    public ExoPlayerAdapter(Context context, PlaybackInfoListener playbackInfoListener) {
        super(context);
        mContext = context.getApplicationContext();
        mPlaybackInfoListener = playbackInfoListener;
    }

    public SimpleExoPlayer getmExoPlayer() {
        return mExoPlayer;
    }

    private void initializeExoPlayer() {//initializeExoPlayer exeytime use it
        if (mExoPlayer == null) {

            mTrackSelector = new DefaultTrackSelector();
            mRenderersFactory = new DefaultRenderersFactory(mContext);
            mDataSourceFactory = new DefaultHttpDataSourceFactory("moocMusic", null);//generate prepare(media source)

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "moocMusic"));
            mediaSourceFactory = new ProgressiveMediaSource.Factory(dataSourceFactory);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, mRenderersFactory, mTrackSelector, new DefaultLoadControl());


            if (mExoPlayerEventListener == null) {
                mExoPlayerEventListener = new ExoPlayerEventListener();
            }
            mExoPlayer.addListener((Player.EventListener) mExoPlayerEventListener);
        }
    }


    private void release() {//release the player when not being used
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
        }

        if(mTimeHandler!=null){
            mTimeHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onPlay() {
        if (mExoPlayer != null && !mExoPlayer.getPlayWhenReady()) {
            mExoPlayer.setPlayWhenReady(true);//发出播放指令
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    @Override
    protected void setPlaySpeed(float speed) {
        if (mExoPlayer != null) {
            PlaybackParameters parameters = new PlaybackParameters(speed);
            mExoPlayer.setPlaybackParameters(parameters);
        }
    }

    @Override
    protected void onPause() {
        if (mExoPlayer != null && mExoPlayer.getPlayWhenReady()) {
            Log.d(TAG, "onPause: mediacontr  exoplayer");
            mExoPlayer.setPlayWhenReady(false);
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }

    }


    @Override
    public void playFromMedia(MediaMetadataCompat metadata) {
        startTrackingPlayback();//return the track percent to ui
        playFile(metadata);
    }

    @Override
    public MediaMetadataCompat getCurrentMedia() {
        return mCurrentMedia;
    }

    @Override
    public boolean isPlaying() {
        return mExoPlayer != null && mExoPlayer.getPlayWhenReady();
    }

    @Override
    protected void onStop() {
        // Regardless of whether or not the ExoPlayer has been created / started, the state must
        // be updated, so that MediaNotificationManager can take down the notification.
        Log.d(TAG, "onStop: stopped");
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        release();
    }

    /**
     * 当调用seekTo的时候才会回调这里，否则不会回调
     * @param position
     */
    @Override
    public void seekTo(long position) {
        if (mExoPlayer != null) {
            mExoPlayer.seekTo((int) position);


            // Set the state (to the current state) because the position changed and should
            // be reported to clients.
            // but client maynot need this event  ---xym
//            setNewState(mState);
        }
    }

    @Override
    public void setVolume(float volume) {
        if (mExoPlayer != null) {
            mExoPlayer.setVolume(volume);
        }
    }

    private void playFile(MediaMetadataCompat metaData) {

        if (metaData == null || metaData.getDescription() == null) {
            return;
        }
        String mediaId = metaData.getDescription().getMediaId();


        boolean mediaChanged = (mCurrentMedia == null || !mediaId.equals(mCurrentMedia.getDescription().getMediaId()));//是第一次播 或者正在播要打断
        if (mCurrentMediaPlayedToCompletion) {
            // Last audio file was played to completion, the resourceId hasn't changed, but the
            // player was released, so force a reload of the media file for playback.
            mediaChanged = true;
            mCurrentMediaPlayedToCompletion = false;
        }
        if (!mediaChanged) {//新选的这首歌跟以前的一样
            if (!isPlaying()) {
                play();
            }
            return;
        } else {
            release();
        }

        mCurrentMedia = metaData;//换碟

        initializeExoPlayer();
        String uri = metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI);


        MediaSource audioSource = createMediaSource(uri);

        mExoPlayer.prepare(audioSource);
        Log.d(TAG, "onPlayerStateChanged: PREPARE");

        play();
    }

    private MediaSource createMediaSource(String uri) {
        if (uri.startsWith("http")) {
//            return new ExtractorMediaSource.Factory(mDataSourceFactory)//这里根据uri来得到source
//                    .createMediaSource(Uri.parse(uri));
            DefaultBandwidthMeter mDefaultBandwidthMeter =new DefaultBandwidthMeter();
            DefaultDataSourceFactory upstreamFactory =new DefaultDataSourceFactory(mContext,mDefaultBandwidthMeter,new DefaultHttpDataSourceFactory("exoplayer-codelab",null,15000,15000,true));
            return new ExtractorMediaSource.Factory(upstreamFactory).
                    createMediaSource(Uri.parse(uri));
        }


        return createLocalVideoMediaSource(uri);
    }

    /**
     * 创建一个播放本地文件的媒体资源
     *
     * @param uri
     * @return
     */
    public MediaSource createLocalVideoMediaSource(String uri) {
        return mediaSourceFactory.createMediaSource(Uri.parse(uri));
    }

    int delay = 1000;
    private MTimeHandler mTimeHandler = new MTimeHandler(this);

    public void startTrackingPlayback() {//这里可以进行Service Callback通信
        Log.d(TAG, "startTrackingPlayback: 开始播放回调 currentThread: " + Thread.currentThread());
        AnyExtentionKt.runOnMain(this, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                postHandel();
                return Unit.INSTANCE;
            }
        });
    }
    private void postHandel(){
        if(mTimeHandler.hasMessages(MTimeHandler.MSG_TIME_COUNT)){
            mTimeHandler.removeMessages(MTimeHandler.MSG_TIME_COUNT);
        }
        mTimeHandler.sendEmptyMessageDelayed(MTimeHandler.MSG_TIME_COUNT,delay);
    }

    // This is the main reducer for the player state machine.
    private void setNewState(@PlaybackStateCompat.State int newPlayerState) {
        mState = newPlayerState;

        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is set to true.
        if (mState == PlaybackStateCompat.STATE_STOPPED) {
            mCurrentMediaPlayedToCompletion = true;

        }

        final long reportPosition = mExoPlayer == null ? 0 : mExoPlayer.getCurrentPosition();

        // Send playback state information to service
        publishStateBuilder(reportPosition);
    }


    private void publishStateBuilder(long reportPosition) {// the current position in ms, report to server
        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(mState,
                reportPosition,
                1.0f,
                SystemClock.elapsedRealtime());
        if (mCurrentMedia == null) {
            return;
        }
//        mPlaybackInfoListener.updateUI(mCurrentMedia.getDescription().getMediaId());
        mPlaybackInfoListener.updateUI(mCurrentMedia);
        mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build());

    }

    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */
    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (mState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }


    private class ExoPlayerEventListener implements Player.EventListener {


        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_ENDED: {
                    setNewState(PlaybackStateCompat.STATE_PAUSED);
//                    mExoPlayer.setPlayWhenReady(false);//发出播放指令
                    mPlaybackInfoListener.onPlaybackComplete();
                    mCurrentMediaPlayedToCompletion = true;
                    break;
                }
                case Player.STATE_BUFFERING: {
                    Log.d(TAG, "onPlayerStateChanged: BUFFERING");
                    mStartTime = System.currentTimeMillis();
                    break;
                }
                case Player.STATE_IDLE: {

                    break;
                }
                case Player.STATE_READY: {
                    mPlaybackInfoListener.onPlayReady(true);
                    Log.d(TAG, "onPlayerStateChanged: READY");
                    Log.d(TAG, "onPlayerStateChanged: TIME ELAPSED: " + (System.currentTimeMillis() - mStartTime));
                    break;
                }
            }
        }


        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            setNewState(PlaybackStateCompat.STATE_ERROR);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }


    private static class MTimeHandler extends Handler{

        public static final int MSG_TIME_COUNT = 1;
        WeakReference<ExoPlayerAdapter> exoPlayerAdapterWeakReference;
        MTimeHandler(ExoPlayerAdapter exoPlayerAdapter){
            super(Looper.getMainLooper());
            exoPlayerAdapterWeakReference = new WeakReference<>(exoPlayerAdapter);

        }
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            if(msg.what == MSG_TIME_COUNT){
                ExoPlayerAdapter exoPlayerAdapter = exoPlayerAdapterWeakReference.get();
                if(exoPlayerAdapter!=null && exoPlayerAdapter.mState == PlaybackStateCompat.STATE_PLAYING){
                    exoPlayerAdapter
                            .mPlaybackInfoListener.
                            onSeekTo(
                                    exoPlayerAdapter.mExoPlayer.getCurrentPosition(), exoPlayerAdapter.mExoPlayer.getBufferedPosition(), exoPlayerAdapter.mExoPlayer.getDuration()
                    );
                }
                sendEmptyMessageDelayed(MSG_TIME_COUNT,1000);

            }
            super.handleMessage(msg);
        }
    }

}
