package com.mooc.commonbusiness.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mooc.commonbusiness.R;
import com.mooc.commonbusiness.manager.VoicePlayerManager;


/**
 * 音频播放器控制器
 */
public class VoicePlayerController extends FrameLayout {


    private Context mContext;
    ImageView playImg;
    SeekBar seekBar;
    TextView startTimeTv;
    TextView totalTime;
    TextView playTv;
    private String playPath;     //播放路径
    private long totalLength;    //播放时常
    private boolean isPlaying; //是否播放

    public static final String TAG = "voiceplayercontroller";
    public static final int MESSAGE_TIMER = 0;
    private MediaPlayer mediaPlayer;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_TIMER && isPlaying) {
                int currentPro = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPro);
                startTimeTv.setText(formatTime(currentPro));
                handler.sendEmptyMessageDelayed(MESSAGE_TIMER, 1000);
            }
        }
    };
    private static final int ONE_HOUR = 60 * 60 * 1000;
    /**one minute in ms*/
    private static final int ONE_MIN = 60 * 1000;
    /**one second in ms*/
    private static final int ONE_SECOND = 1000;
    public static String formatTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int hour = (int) (ms / ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }
    public VoicePlayerController(@NonNull Context context) {
        this(context, null);
    }

    public VoicePlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoicePlayerController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.common_layout_voice_player, this);
        playTv = findViewById(R.id.play_tv);
        playImg = findViewById(R.id.play_voice);
        seekBar = findViewById(R.id.play_prg);
        startTimeTv = findViewById(R.id.pro_time);
        totalTime = findViewById(R.id.total_time);

        playTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    playPause();
                } else {
                    startPlay();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer!=null){
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    private void playPause() {
        if (mediaPlayer != null){
            mediaPlayer.pause();
            isPlaying = false;
            playImg.setImageResource(R.mipmap.common_ic_voice_play);

        }
    }

    private void startPlay() {
        if (TextUtils.isEmpty(playPath)) return;
        playImg.setImageResource(R.mipmap.common_ic_voice_pause);


        //如果有音频正在播放则停止音频播放

        if(mediaPlayer != null && !isPlaying){
            mediaPlayer.start();
            isPlaying = true;

            if(handler.hasMessages(MESSAGE_TIMER)){
                handler.removeMessages(MESSAGE_TIMER);
            }
            handler.sendEmptyMessage(MESSAGE_TIMER);
            return;
        }

        setPlayer();
    }

    private void startTimer() {
        handler.sendEmptyMessage(MESSAGE_TIMER);
    }

    public void setPlayPath(String playPath) {
        this.playPath = playPath;
    }

    public String getPlayPath() {
        return playPath;
    }

    public void setTotleTimeLength(String length) {
        try {
            this.totalLength = Long.parseLong(length);
            totalTime.setText(formatTime(totalLength));
            seekBar.setMax((int) totalLength);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重置
     */
    public void reset() {
        isPlaying = false;
        playImg.setImageResource(R.mipmap.common_ic_voice_play);
        totalTime.setText(formatTime(totalLength));
        seekBar.setMax((int) totalLength);
        seekBar.setProgress(0);
        startTimeTv.setText(formatTime(0));
    }

    public void setPlayer() {
        VoicePlayerManager.addVoicePlayerVoiceView(this);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playPath);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    isPlaying = true;
                    startTimer();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    handler.removeMessages(MESSAGE_TIMER);
                    reset();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        release();
    }

    public void release(){
        reset();
        handler.removeCallbacksAndMessages(null);
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
