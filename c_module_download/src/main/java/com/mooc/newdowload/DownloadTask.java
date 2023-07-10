package com.mooc.newdowload;


import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import com.mooc.download.db.DownloadDatabase;
import com.mooc.download.util.UrlUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 下载任务
 */
public class DownloadTask extends ThreadPoolUtils.Task {

    private final DownloadInfo downloadInfo;

    public DownloadTask(Long id) {
        this.id = id;
        downloadInfo = DownloadManager.DOWNLOAD_INFO_HASHMAP.get(id);
    }

    @Override
    protected void work() {
        downloadFile();
    }

//    int time = 0;

    private void downloadFile() {
        InputStream in = null;
        FileOutputStream out = null;

        try {

//            Log.e("downloadTask", "hashCode" + hashCode() +  "rangeStart: " + downloadInfo.downloadSize);


            //真正开始在子线中中下载apk
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(downloadInfo.downloadUrl)
                    .addHeader("Referer", "http://*.learning.mil.cn")
                    .addHeader("Range", "bytes=" + (downloadInfo.downloadSize > 0 ? downloadInfo.downloadSize : 0) + "-")
                    .build();


            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {

                ResponseBody body = response.body();


                in = body.byteStream();
                //1.创建文件夹
                File fileDir = new File(downloadInfo.downloadPath);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }

                if (TextUtils.isEmpty(downloadInfo.fileName)) {
                    throw new Exception("fileName can not be empty");
                }
                DownloadDatabase.Companion.getDatabase().getDownloadDao().insert(downloadInfo);
                //fileName 下载到Sdcard的/data/android/包名/apk/filename
                File saveFile = new File(fileDir, downloadInfo.fileName);

                if (downloadInfo.downloadSize == 0) {
                    //文件总大小
                    downloadInfo.size = body.contentLength();
                    //1.覆盖写入
                    out = new FileOutputStream(saveFile, false);

                    Log.e("downloadTask", "hashCode" + hashCode() +  "writeCover");

                } else {
                    //写文件的时候,以追加的方式去写
                    out = new FileOutputStream(saveFile, true);

                    Log.e("downloadTask", "hashCode" + hashCode() +  "writeAppEnd");

                }



                int len = 0;
                byte[] buffer = new byte[1024];

                setState(State.DOWNLOADING);
                while (true) {
                    if (downloadInfo.state == State.DOWNLOAD_STOP) {
                        setState(State.DOWNLOAD_STOP);
                        break;
                    }
                    len = in.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    out.write(buffer, 0, len);
                    // 显示进度
                    downloadInfo.downloadSize += len;
//                    if (time == 20) {
//                        Log.e("downloadTask", "hashCode: " + hashCode() + " total " + downloadInfo.size + " current: " + downloadInfo.downloadSize);
//                        time = 0;
//                    }else {
//                        time++;
//                    }
                    DownloadManager.getInstance().notifyObserves(downloadInfo.id);
                }
                if (downloadInfo.downloadSize == downloadInfo.size) {
                    // 显示完成
                    setState(State.DOWNLOAD_COMPLETED);
                }

                if (downloadInfo.downloadSize > downloadInfo.size) {
                    // size变为0 显示异常状态
                    downloadInfo.downloadSize = 0;
                    setState(State.DOWNLOAD_ERROR);
                }
            } else {
                // 显示重试
                setState(State.DOWNLOAD_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 显示重试
            setState(State.DOWNLOAD_ERROR);
        } finally {
            IOUtils.close(out);
            IOUtils.close(in);
        }


    }


    public void setState(int state) {
        downloadInfo.state = state;

        //如果是异常状态,需要重新下载
//        if (downloadInfo.state == State.DOWNLOAD_ERROR) {
//            downloadInfo.downloadSize = 0;
//        }
        DownloadManager.getInstance().notifyObserves(downloadInfo.id);

        //数据库相关,存储
        DownloadDatabase.Companion.getDatabase().getDownloadDao().update(downloadInfo);
    }
}