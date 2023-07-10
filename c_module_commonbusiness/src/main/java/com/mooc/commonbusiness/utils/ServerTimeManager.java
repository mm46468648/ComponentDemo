package com.mooc.commonbusiness.utils;

import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.GsonManager;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.net.ApiService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 服务器时间管理者
 */
public class ServerTimeManager {
    private static ServerTimeManager instance;
    private long differenceTime;        //以前服务器时间 - 以前服务器时间的获取时刻的系统启动时间
    private boolean isServerTime;       //是否是服务器时间

    private ServerTimeManager() {
    }
    public static ServerTimeManager getInstance() {
        if (instance == null) {
            synchronized (ServerTimeManager.class) {
                if (instance == null) {
                    instance = new ServerTimeManager();
                }
            }
        }
        return instance;
    }

    /**
     * 获取当前时间
     *
     * @return the time
     */
    public synchronized long getServiceTime() {
        if (!isServerTime) {
            //获取服务器时间操作
            getServerTime();
            return System.currentTimeMillis();
        }

        //时间差加上当前手机启动时间就是准确的服务器时间了
        return differenceTime + SystemClock.elapsedRealtime();
    }

    /**
     * 获取服务器时间
     */
    private void getServerTime() {
        String getTimeUrl = ApiService.BASE_URL +  "/api/mobile/studyplan/checkin/timestamp/";
        Request request = new Request.Builder()
                .addHeader("User-Agent", SystemUtils.getUserAgent())
                .url(getTimeUrl).build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                AnyExtentionKt.loge(this,e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    HttpResponse<Long> r = GsonManager.getInstance().fromJson(response.body().string(), new TypeToken<HttpResponse<Long>>() {}.getType());
                    initServerTime(r.getData() * 1000);
//                    AnyExtentionKt.loge(this,"获取服务器时间：" + r.getData() * 1000);
                }catch (Exception e){

                }

            }
        });

    }

    /**
     * 时间校准
     *
     * @param lastServiceTime 当前服务器时间
     * @return the long
     */
    public synchronized long initServerTime(long lastServiceTime) {
        //记录时间差
        differenceTime = lastServiceTime - SystemClock.elapsedRealtime();
        isServerTime = true;
        return lastServiceTime;
    }
}
