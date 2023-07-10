package com.mooc.download

import com.mooc.download.db.DownloadDatabase
import com.mooc.download.strategy.ContinueStrategy
import com.mooc.download.strategy.CoverStrategy
import com.mooc.download.util.DownloadConstants
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 下载消费者
 * 实际进行下载的类
 */
class DownloadConsumer(var queue: BlockingQueue<DownloadModel>, var currentConsumerNum: AtomicInteger) : Runnable {


    override fun run() {
        println("start Consumer id :" + Thread.currentThread().id)
        try {
            while (true) {
                //如果2s内取数据没有任务，则终止循环
                val downloadMode = queue.poll(2, TimeUnit.SECONDS) ?: break

                currentConsumerNum.decrementAndGet()
//                DownloadDatabase.database?.getDownloadDao()?.insert(downloadMode)
                //准备开始执行
                downloadMode.status = DownloadModel.STATUS_PREPARE
                downloadMode.postListenerState(DownloadModel.STATUS_PREPARE, downloadMode.downloadUrl)
//                downloadMode.downloadListener?.downloadState(DownloadModel.STATUS_PREPARE, downloadMode.downloadUrl)

                downloadFile(downloadMode)
            }
        } catch (e: Exception) {
//            downloadMode.postError(DownloadConstants.ERROR_UNKNOWN, e.toString())
//                downloadMode.downloadListener?.onDownloadError()

            e.printStackTrace()
            Thread.currentThread().interrupt()
        }
    }

    fun downloadFile(downloadModel: DownloadModel) {


        val request = Request.Builder().url(downloadModel.downloadUrl)
                .addHeader("Referer", "http://*.learning.mil.cn")
                .addHeader("Range",  "bytes=" + downloadModel.progress + "-")
                .build()

        val okHttpClient = OkHttpClient()
        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
//            writeResponseToDisk(downloadModel,response)
            downloadModel.status = DownloadModel.STATUS_DOWNLOADING

            //判断服务端是否支持断点续传
            if(response.code == 206){
                val continueStrategy = ContinueStrategy()
                continueStrategy.writeResponseToDisk(downloadModel, response)
            }else{
                val continueStrategy = CoverStrategy()
                continueStrategy.writeResponseToDisk(downloadModel, response)
            }

        } else {
            downloadModel.status = DownloadModel.STATUS_ERROR
            downloadModel.postError(DownloadConstants.ERROR_UNKNOWN, DownloadConstants.ERROR_NET.toString())

        }
    }


}