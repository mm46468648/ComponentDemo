//package com.example.music.audioheat;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.example.music.bean.MusicData;
//import com.example.music.manager.MusicPlayerManager;
//import com.mooc.commonbusiness.api.HttpService;
//import com.mooc.commonbusiness.modle.audio.BaseAudioModle;
//import com.mooc.commonbusiness.modle.search.TrackBean;
//import com.mooc.commonbusiness.utils.sp.SPUserUtils;
//import com.moocxuetang.common.utils.rxjava.RxUtils;
//import com.moocxuetang.common.utils.studypro.AudioHeat;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.Observer;
//import io.reactivex.annotations.NonNull;
//import io.reactivex.disposables.Disposable;
//import jp.wasabeef.glide.transformations.internal.Utils;
//import okhttp3.ResponseBody;
//
///***
// * 音频打点类，启动定时器打点，上传音频进度
// */
//public class VideoHeatUtil {
//
//    MusicPlayerManager manager;
//    private float speed = 1f;
//
//    public VideoHeatUtil(MusicPlayerManager manager) {
//        this.manager = manager;
//        startTimes();
//    }
//
//    MusicData musicData;
//
//    boolean isPlayReady = false;
//
//    boolean isPlaying = false;
//
//    boolean isSeeking = false;
//
//
//    int state = HEAT;
//
//    private static final int PAUSE = 0;
//    private static final int PLAY = 1;
//    private static final int SEEKING = 2;
//    private static final int END = 3;
//    private static final int HEAT = 5;
//
//    TrackBean trackBean;
//
//    public void setSeeking(boolean seeking) {
//        isSeeking = seeking;
//        state = SEEKING;
//        et = "seeking";
////        runHeat();
//    }
//
//    public void setTrackBean(TrackBean trackBean) {
//        this.trackBean = trackBean;
//        //切换音频上传一次
//        postXimaDuration(trackBean);
//    }
//
//
//    public void setIsPlaying(boolean playing) {
//        isPlaying = playing;
//        if (isPlaying) {
//            state = PLAY;
//            et = "play";
//            videoHeat();
//        } else {
//            state = PAUSE;
//            et = "pause";
//            postXimaDuration(trackBean);
//            runHeat();
//        }
//
//    }
//
//    public void setPlayReady(boolean playReady) {
//        isPlayReady = playReady;
//        state = PLAY;
//    }
//
//    public void setMusicData(MusicData musicData) {
//        this.musicData = musicData;
//        state = HEAT;
//        //播放到末尾,打个点并停止打点
//        if (musicData.getProgress() == musicData.getDuration()) {
//            state = END;
//            et = "videoend";
//            videoHeat();
//            postXimaDuration(trackBean);
//        } else {
//            state = HEAT;
//            et = "heartbeat";
//        }
//
//    }
//
//
//    private void startTimes() {
//        Observable.interval(5, TimeUnit.SECONDS).compose(RxUtils.applySchedulers())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//
//                    @Override
//                    public void onNext(@NonNull Long aLong) {
//
//                        //只在播放时打点
//                        videoHeat();
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
//
//
//
//
//    private String pg = "";//页面编号；取值<videoid>_<随机数>
//    private String p = "android";//终端类型；
//    private String i = "5";//心跳间隔，当前默认为5s；
//    private String et = "heartbeat";
//    private String fp = "0";
//    private String tp = "0";
//    private String cp = "0";
//    private String sp = "1.0";
//    private String ts = String.valueOf(System.currentTimeMillis());
//    private String u = "";
//    private String c = "";
//    private String v = "";
//    private String cc = "";
//    private String d = "";
//    private int sq = 1;
//    int CCC = 0;
//
//    public void videoHeat() {
//        //没有音频，数据、没播放时不操作
//        if (manager.getCurrentPositon() == -1 || musicData == null || !manager.isPlaying()) {
//            return;
//        }
//
//        if (state == SEEKING) {
//            return;
//        }
//        if (state == HEAT) {
//            sq++;
//            fp = musicData.getProgress() / 1000 + "";
//        }
//        if (state == PLAY) {
//            sq++;
//        }
//
//        //执行打点
//        runHeat();
//
//    }
//
//    private void runHeat() {
//        String id = SPUserUtils.getInstance().getString(SPUserUtils.KEY_USER_ID, "");
//        if (TextUtils.isEmpty(id)) {
//            id = "!" + getSessionId();
//        }
//        if (et.equals("seeking") && fp.equals("0") && tp.equals("0")) {
//            return;
//        }
//        ts = String.valueOf(System.currentTimeMillis());
//        u = id;
//        c = "s_audio";
//        String videoId = "";
//
////        videoId = manager.getCurrentMusicBean().getId() + "";
//        Log.i("XimaUtils", "打点:" + videoId);
//        if (AudioHeat.studyPlanIds.containsKey(String.valueOf(videoId))) {
//            videoId = "p_" + AudioHeat.studyPlanIds.get(String.valueOf(videoId)) + "_" + videoId;
//            Log.i("XimaUtils", "获取planId:" + videoId);
//        }
//
//        v = videoId;
//        cc = videoId;
//        cp = musicData.getProgress() / 1000 + "";
//        sp = String.valueOf(getPlaySpeed());
//        d = musicData.getDuration() / 1000 + "";
//        java.util.Random random = new java.util.Random();
////        pg = videoId + "_" + get6MD5WithString(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
//
//
//        //如果旧域名可用先用旧域名
//        if (isOldProxyCanUse) {
//            HttpService.Companion.getHeatApi().videoPlayHeatMooc(i, et, p, cp, fp, tp, sp, ts, u, c, v, cc, d, pg, sq + "").enqueue(new retrofit2.Callback<ResponseBody>() {
//                @Override
//                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//
//                }
//
//                @Override
//                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
//                    isOldProxyCanUse = false;
//                    useNewProxyVideoHeat();
////                    saveToDb();
//                }
//            });
//        } else {
//            useNewProxyVideoHeat();
//        }
//    }
//
//    //旧域名是否可用
//    boolean isOldProxyCanUse = true;
//
//    private void useNewProxyVideoHeat() {
//        HttpService.Companion.getHeatApi().videoPlayHeat(i, et, p, cp, fp, tp, sp, ts, u, c, v, cc, d, pg, sq + "").enqueue(new retrofit2.Callback<ResponseBody>() {
//            @Override
//            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
////                saveToDb();
//            }
//        });
//    }
//
//    private String getSessionId() {
//        String sessionNum;
//        try {
//            int randomNum = (int) (Math.random() * 10000);
//            sessionNum = FileNameGenerator.generator(System.currentTimeMillis() + "" + randomNum);
//        } catch (Exception e) {
//            sessionNum = System.currentTimeMillis() + "";
//        }
//        return sessionNum;
//    }
//
//    public float getPlaySpeed() {
//        return speed;
//    }
//
//    private void postXimaDuration(TrackBean track) {
//        if (track == null || musicData == null) {
//            return;
//        }
//        JSONObject requestData = new JSONObject();
//        try {
//            requestData.put("track_id", track.getId());
//            requestData.put("duration", musicData.getProgress());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        HttpService.Companion.getHeatApi().postLearnDuration(OkhttpUtil.getRequestBody(requestData)).compose(RxUtils.<BaseAudioModle>applySchedulers())
//                .subscribe(new Observer<BaseAudioModle>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(BaseAudioModle ximaPostDurationBean) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
//}
