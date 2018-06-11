package com.fmtech.fmhttp.http;

import com.alibaba.fastjson.JSON;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.FutureTask;

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

public class HttpTask<T> implements Runnable{

    private IHttpService mHttpService;
    private FutureTask mFutureTask;

    public HttpTask(RequestHodler<T> requestHodler){
        mHttpService = requestHodler.getHttpService();
        mHttpService.setHttpListener(requestHodler.getHttpListener());
        mHttpService.setUrl(requestHodler.getUrl());

        T request = requestHodler.getRequestInfo();
        String requestInfo = JSON.toJSONString(request);
        try {
            mHttpService.setRequestData(requestInfo.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        mHttpService.excute();
    }

    public void start(){
        mFutureTask = new FutureTask(this, null);
        try {
            ThreadPoolManager.getInstance().excute(mFutureTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        mHttpService.pause();
        if(null != mFutureTask){
            ThreadPoolManager.getInstance().removeTask(mFutureTask);
        }
    }

}
