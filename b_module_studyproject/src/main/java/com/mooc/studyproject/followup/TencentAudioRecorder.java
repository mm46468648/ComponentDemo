package com.mooc.studyproject.followup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;


import com.mooc.common.utils.rxjava.RxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class TencentAudioRecorder {

    // 音频源：音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;

    // 音频通道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;

    // 音频格式：PCM编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 需要申请的运行时权限
     */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private static final int MY_PERMISSIONS_REQUEST = 1001;
    private String pcmFileName;
    private String wavFileName;
    private final String path = Environment.getExternalStorageDirectory() + "/tai/";
    private AudioRecord audioRecord = null;  // 声明 AudioRecord 对象
    private int recordBufSize = 0; // 声明recoordBufffer的大小字段
    private byte[] buffer;
    private boolean isRecording;

    public String getWavFileName() {
        return wavFileName;
    }

    public TencentAudioRecorder(Context context) {

        pcmFileName = context.getExternalFilesDir("followUp").getAbsolutePath() + "/" + "record.pcm";
        wavFileName = context.getExternalFilesDir("followUp").getAbsolutePath() + "/" + "record.wav";

    }

    public void stop() {
        isRecording = false;

        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }


    }

    @SuppressLint("CheckResult")
    public void pcToWav(IPC2WACSucess ipc2WACSucess) {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(@SuppressLint("CheckResult") @NonNull ObservableEmitter<Integer> emitter) throws Exception {

                try {
                    Pcm2WavUtil.pcmToWav(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL,
                            recordBufSize, pcmFileName, wavFileName);
                    emitter.onNext(1);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }

            }
        }).compose(RxUtils.applySchedulers()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (ipc2WACSucess != null) {
                    ipc2WACSucess.sucess();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (ipc2WACSucess != null) {
                    ipc2WACSucess.faile();
                }
            }
        });

    }


    public interface IPC2WACSucess {
        void sucess();

        void faile();
    }

    public void setWavPath(String path) {
        wavFileName = path;
    }

    public void start() {

        //audioRecord能接受的最小的buffer大小
        recordBufSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, recordBufSize);
        buffer = new byte[recordBufSize];

        audioRecord.startRecording();
        isRecording = true;

        new Thread(() -> {
            FileOutputStream os = null;

            try {
                if (!new File(path).exists()) {
                    boolean a = new File(path).mkdirs();
                }

                if (!new File(pcmFileName).exists()) {
                    boolean b = new File(pcmFileName).createNewFile();
                }
                os = new FileOutputStream(pcmFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != os) {
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, recordBufSize);

                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os.write(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
