package com.fmtech.fmhttp.http;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @version v1.0.0
 * @email chiangchuna@gmail.com
 * <p>
 * ==================================================================
 */

public class ThreadPoolManager {
    private static final ThreadPoolManager sInstance = new ThreadPoolManager();
    private LinkedBlockingDeque<Future<?>> mTaskQueue = new LinkedBlockingDeque<>();
    private ThreadPoolExecutor mThreadPoolExecutor;

    public static ThreadPoolManager getInstance() {
        return sInstance;
    }

    private ThreadPoolManager() {
        mThreadPoolExecutor = new ThreadPoolExecutor(4,10,10, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(4), mRejectedExecutionHandler);
        mThreadPoolExecutor.execute(mCommand);
    }

    private Runnable mCommand = new Runnable() {
        @Override
        public void run() {
            while(true){
                FutureTask futureTask = null;

                try {
                    //阻塞式函数
                    futureTask = (FutureTask) mTaskQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(null != futureTask) {
                    mThreadPoolExecutor.execute(futureTask);
                }
            }
        }
    };

    private RejectedExecutionHandler mRejectedExecutionHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                mTaskQueue.put(new FutureTask<Object>(r, null));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public <T> void excute(FutureTask<T> futureTask) throws InterruptedException {
        mTaskQueue.put(futureTask);
    }

}
