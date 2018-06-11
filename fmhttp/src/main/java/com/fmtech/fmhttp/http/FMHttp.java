package com.fmtech.fmhttp.http;

import com.fmtech.fmhttp.http.interfaces.IDataListener;
import com.fmtech.fmhttp.http.interfaces.IHttpListener;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

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

public class FMHttp {
    public static <T,M> void sendJsonRequest(T requestInfo, String url, Class<M> responseClass, IDataListener dataListener){

        RequestHodler<T> requestHolder = new RequestHodler<>();
        IHttpListener httpListener = new JsonDealListener<>(responseClass, dataListener);
        IHttpService httpService = new JsonHttpService();
        requestHolder.setHttpListener(httpListener);
        requestHolder.setHttpService(httpService);
        requestHolder.setRequestInfo(requestInfo);
        requestHolder.setUrl(url);
        HttpTask<T> httpTask = new HttpTask<>(requestHolder);
        try {
            ThreadPoolManager.getInstance().excute(new FutureTask<Object>(httpTask, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
