package com.mooc.download.strategy

import android.os.Looper
import com.mooc.download.DownloadModel
import com.mooc.download.exception.DownloadException
import com.mooc.download.exception.DownloadPauseException
import com.mooc.download.util.FileUtil
import okhttp3.Response

/**
 * 写入到文件策略
 */
abstract class WriteStrategy {
    val mHandler = android.os.Handler(Looper.getMainLooper())

    abstract fun writeResponseToDisk(downloadInfo: DownloadModel, response: Response)


    fun checkPause(downloadInfo: DownloadModel) {
        if (downloadInfo.isPause()) {
            throw DownloadPauseException(DownloadException.EXCEPTION_PAUSE)
        }
    }

    /**
     * 回调运行在主线程
     */
    fun runOnMain(block:()->Unit){
        mHandler.post(block)
    }

    /**
     * 检测剩余可用空间
     */
    fun checkAvailableSize(downloadSize:Long,filePath:String) : Boolean{
        return FileUtil.checkDownloadPathSize(downloadSize,filePath)
    }
}