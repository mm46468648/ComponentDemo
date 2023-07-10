package com.mooc.newdowload;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 下载数据封装
 */
@Entity(tableName = "downloadinfo") //表名
public class DownloadInfo {
    @PrimaryKey
    public Long id;
    public int state;
    public long downloadSize;     //已下载大小
    public long size;        //文件大小
    public String downloadUrl;
    public String downloadPath;
    public String fileName;

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "id=" + id +
                ", state=" + state +
                ", downloadSize=" + downloadSize +
                ", size=" + size +
                '}';
    }
}
