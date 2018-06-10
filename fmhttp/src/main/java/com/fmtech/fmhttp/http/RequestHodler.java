package com.fmtech.fmhttp.http;

import com.fmtech.fmhttp.http.interfaces.IHttpListener;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

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

public class RequestHodler<T> {
    private IHttpService mHttpService;
    private IHttpListener mHttpListener;
    private String mUrl;
    private T mRequestInfo;

    public IHttpService getHttpService() {
        return mHttpService;
    }

    public void setHttpService(IHttpService mHttpService) {
        this.mHttpService = mHttpService;
    }

    public IHttpListener getHttpListener() {
        return mHttpListener;
    }

    public void setHttpListener(IHttpListener mHttpListener) {
        this.mHttpListener = mHttpListener;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public T getRequestInfo() {
        return mRequestInfo;
    }

    public void setRequestInfo(T mRequestInfo) {
        this.mRequestInfo = mRequestInfo;
    }

}
