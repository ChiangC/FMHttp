package com.fmtech.fmhttp.http;

import com.alibaba.fastjson.JSON;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

import java.io.UnsupportedEncodingException;

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

}
