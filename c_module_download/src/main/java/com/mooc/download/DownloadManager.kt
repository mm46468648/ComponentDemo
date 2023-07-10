package com.mooc.download

import com.mooc.download.db.DownloadDatabase
import com.mooc.download.util.DownloadConstants
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicInteger

/**
 * 下载管理类
 */
object DownloadManager {
    private val maxConsumerNum = 2  //同时运行最多任务
    private var downloadExecutors = Executors.newFixedThreadPool(maxConsumerNum)
    //缓存队列
    private var queue: BlockingQueue<DownloadModel> = LinkedBlockingDeque(Int.MAX_VALUE) //缓存队列
    val bufferSize = 8192  //buffer size
    private val currentConsumerNum = AtomicInteger(0)


    private val currentDownloadConsumer: ConcurrentHashMap<String, DownloadConsumer> by lazy {
        ConcurrentHashMap<String, DownloadConsumer>()
    }



    fun clearAllTask(){
        currentConsumerNum.set(0)
        queue.clear()
    }

    fun addProduct(downloadModel:DownloadModel){
        queue.offer(downloadModel)
        if(queue.size > maxConsumerNum){
            downloadModel.status = DownloadModel.STATUS_WAIT
        }else{
            downloadModel.status = DownloadModel.STATUS_PREPARE
            val downloadConsumer = DownloadConsumer(queue, currentConsumerNum)
            downloadExecutors.execute(downloadConsumer)
        }

//        if(queue.offer(downloadModel)){
//            if(currentConsumerNum.get()<maxConsumerNum){
//                currentConsumerNum.incrementAndGet()
//                val downloadConsumer = DownloadConsumer(queue, currentConsumerNum)
//                downloadExecutors.execute(downloadConsumer)
//            }else{
//                //等待执行
//                downloadModel.status = DownloadModel.STATUS_WAIT
//            }
//        }else{
//            System.out.println("队列已满")
//            downloadModel.downloadListener?.onDownloadError(DownloadConstants.ERROR_WAIT_QUEUE_FULL)
//        }
    }


//    /**
//     * 准备下载
//     */
//    fun prepareDownload(downloadModel:DownloadModel){
//        if (currentDownloadConsumer.size >= maxConsumerNum) {
//            downloadModel.status = DownloadModel.STATUS_WAIT
//        } else {
//            downloadModel.status = DownloadModel.STATUS_PREPARE
//            val downloadConsumer = DownloadConsumer(queue, currentConsumerNum)
//            currentDownloadConsumer.put(downloadModel.downloadUrl,downloadConsumer)
//            downloadExecutors.execute(downloadConsumer)
//        }
//    }
//
    /**
     * 准备下载下一个任务
     */
    fun prepareDownloadNextTask(){
        val downloadConsumer = DownloadConsumer(queue, currentConsumerNum)
        downloadExecutors.execute(downloadConsumer)
    }

    /**
     * 并添加到下载任务栈中
     */
    fun start(downloadModel: DownloadModel){
        if(downloadModel.downloadUrl.isEmpty()){
            throw IllegalArgumentException("downloadUrl must not be null")
        }
        addProduct(downloadModel)
    }

    /**
     * 暂停任务
     */
    fun pause(downloadModel: DownloadModel){
        downloadModel.status = DownloadModel.STATUS_PAUSED
        prepareDownloadNextTask()
    }

    /**
     * 继续任务
     */
    fun resume(downloadModel: DownloadModel){
        downloadModel.status = DownloadModel.STATUS_PREPARE
        addProduct(downloadModel)
    }

//    /**
//     * 获取下载模型
//     * @param downloadUrl 下载地址
//     * @param filePath 文件的绝对路径
//     *
//     * 过时，使用url查询
//     */
//    @Deprecated("")
//    fun findDownloadMode(downloadUrl :String,filePath : String) : DownloadModel?{
//        return DownloadDatabase.database?.getDownloadDao()?.getDownloadModle(downloadUrl,filePath)
//    }

    /**
     * 获取下载模型
     * @param fileName 文件名
     */
    fun findDownloadModeByUrl(url :String) : DownloadModel?{
//        return DownloadDatabase.database?.getDownloadDao()?.getDownloadModleByUrl(url)
        return null
    }

//    /**
//     * 获取下载模型
//     * @param fileName 文件名
//     */
//    fun findDownloadModeByName(fileName :String) : DownloadModel?{
//        return DownloadDatabase.database?.getDownloadDao()?.getDownloadModleByName(fileName)
//    }



}