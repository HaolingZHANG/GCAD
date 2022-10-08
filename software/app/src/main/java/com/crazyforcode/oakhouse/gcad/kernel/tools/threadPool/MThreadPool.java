package com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool;

import android.util.Log;

import com.crazyforcode.oakhouse.gcad.window.AlignActivity;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MThreadPool {

    /** 总共多少任务（根据CPU个数决定创建活动线程的个数） */
    private static final int count = Runtime.getRuntime().availableProcessors() * 3;

    /** 任务执行队列 */
    private static ConcurrentLinkedQueue<MRunnable> taskQueue = null;

    /** 正在等待执行或已经完成的任务队列
     *
     * Future类，一个用于存储异步任务执行的结果，
     * 比如：判断是否取消、是否可以取消、是否正在执行、是否已经完成等。
     * */
    private static ConcurrentMap<Future, MRunnable> taskMap = null;

    private static ExecutorService executorService = null;

    /** 在此类中使用同步锁时使用如下lock对象 */
    private static final Object lock = new Object();

    /** 唤醒标志，是否唤醒线程池工作 */
    private static boolean isNotify = true;

    /** 线程池是否处于运行状态 */
    private static boolean isRunning = true;

    /** 增加任务 */
    public static void addTask(final MRunnable mRunnable) {
        if(executorService == null) {
            executorService = Executors.newFixedThreadPool(count);
            notifyWork();
        }

        if(taskQueue == null)
            taskQueue = new ConcurrentLinkedQueue<>();

        if(taskMap == null)
            taskMap = new ConcurrentHashMap<>();

        executorService.execute(mRunnable);
    }

    /** 释放任务 */
    public static void release() {
        isRunning = false;

        for(Map.Entry<Future, MRunnable> entry : taskMap.entrySet()) {
            Future result = entry.getKey();

            if(result == null)
                continue;

            result.cancel(true);
            taskMap.remove(result);
        }

        if(executorService != null)
            executorService.shutdown();

        executorService = null;
        taskMap = null;
        taskQueue = null;
    }

    /** 重新加载任务 */
    public static void reload(final MRunnable mRunnable) {
        if(executorService == null) {
            executorService = Executors.newFixedThreadPool(count);
            notifyWork();
        }

        if(taskQueue == null)
            taskQueue = new ConcurrentLinkedQueue<>();

        if(taskMap == null)
            taskMap = new ConcurrentHashMap<>();

        executorService.execute(mRunnable);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if(isRunning) {
                    MRunnable mRunnable;
                    synchronized (lock) {
//                        从线程队列中取出一个Runnable对象来执行，如果此队列为空，则调用poll()方法会返回null
                        mRunnable = taskQueue.poll();
                        if(mRunnable == null)
                            isNotify = true;
                    }

                    if(mRunnable != null) {
                        Future future = executorService.submit(mRunnable);
                        if(mRunnable.hasTimeLimit()) {
                            try {
                                future.get(mRunnable.getMillisecond(), TimeUnit.MILLISECONDS);
                            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                e.printStackTrace();
                                stopAllWait();
                            }
                        }
                        taskMap.put(future, mRunnable);
                    }
                }
            }
        });
    }

    /** 终止任务 */
    public static void stop() {
        for(MRunnable runnable : taskMap.values())
            runnable.setCancelTaskUnit(true);
    }

    /** 开始任务 */
    public static void start() {

        if(executorService == null || taskQueue == null || taskMap == null) {
            Log.i("ThreadPool", "资源已经被释放了");
            return;
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    MRunnable mRunnable;

                    synchronized (lock) {
//                        从线程队列中取出一个Runnable对象来执行，如果此队列为空，则调用poll()方法会返回null
                        mRunnable = taskQueue.poll();
                        if (mRunnable == null)
                            isNotify = true;
                    }

                    if(mRunnable != null) {
                        Future future = executorService.submit(mRunnable);
                        if(mRunnable.hasTimeLimit()) {
                            try {
                                future.get(mRunnable.getMillisecond(), TimeUnit.MILLISECONDS);
                            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                e.printStackTrace();
                                future.cancel(true);
                                stopAllWait();
                            }
                        }
                        taskMap.put(future, mRunnable);
                    }
                }
            }
        });
    }

    /** 唤醒线程池工作 */
    private static void notifyWork() {
        synchronized (lock) {
            if (isNotify) {
                lock.notifyAll();
                isNotify = !isNotify;
            }
        }
    }

    private static void stopAllWait() {
        MainActivity.stopLoading();
        AlignActivity.stopLoading();
    }
}
