package com.tcl.zhanglong.utils.Utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 自定义完美ThreadPool
 1、用ThreadPoolExecutor自定义线程池，看线程是的用途，如果任务量不大，可以用无界队列，如果任务量非常大，要用有界队列，防止OOM
 2、如果任务量很大，还要求每个任务都处理成功，要对提交的任务进行阻塞提交，重写拒绝机制，改为阻塞提交。保证不抛弃一个任务
 3、最大线程数一般设为2N+1最好，N是CPU核数
 4、核心线程数，看应用，如果是任务，一天跑一次，设置为0，合适，因为跑完就停掉了，如果是常用线程池，看任务量，是保留一个核心还是几个核心线程数
 5、如果要获取任务执行结果，用CompletionService，但是注意，获取任务的结果的要重新开一个线程获取，如果在主线程获取，就要等任务都提交后才获取，就会阻塞大量任务结果，队列过大OOM，所以最好异步开个线程获取结果
 * Created by Steve on 16/11/17.
 */

public class ThreadPoolUtil {

    private ThreadPoolExecutor pool = null;

    private static final int CORE_POOL_SIZE = 3;

    private static final int MAXIUM_POOL_SIZE = 3;

    private static final int KEEP_ALIVE_TIME = 1;

    private static final int BLOCKING_QUEUE_SIZE =5;


    private static volatile ThreadPoolUtil ins;

    public static ThreadPoolUtil getIns(){
        if (ins == null){
            synchronized (ThreadPoolUtil.class){
                if (ins ==null){
                    ins = new ThreadPoolUtil() ;
                }
            }
        }
        return ins;
    }



    private ThreadPoolUtil(){
        pool = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIUM_POOL_SIZE,KEEP_ALIVE_TIME, TimeUnit.MINUTES,new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE),new CustomFactory(),new CustomRejectHandler());
    }


    public void destory() {
        if(pool != null) {
            pool.shutdownNow();
        }
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        if (pool!=null)
            return this.pool;
        else
            return  pool = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIUM_POOL_SIZE,KEEP_ALIVE_TIME, TimeUnit.MINUTES,new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE),new CustomFactory(),new CustomRejectHandler());
    }





    private class CustomFactory implements ThreadFactory{

        private AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            String threadName = "HiLogUploadThread-" + count.addAndGet(1);
            thread.setName(threadName);
            return thread;
        }
    }


    private class CustomRejectHandler implements RejectedExecutionHandler{

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }





}
