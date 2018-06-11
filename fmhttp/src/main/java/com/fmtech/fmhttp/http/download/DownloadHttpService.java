package com.fmtech.fmhttp.http.download;

import com.fmtech.fmhttp.http.interfaces.IHttpListener;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

public class DownloadHttpService implements IHttpService{

    private Map<String, String> mHeaderMap = Collections.synchronizedMap(new HashMap<String, String>());
    private IHttpListener mHttpListener;
    private HttpClient mHttpClient = new DefaultHttpClient();
    private HttpGet mHttpGet;
    private String mUrl;
    private byte[] mReqeustData;
    private HttpResponseHandler mHttpResponseHandler = new HttpResponseHandler();
    private AtomicBoolean mPaused = new AtomicBoolean(false);

    @Override
    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public void excute() {
        mHttpGet = new HttpGet(mUrl);
        constructHeader();
        try {
            mHttpClient.execute(mHttpGet, mHttpResponseHandler);
        } catch (IOException e) {
            mHttpListener.onFail();
        }
    }

    private void constructHeader(){
        Iterator iterator = mHeaderMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = (String)iterator.next();
            String value = mHeaderMap.get(key);
            mHttpGet.addHeader(key, value);
        }
    }

    @Override
    public void setHttpListener(IHttpListener listener) {
        mHttpListener = listener;
    }

    @Override
    public void setRequestData(byte[] requestData) {
        mReqeustData = requestData;
    }

    @Override
    public void pause() {
        mPaused.compareAndSet(false, true);
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
        return mPaused.get();
    }

    @Override
    public Map<String, String> getRequestHeaderMap() {
        return mHeaderMap;
    }

    class HttpResponseHandler extends BasicResponseHandler{
        @Override
        public String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
            int code = response.getStatusLine().getStatusCode();
            if(code == 200 || code == 206){
                mHttpListener.onSuccess(response.getEntity());
            }else{
                mHttpListener.onFail();
            }
            return null;
        }
    }
}
