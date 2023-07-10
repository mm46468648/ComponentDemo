package com.mooc.newdowload;



import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 下载过程中管理
 */
public class DownloadManager {
//    public static HashMap<Long, DownloadInfo> DOWNLOAD_INFO_HASHMAP = new HashMap<Long, DownloadInfo>();
    public static ConcurrentHashMap<Long, DownloadInfo> DOWNLOAD_INFO_HASHMAP = new ConcurrentHashMap<Long, DownloadInfo>();

    Handler mHandler = new Handler(Looper.getMainLooper());
    private static DownloadManager instance = new DownloadManager();
    public static DownloadManager getInstance() {
        return instance;
    }

    // 会有多个界面一同更新，创建一个集合，将需要更新将的对象缓存起来。当界面更新时循环该集合，通知集合中元素数据更新了
    public static CopyOnWriteArrayList<DownloadTaskObserve> OBSERVES = new CopyOnWriteArrayList<>();
    public void removeObserve(DownloadTaskObserve observe) {
        OBSERVES.remove(observe);
    }
    public void addObserve(DownloadTaskObserve observe) {
        OBSERVES.add(observe);
    }

    public void notifyObserves(final long appId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DownloadTaskObserve item : OBSERVES) {
                    item.update(appId);
                }
            }
        });
    }

    /**
     * 开始下载
     */
    public void download(long id) {
        DownloadTask task = new DownloadTask(id);
        boolean execute = ThreadPoolUtils.execute(task);
        if (!execute) {
            // 等待下载
            task.setState(State.DOWNLOAD_WAIT);
        }
    }

    public synchronized void clearAllTask(){
        Set<Long> longs = DOWNLOAD_INFO_HASHMAP.keySet();
        for (Long l : longs){
            DownloadInfo downloadInfo = DOWNLOAD_INFO_HASHMAP.get(l);
            if(downloadInfo ==null){
                continue;
            }
            //清除所有等待任务,所有下载中的任务暂停
            if(downloadInfo.state == State.DOWNLOAD_WAIT ||downloadInfo.state == State.DOWNLOADING){
                handleDownload(l);
            }
        }

        DOWNLOAD_INFO_HASHMAP.clear();

    }

    /**
     * 取消下载任务
     *
     * @param id
     */
    public void deleteQueueTask(long id) {
        boolean cancelWaitTask = ThreadPoolUtils.cancelWaitTask(id);
        if (cancelWaitTask) {

            DownloadInfo downloadInfo = DOWNLOAD_INFO_HASHMAP.get(id);
            if(downloadInfo.downloadSize>0){
                downloadInfo.state = State.DOWNLOAD_STOP;
            }else {
                downloadInfo.state = State.DOWNLOAD_NOT;
            }
            notifyObserves(id);
        }
    }

    public void setState(Long id, int state) {
        DOWNLOAD_INFO_HASHMAP.get(id).state = state;
    }

    /**
     * 处理下载事件
     * @param id
     */
    public void handleDownload(long id){
        DownloadInfo downLoadInfo = DownloadManager.DOWNLOAD_INFO_HASHMAP.get(id);

        if (downLoadInfo == null) {
            return;
        }
        switch (downLoadInfo.state) {
            case State.DOWNLOAD_COMPLETED:
                // 已经下载完成
                break;
            case State.DOWNLOAD_STOP:
                // 暂停
            case State.DOWNLOAD_ERROR:
                // 出错，重试
            case State.DOWNLOAD_NOT:
                // 未下载
                DownloadManager.getInstance().download(downLoadInfo.id);
                break;
            case State.DOWNLOAD_WAIT:
                // 线程池已满，应用添加到等待队列，用户点击后取消下载
                DownloadManager.getInstance().deleteQueueTask(downLoadInfo.id);
                break;
            case State.DOWNLOADING:
                // 未出现异常，显示下载进度，点击暂停
                DownloadManager.getInstance().setState(downLoadInfo.id, State.DOWNLOAD_STOP);
                break;
        }
    }
}
