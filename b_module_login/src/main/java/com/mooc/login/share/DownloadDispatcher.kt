package com.ding.basic.util

import java.util.concurrent.*

/**
 *
 * 分享功能中，需要对图片进行操作的异步处理调度器
 * 练习自定义线程池
 */
object DownloadDispatcher {
    val executor: ThreadPoolExecutor by lazy {
        val corePoolSize = 2           //核心线程数
        val maximumPoolSize = 4        //最大线程数
        val keepAliveTime: Long = 10     //空闲线程存活时间
        val unit = TimeUnit.SECONDS      //单位
//        val workQueue = ArrayBlockingQueue(2)
        val workQueue = ArrayBlockingQueue<Runnable>(2)     //等待线程容器
        val threadFactory = MyThreadFactory()
        val handler = MyIgnorePolicy()    //策略
        ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, threadFactory, handler)
    }

    init {
        executor.prestartAllCoreThreads() // 预启动所有核心线程
    }

    class MyThreadFactory : ThreadFactory {
        private val mThreadNum = "ImageThread"
        override fun newThread(r: Runnable?): Thread {
            val t = Thread(r, "my-thread-$mThreadNum")
//            loge(t.name + " has been created")
            return t
        }
    }

    class MyIgnorePolicy : RejectedExecutionHandler {
        override fun rejectedExecution(r: Runnable?, p1: ThreadPoolExecutor?) {
            r?.toString()?.let {
//                loge(it)
            }
        }
    }


}
