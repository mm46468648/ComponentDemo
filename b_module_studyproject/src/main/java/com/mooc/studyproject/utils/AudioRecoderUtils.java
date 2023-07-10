package com.mooc.studyproject.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mooc.commonbusiness.config.DownloadConfig;
import com.mooc.studyproject.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecoderUtils {

    //文件路径
    private String filePath = "";
    //文件夹路径
    private String FolderPath;


//    private MediaRecorder mMediaRecorder;

    MediaRecorder mp3Recorder;

    private MediaPlayer mediaPlayer;

    View playView;
    private final String TAG = "fan";
    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;

    public static final int PLAY_STATE_PREPARE = 21;
    public static final int PLAY_STATE_PLAYING = 22;
    public static final int PLAY_STATE_COMPLETE = 23;
    public static final int PLAY_STATE_PAUSE = 24;
    public static final int PLAY_STATE_CONTINUE = 25;
    public static final int PLAY_STATE_ERROR = 26;


    public static final String CHECK_PERMISSION = "permission";


    private int playState = PLAY_STATE_PREPARE;

    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    private OnPlayStatusUpdateListener playStatusUpdateListener;

    /**
     * 文件存储默认sdcard/record
     */
    public AudioRecoderUtils() {

        //默认保存路径为/sdcard/record/下
        this(DownloadConfig.INSTANCE.getDefaultLocation() + "/record/");
    }

    public void setPlayStatusUpdateListener(OnPlayStatusUpdateListener playStatusUpdateListener) {
        this.playStatusUpdateListener = playStatusUpdateListener;
    }


    public void resetPlayState() {
        stopPlay();
    }


    public AudioRecoderUtils(String filePath) {

        File path = new File(filePath);
        if (!path.exists())
            path.mkdirs();

        this.FolderPath = filePath;
    }

    private long startTime;
    private long endTime;


    public int getPlayState() {
        return playState;
    }

    public void startPlay(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        filePath = path;

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        startPlay();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        handler.sendEmptyMessage(PLAY_STATE_ERROR);
                        return false;
                    }
                });
                return;
            }

            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startPlay();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    handler.sendEmptyMessage(PLAY_STATE_ERROR);
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    private void startPlay() {
        mediaPlayer.start();
        playState = PLAY_STATE_PLAYING;
        totalTime = mediaPlayer.getDuration();
        currentPro = 0;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
            }
        });

        refreshUI();
    }

    public void setCurrentPro(int currentPro) {
        this.currentPro = currentPro;
    }

    public void refreshViewComplete() {
        playimg.setImageResource(R.mipmap.common_ic_voice_play);
        seekBar.setProgress(0);
        startTimeTv.setText("00:00");
    }

    public void refreshViewPrepare() {
        playimg.setImageResource(R.mipmap.common_ic_voice_play);
        totalTimeTv.setText(ToolUtil.formatTime(totalTime));
        seekBar.setMax(totalTime);
        startTimeTv.setText("00:00");
        seekBar.setProgress(0);
    }

    public void refreshViewUpdate(int currentTime) {
        playimg.setImageResource(R.mipmap.common_ic_voice_pause);
        totalTimeTv.setText(ToolUtil.formatTime(totalTime));
        seekBar.setMax(totalTime);
        seekBar.setProgress(currentTime);
        startTimeTv.setText(ToolUtil.formatTime(currentTime));
    }

    public void refreshUI() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(PLAY_STATE_PLAYING);
            }
        }, 0, 1000);


    }


    ImageView playimg;
    SeekBar seekBar;
    TextView startTimeTv, totalTimeTv, playTv;
    Timer timer;


    public void setPlayView(View view) {
        this.playView = view;
        initPlayView();
    }

    public void clearPlayView() {
        this.playView = null;
    }

    private void initPlayView() {
        playimg = (ImageView) playView.findViewById(R.id.play_voice);
        seekBar = (SeekBar) playView.findViewById(R.id.play_prg);
        startTimeTv = (TextView) playView.findViewById(R.id.pro_time);
        totalTimeTv = (TextView) playView.findViewById(R.id.total_time);
        playTv = (TextView) playView.findViewById(R.id.play_tv);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 0) {
                    seekBar.setThumb(context.getResources().getDrawable(R.mipmap.common_ic_voice_seekbar));
                } else {
                    seekBar.setThumb(context.getResources().getDrawable(R.mipmap.common_ic_voice_seekbar_green));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private int currentPro = 0;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PLAY_STATE_PLAYING) {
                if (playState == PLAY_STATE_COMPLETE || mediaPlayer == null) {
                    return;
                }
                currentPro = mediaPlayer.getCurrentPosition();
                if (playStatusUpdateListener != null) {
                    playStatusUpdateListener.onUpdate(currentPro);
                }
            }

            if (msg.what == PLAY_STATE_PAUSE) {
                if (playStatusUpdateListener != null) {
                    playStatusUpdateListener.onPlayPause();
                }
            }
            if (msg.what == PLAY_STATE_CONTINUE) {
                if (playStatusUpdateListener != null) {
                    playStatusUpdateListener.onContinuePlay();
                }
            }
            if (msg.what == PLAY_STATE_COMPLETE) {
                if (playStatusUpdateListener != null) {
                    playStatusUpdateListener.onPlayComplete();
                }
            }
            if (msg.what == PLAY_STATE_ERROR) {
                releasePlay();
                if (playStatusUpdateListener != null) {
                    playStatusUpdateListener.onError();
                }
            }
        }
    };

    public int getCurrentPro() {
        return currentPro;
    }

    public void stopPlay() {
        playState = PLAY_STATE_COMPLETE;
        if (timer != null) {
            timer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        handler.sendEmptyMessage(PLAY_STATE_COMPLETE);

    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }


    public void pausePlay() {
        if (timer != null) {
            timer.cancel();
        }
        playState = PLAY_STATE_PAUSE;
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        handler.sendEmptyMessage(PLAY_STATE_PAUSE);
    }

    public void continuePlay() {
        playState = PLAY_STATE_PLAYING;
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        refreshUI();

    }

    int totalTime;

    public void setTotalTime(int total) {
        totalTime = total;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void releasePlay() {
        if (mediaPlayer == null) {
            return;
        }
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     * @return
     */
    public void startRecord() {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mp3Recorder == null)
            mp3Recorder = new MediaRecorder();
        try {
            /* ②setAudioSource/setVedioSource */
            mp3Recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mp3Recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mp3Recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            filePath = FolderPath  + "newdynamic.mp3";
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            /* ③准备 */
            mp3Recorder.setOutputFile(filePath);
            mp3Recorder.setMaxDuration(MAX_LENGTH);
            mp3Recorder.prepare();
            /* ④开始 */
            mp3Recorder.start();
            isRecordStop=false;

            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
        } catch (Exception e) {
            e.printStackTrace();
            mp3Recorder = null;

            if (null != audioStatusUpdateListener) {
                audioStatusUpdateListener.onStop(CHECK_PERMISSION);
            }
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (mp3Recorder == null)
            return 0L;
        endTime = System.currentTimeMillis();
        totalTime = (int) (endTime - startTime);
        try {
            mp3Recorder.stop();
            isRecordStop=true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        audioStatusUpdateListener.onStop(filePath);
        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        return totalTime;
    }

    public void releaseAudio() {
        delFile();
        handler.removeCallbacksAndMessages(null);
        if (mp3Recorder == null) {
            return;
        }
        try {
            audioStatusUpdateListener.onStop(filePath);
        } catch (RuntimeException e) {
            File file = new File(filePath);
            if (file.exists())
                file.delete();
        }
    }

    public void delFile() {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {

        if (mp3Recorder == null) {
            return;
        }
        try {
            mp3Recorder.stop();
            isRecordStop=false;
            mp3Recorder.reset();
            mp3Recorder.release();
            mp3Recorder = null;

        } catch (RuntimeException e) {
            mp3Recorder.reset();
            isRecordStop=true;
            mp3Recorder.release();
            mp3Recorder = null;
        }
        File file = new File(filePath);
        if (file.exists())
            file.delete();
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };


    private int BASE = 1;
    private Boolean isRecordStop = false;
    private int SPACE = 100;// 间隔取样时间

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {

        if (mp3Recorder != null&&!isRecordStop) {
            if (null != audioStatusUpdateListener) {
                audioStatusUpdateListener.onUpdate(20, System.currentTimeMillis() - startTime);
            }
                mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);

        }
    }


    public String getFilePath() {
        return filePath;
    }


    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        public void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param filePath 保存路径
         */
        public void onStop(String filePath);
    }

    public interface OnPlayStatusUpdateListener {
        void playStart(int totalPro);

        void onUpdate(int currentPro);

        void onPlayComplete();

        void onPlayPause();

        void onContinuePlay();

        void onError();
    }

}