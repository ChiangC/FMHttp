package com.fmtech.fmhttp.http.download;

import com.fmtech.fmhttp.http.download.iterfaces.IDownloadListener;

import org.apache.http.HttpEntity;

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

public class DownloadFileListener implements IDownloadListener{

    @Override
    public void onSuccess(HttpEntity httpEntity) {

    }

    @Override
    public void onFail() {

    }

    @Override
    public void addHttpHeader(Map<String, String> headerMap) {

    }

}
