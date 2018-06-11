package com.fmtech.fmhttp.http;

import com.fmtech.fmhttp.http.interfaces.IHttpListener;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
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

public class JsonHttpService implements IHttpService{

    private IHttpListener mHttpListener;
    private String mUrl;
    private HttpClient mHttpClient = new DefaultHttpClient();
    private byte[] mRequestData;
    private HttpPost mHttpPost;
    private HttpResponseHandler mHttpResponseHandler = new HttpResponseHandler();

    @Override
    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public void setHttpListener(IHttpListener listener) {
        mHttpListener = listener;
    }

    @Override
    public void setRequestData(byte[] requestData) {
        mRequestData = requestData;
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

    @Override
    public void excute() {
        mHttpPost = new HttpPost(mUrl);
        ByteArrayEntity entity = new ByteArrayEntity(mRequestData);
        mHttpPost.setEntity(entity);
        try {
            mHttpClient.execute(mHttpPost, mHttpResponseHandler);
        } catch (Exception e) {
            mHttpListener.onFail();
            e.printStackTrace();
        }
    }

    class HttpResponseHandler extends BasicResponseHandler{
        @Override
        public String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
            int code = response.getStatusLine().getStatusCode();
            if(code == 200){
                mHttpListener.onSuccess(response.getEntity());
            }else{
                mHttpListener.onFail();
            }
            return null;
        }
    }
}
