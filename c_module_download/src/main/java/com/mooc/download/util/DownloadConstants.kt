package com.mooc.download.util

class DownloadConstants {
    companion object{
        const val TYPE_COURSE = "1"    //课程
        const val TYPE_AUDIO = "2"      //音频
        const val TYPE_EBOOK = "3"     //电子书


        //错误码
        const val ERROR_UNKNOWN = -1 //未知错误
        const val ERROR_SD_FULL = 1 //磁盘空间慢
        const val ERROR_NET = 2 //网络异常
        const val ERROR_WAIT_QUEUE_FULL = 3//等待队列已满


        const val DOWNLOAD_EXTRA_STATE_NOT = 0;
        const val DOWNLOAD_EXTRA_STATE_PREPARE = 1;
        const val DOWNLOAD_EXTRA_STATE_SUCCESS = 2;
        const val DOWNLOAD_EXTRA_STATE_FAIL = 3;
    }


}