package com.mooc.studyproject.followup;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.mooc.common.utils.GsonManager;
import com.mooc.common.utils.Md5Util;
import com.mooc.common.utils.sharepreference.SpUtils;
import com.mooc.commonbusiness.constants.SpConstants;
import com.mooc.studyproject.model.ServerFollowupResult;
import com.tencent.taisdk.TAIError;
import com.tencent.taisdk.TAIOralEvaluation;
import com.tencent.taisdk.TAIOralEvaluationCallback;
import com.tencent.taisdk.TAIOralEvaluationData;
import com.tencent.taisdk.TAIOralEvaluationEvalMode;
import com.tencent.taisdk.TAIOralEvaluationFileType;
import com.tencent.taisdk.TAIOralEvaluationListener;
import com.tencent.taisdk.TAIOralEvaluationParam;
import com.tencent.taisdk.TAIOralEvaluationRet;
import com.tencent.taisdk.TAIOralEvaluationServerType;
import com.tencent.taisdk.TAIOralEvaluationStorageMode;
import com.tencent.taisdk.TAIOralEvaluationTextMode;
import com.tencent.taisdk.TAIOralEvaluationWorkMode;
import com.tencent.taisdk.TAIRecorderParam;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

public class TecentSpeedUtil {
    TAIOralEvaluation taiOralEvaluation;

    TAIRecorderParam recordParam;
    TAIOralEvaluationParam param;
    Context mContext;
    TencentAudioRecorder recorder;

    public TecentSpeedUtil(Context context) {
        mContext = context;
        if (taiOralEvaluation == null) {
            taiOralEvaluation = new TAIOralEvaluation();
        }
        recorder = new TencentAudioRecorder(mContext);
    }

    public void stopRecord(TAIOralEvaluationCallback callback) {
//        if (taiOralEvaluation != null && taiOralEvaluation.isRecording()) {
//            taiOralEvaluation.stopRecordAndEvaluation(callback);
//        }
        recorder.stop();
    }

    public void pcToWav(TencentAudioRecorder.IPC2WACSucess ipc2WACSucess) {
        recorder.pcToWav(ipc2WACSucess);
    }

    public void stopLocalOral() {
        if (taiOralEvaluation != null) {
//            taiOralEvaluation.stopRecordAndEvaluation(new TAIOralEvaluationCallback() {
//                @Override
//                public void onResult(TAIError error) {
//
//                }
//            });

            taiOralEvaluation.stopRecordAndEvaluation();
        }
    }

    public void startRecord(String text, String resourceId, TAIOralEvaluationListener listener, TAIOralEvaluationCallback callback) {
        initParams(mContext);
        param.refText = text;
        saveMp3(resourceId + text);
        taiOralEvaluation.setListener(listener);
        recorder.start();
//        taiOralEvaluation.startRecordAndEvaluation(param, callback);
    }

    private String currenMp3;

    public String getCurrenMp3() {
        return currenMp3;
    }

    public void setCurrenMp3(String currenMp3) {
        this.currenMp3 = currenMp3;
    }

    private void initParams(Context context) {
        param = new TAIOralEvaluationParam();
        param.context = context;
        param.sessionId = UUID.randomUUID().toString();
        param.appId = PrivateInfo.appId;
        param.soeAppId = PrivateInfo.soeAppId;
        param.secretId = PrivateInfo.secretId;
        param.secretKey = PrivateInfo.secretKey;
        param.token = PrivateInfo.token;
        param.workMode = TAIOralEvaluationWorkMode.STREAM;      //流式传输
        param.evalMode = TAIOralEvaluationEvalMode.PARAGRAPH;  //段落
        param.fileType = TAIOralEvaluationFileType.MP3;
        param.storageMode = TAIOralEvaluationStorageMode.ENABLE;   //允许存储
        param.serverType = TAIOralEvaluationServerType.CHINESE;    //中午评测
        param.textMode = TAIOralEvaluationTextMode.NORMAL;       //普通文本
        param.scoreCoeff = 1.0;                                    //苛刻指数 [1-4]
        param.audioPath = context.getExternalFilesDir("followUp").getAbsolutePath() + "/" + param.sessionId + ".mp3";
        recorder.setWavPath(context.getExternalFilesDir("followUp").getAbsolutePath() + "/" + param.sessionId + ".wav");
        currenMp3 = recorder.getWavFileName();

        if (param.workMode == TAIOralEvaluationWorkMode.STREAM) {
            param.timeout = 5;
            param.retryTimes = 5;
        } else {
            param.timeout = 30;
            param.retryTimes = 0;
        }

        recordParam = new TAIRecorderParam();
        recordParam.fragSize = 1024;
        recordParam.fragEnable = true;
        recordParam.vadEnable = true;
        recordParam.vadInterval = 5000;
        taiOralEvaluation.setRecorderParam(recordParam);

    }


