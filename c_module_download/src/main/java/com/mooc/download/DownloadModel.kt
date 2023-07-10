package com.mooc.download

import android.os.Looper
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.mooc.download.db.DownloadDatabase
import com.mooc.download.util.DownloadConstants
import org.jetbrains.annotations.NotNull
import java.lang.Exception

/**
 * 下载模型
 *
 * primaryKeys (下载地址+存储路径+文件名
 */

class DownloadModel {

    companion object {
        /**
         *无
         */
        const val STATUS_NONE = 0

        /**
         *准备
         * 介于NONE 和 开始中间的状态，有可能是等待网络请求结果返回时的状态
         */
        const val STATUS_PREPARE = 1

        /**
         *下载中
         */
        const val STATUS_DOWNLOADING = 2

        /**
         *等待
         */
        const val STATUS_WAIT = 3

        /**
         *暂停
         */
        const val STATUS_PAUSED = 4

        /**
         *完成
         */
        const val STATUS_COMPLETED = 5

        /**
         *错误
         */
        const val STATUS_ERROR = 6

        /**
         *移除对列
         */
        const val STATUS_REMOVED = 7


    }


//    var type = "" //下载类型
//    var obj: ByteArray? = null   //下载的数据bean
    @NotNull
    var downloadUrl = ""    //下载地址
    var downloadFilePath = "" //下载到的路径
    var downloadFileName = ""  //下载文件名称


    //下载文件的绝对路径
    var absoluteFilePath = ""
        get() {
            if (downloadFilePath.isNotEmpty() && downloadFileName.isNotEmpty()){
                field = "$downloadFilePath/$downloadFileName"
            }
            return field
        }

    var status: Int = 0  //下载状态
        set(value) {
            field = value
            //数据库同步下载状态
//            DownloadDatabase.database?.getDownloadDao()?.update(this)
        }
    var progress: Long = 0   //当前下载的字节进度
        set(value) {
            field = value
            //数据库同步下载状态
//            DownloadDatabase.database?.getDownloadDao()?.update(this)
        }

    @Ignore
    var fileSize = 0L //文件大小
    @Ignore
    var downloadListener: DownloadListener? = null
    fun isPause(): Boolean {
        return status == STATUS_PAUSED || status == STATUS_ERROR || status == STATUS_REMOVED
    }

    /**
     * 处理回调
     * @param status 回调状态
     * @param statusMessage 状态信息
     */
    fun postListenerState(status:Int,statusMessage:String="") {
        runOnMain {
            downloadListener?.downloadState(status,statusMessage)
        }
    }

    fun postProgress(progress:Int){
        runOnMain {
            downloadListener?.onDownloading(progress)
        }
    }

    fun postError(errorCode:Int,errorMessage:String){
        runOnMain {
            downloadListener?.onDownloadError(errorCode,errorMessage)
        }
    }

    @Ignore
    val mHandler = android.os.Handler(Looper.getMainLooper())
    /**
     * 回调运行在主线程
     */
    fun runOnMain(block:()->Unit){
        mHandler.post(block)
    }
}