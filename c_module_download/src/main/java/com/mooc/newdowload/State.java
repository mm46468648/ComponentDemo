package com.mooc.newdowload;

/**
 * 应用下载状态
 */
public interface State {
    // 未下载
    int DOWNLOAD_NOT = 0;
    // 下载完成
    int DOWNLOAD_COMPLETED = 5;
    // 下载等待
    int DOWNLOAD_WAIT = 3;
    // 下载暂停
    int DOWNLOAD_STOP = 4;
    // 下载中
    int DOWNLOADING = 2;
    // 下载过程中出现异常
    int DOWNLOAD_ERROR = -1;
}