    byte[][] bytes;

    int currentPosition = 0;
    int total = 0;

    int cutNum = 1;

    private int upProgress = 0;
    int partTime = 10;

    public int getUpProgress() {
        return upProgress;
    }

    public void oralLocalMp3(Context context, String refTeXT, String file, int recoredTime, upProgress upCallBack, TAIOralEvaluationListener listener) {


        if (recoredTime == 0) {
            recoredTime = 60;
        }
        //按30秒一个时间片
        cutNum = recoredTime < partTime ? 1 : 1 + recoredTime / partTime;
        currentPosition = 0;
        taiOralEvaluation.setListener(new TAIOralEvaluationListener() {
            @Override
            public void onEvaluationData(TAIOralEvaluationData data, TAIOralEvaluationRet result) {


                if (currentPosition != total - 1) {
                    currentPosition++;
                    upProgress = 100 * currentPosition / total;
                    Log.i("msg", "upProgress" + upProgress + ":" + currentPosition + ":" + total);
                    if (upCallBack != null) {
                        upCallBack.upProgress(upProgress);
                    }
                    oralPartBuffer(param, currentPosition + 1, currentPosition == total - 1, getByteWithPosition(currentPosition, bytes));
                } else {
                    if (listener != null) {
//                        listener.onEvaluationData(taiOralEvaluationData, taiOralEvaluationRet, taiError);
                        listener.onEvaluationData(data, result);
                    }
                }
            }

            @Override
            public void onEvaluationError(TAIOralEvaluationData data, TAIError taiError) {
                if (taiError.code != 0) {
                    if (upCallBack != null) {
                        Log.i("msg", "TAIError" + taiError.desc + ":   code" + taiError.code);
                        upCallBack.onUpError(taiError);
                    }
                    return;
                }
            }

            @Override
            public void onFinalEvaluationData(TAIOralEvaluationData data, TAIOralEvaluationRet result) {

            }

            @Override
            public void onEndOfSpeech(boolean isSpeak) {

            }

            @Override
            public void onVolumeChanged(int i) {

            }
        });
        param = new TAIOralEvaluationParam();
        param.context = context;
//        param.sessionId = UUID.randomUUID().toString();
//        param.appId = PrivateInfo.appId;
//        param.soeAppId = PrivateInfo.soeAppId;
//        param.secretId = PrivateInfo.secretId;
//        param.secretKey = PrivateInfo.secretKey;
//        param.workMode = TAIOralEvaluationWorkMode.STREAM;
//        param.evalMode = TAIOralEvaluationEvalMode.PARAGRAPH;
//        param.storageMode = TAIOralEvaluationStorageMode.ENABLE;
//        param.fileType = TAIOralEvaluationFileType.WAV;
//        param.serverType = TAIOralEvaluationServerType.CHINESE;
//        param.textMode = TAIOralEvaluationTextMode.NORMAL;
//        param.scoreCoeff = 1.0;
        param.refText = refTeXT;


        param.sessionId = UUID.randomUUID().toString();
        param.appId = PrivateInfo.appId;
        param.soeAppId = PrivateInfo.soeAppId;
        param.secretId = PrivateInfo.secretId;
        param.secretKey = PrivateInfo.secretKey;
        param.workMode = TAIOralEvaluationWorkMode.STREAM;
        param.evalMode = TAIOralEvaluationEvalMode.PARAGRAPH;
        param.storageMode = TAIOralEvaluationStorageMode.DISABLE;
        param.fileType = TAIOralEvaluationFileType.WAV;
        param.serverType = TAIOralEvaluationServerType.CHINESE;
        param.textMode = TAIOralEvaluationTextMode.NORMAL;
        param.scoreCoeff =1.0;


        InputStream is = null;
        try {
            is = new FileInputStream(file);

            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            bytes = splitBytes(buffer, buffer.length / cutNum);
            total = bytes.length;


            oralPartBuffer(param, currentPosition + 1, currentPosition == total - 1, getByteWithPosition(currentPosition, bytes));


        } catch (Exception e) {

        }


    }


