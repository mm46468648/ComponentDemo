package com.mooc.commonbusiness.utils;

import android.content.Context;

import com.mooc.commonbusiness.constants.SpConstants;
import com.mooc.commonbusiness.pop.OnlyWifiDownloadTipPop;
import com.lxj.xpopup.XPopup;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.NetUtils;
import com.mooc.common.utils.sharepreference.SpDefaultUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;


/**
 * Description: 下载工具类
 * Created by jia on 2017/11/30.
 * 人之所以能，是相信能
 */
public class DownloadUtils {

    private static final String TAG = "DownloadUtils";


    public static boolean DownloadSmallFile(final String uri, final String filePath) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(uri.toString()).build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }

            ResponseBody body = response.body();
            long contentLength = body.contentLength();
            BufferedSource source = body.source();
            File file = new File(filePath);
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(source);
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 检查当前网络状态是否可以下载
     *
     * @param context
     * @param
     * @return true 就是可以下载
     */
    public static boolean checkNetStateCanDownload(final Context context) {
        if (!NetUtils.isNetworkConnected()) {
            AnyExtentionKt.toast("", "请检查网络链接");
            return false;
        }
        //如果是wifi，直接下载
        if (NetUtils.getNetworkType().equals("WIFI")) {
            return true;
        }
        //如果不是wifi
        boolean onlyWifi = SpDefaultUtils.getInstance().getBoolean(SpConstants.ONLY_WIFI_DOWNLOAD, true);
        if (!onlyWifi) { //是否设置了仅wifi
            AnyExtentionKt.toast("", "当前为非Wifi环境，请注意流量资费");
        }else {
            new XPopup.Builder(context)
                    .asCustom(new OnlyWifiDownloadTipPop(context))
                    .show();
        }
        return !onlyWifi;
    }
}
