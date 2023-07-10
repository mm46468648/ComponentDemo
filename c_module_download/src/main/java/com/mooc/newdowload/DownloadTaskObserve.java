package com.mooc.newdowload;

/**
 * Created by itheima.
 */
public interface DownloadTaskObserve {
    void update(long id);
    long getDownloadId();  //下载id，根据特定规则生成
}
