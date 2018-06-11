package com.fmtech.fmhttp.http.download;

import com.fmtech.fmhttp.http.interfaces.IHttpListener;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

import java.util.Map;

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

public class DownloadFileHttpService implements IHttpService{

    @Override
    public void setUrl(String url) {

    }

    @Override
    public void excute() {

    }

    @Override
    public void setHttpListener(IHttpListener listener) {

    }

    @Override
    public void setRequestData(byte[] requestData) {

    }

    @Override
    public void pause() {

    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public Map<String, String> getRequestHeaderMap() {
        return null;
    }
}