    public void oralPartBuffer(TAIOralEvaluationParam param, int seqid, boolean bEnd, byte[] buffer) {

        TAIOralEvaluationData data = new TAIOralEvaluationData();
        data.seqId = seqid;
        data.bEnd = bEnd;
        data.audio = buffer;

        Log.i("msg", data.seqId + ":" + data.bEnd + ":" + data.audio);
//        taiOralEvaluation.oralEvaluation(param, data, new TAIOralEvaluationCallback() {
//            @Override
//            public void onResult(TAIError taiError) {
//
//                Log.i("msg", "oralPartBuffer  TAIError");
//
//                if (upCallBack != null) {
//                    upCallBack.onUpError(taiError);
//                }
//            }
//        });

        try {
            taiOralEvaluation.oralEvaluation(param, data);
        }catch (Exception e){
            Log.e("TEST", "oralPartBuffer: " + e.toString());
        }

    }

    private byte[] getByteWithPosition(int position, byte[][] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (i == position) {
                byte[] set = new byte[bytes[i].length];
                for (int i1 = 0; i1 < bytes[i].length; i1++) {
                    set[i1] = bytes[i][i1];
                }
                return set;
            }

        }
        return null;
    }


    public byte[][] splitBytes(byte[] bytes, int size) {
        int partLength = 100 * 1024;
        if (bytes.length < 100 * 1024) {
            partLength = 10 * 1024;
        }

        total = bytes.length / partLength + 1;
        byte[][] result = new byte[total][];
        int from, to;
        int i = 0;
        while (i * partLength < bytes.length) {
            from = i * partLength;
            i++;
            to = (int) (i * partLength);
            if (to > bytes.length)
                to = bytes.length;
            result[i - 1] = Arrays.copyOfRange(bytes, from, to);
        }

        return result;
    }


    public String getLastMp3(@NotNull String resourceId) {
        String key = Md5Util.getMD5Str(resourceId);
//        return SpUtils.get().getValue(key, "");

        String value = SpUtils.get().getValue(SpConstants.SP_FOLLOWUP_UPLAOD_STATE, "");
        String path = "";
        if(!TextUtils.isEmpty(value)){
            ArrayList<ServerFollowupResult> arrayList = GsonManager.getInstance().fromJson(value,new TypeToken<ArrayList<ServerFollowupResult>>(){}.getType());
//            for(ServerFollowupResult s : arrayList){
//                if(key.equals(s.getId())){
//                    path = s.getNotUploadFilePath();
//                    break;
//                }
//            }
            for (int i = arrayList.size()-1; i >= 0; i--) {
                ServerFollowupResult s = arrayList.get(i);
                if(key.equals(s.getId())){
                    path = s.getNotUploadFilePath();
                    break;
                }
            }
        }
        return path;
    }

    public void saveMp3(String resourceId) {
        String key = Md5Util.getMD5Str(resourceId);


        String value = SpUtils.get().getValue(SpConstants.SP_FOLLOWUP_UPLAOD_STATE, "");
        ArrayList<ServerFollowupResult> arrayList = new ArrayList<>();
        if(!TextUtils.isEmpty(value)){
            arrayList = GsonManager.getInstance().fromJson(value,new TypeToken<ArrayList<ServerFollowupResult>>(){}.getType());
        }

//        if(arrayList.size()>10){
//            arrayList.clear();
//        }
        arrayList.add(new ServerFollowupResult(key, false,currenMp3));
        SpUtils.get().putValue(SpConstants.SP_FOLLOWUP_UPLAOD_STATE, GsonManager.getInstance().toJson(arrayList));
    }

    public void deleteMp3Key(String resourceId){

        String key = Md5Util.getMD5Str(resourceId);

        String value = SpUtils.get().getValue(SpConstants.SP_FOLLOWUP_UPLAOD_STATE, "");
        if(!TextUtils.isEmpty(value)){
            ArrayList<ServerFollowupResult> arrayList = GsonManager.getInstance().fromJson(value,new TypeToken<ArrayList<ServerFollowupResult>>(){}.getType());
            Iterator<ServerFollowupResult> iterator = arrayList.iterator();
            while (iterator.hasNext()) {
                ServerFollowupResult next = iterator.next();
                if (key.equals(next.getId())) {
                    iterator.remove();
                }
            }
//            arrayList.remove(new ServerFollowupResult(key, false,currenMp3));
            SpUtils.get().putValue(SpConstants.SP_FOLLOWUP_UPLAOD_STATE, GsonManager.getInstance().toJson(arrayList));
        }

    }


    upProgress upCallBack;

    public TecentSpeedUtil.upProgress getUpCallBack() {
        return upCallBack;
    }

    public void setUpCallBack(TecentSpeedUtil.upProgress upCallBack) {
        this.upCallBack = upCallBack;
    }

    public interface upProgress {
        void upProgress(int progress);

        void onUpError(TAIError taiError);
    }
}
