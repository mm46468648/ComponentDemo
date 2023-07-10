package com.mooc.newdowload;


import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 下载线程池
 */
public class ThreadPoolUtils {

    private static final int POOLNUM = 3;// 线程池大小
    private volatile static AtomicInteger count = new AtomicInteger(0);// 工作线程数
    private static ThreadPoolExecutor THREADPOOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(POOLNUM);// 线程池
    private static LinkedList<Task> TASKQUEUE = new LinkedList<>();// 等待队列

    /**
     * 执行任务
     * @param task
     * @return 是否执行，否：放入等待队列
     */
    public static boolean execute(Task task) {
        boolean isExecute = false;
        if (count .get()< POOLNUM) {
            count.incrementAndGet();
            isExecute = true;
            THREADPOOL.execute(task);
        } else {
            // 排队
            TASKQUEUE.addLast(task);
        }
        return isExecute;
    }

    /**
     * 删除等待任务
     * @param id
     * @return
     */
    public static boolean cancelWaitTask(long id){
        boolean isCancel=false;

        Task target = null;
        for (Task item : TASKQUEUE) {
            if (item.id == id) {
                target = item;
                break;
            }
        }
        if (target != null) {
            TASKQUEUE.remove(target);
            isCancel=true;
        }
        return isCancel;
    }

    /**
     * 线程池中执行的任务
     */
    public static abstract class Task implements Runnable {
        // 使用id去标识任务，当存在排队等待的任务时可以依据id进行删除操作。
        public long id;

        @Override
        public void run() {
            // 耗时工作
            work();
            // 完成任务后通知主线程，线程池有一个空余线程,并试图从等待队列中获取下载任务
//            DownloadManager.getInstance().mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    count--;
//                    Task first = TASKQUEUE.pollFirst();
//                    if (first != null) {
//                        execute(first);
//                    }
//                }
//            });

            count.decrementAndGet();
            Task first = TASKQUEUE.pollFirst();
            if (first != null) {
                execute(first);
            }
        }

        protected abstract void work();
    }
}
