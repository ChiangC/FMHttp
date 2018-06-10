package com.fmtech.fmhttp.http;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.fmtech.fmhttp.http.interfaces.IDataListener;
import com.fmtech.fmhttp.http.interfaces.IHttpListener;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @version v1.0.0
 * @email chiangchuna@gmail.com
 * @create_date 2018/6/10 11:52
 * <p>
 * ==================================================================
 */

public class JsonDealListener<T> implements IHttpListener{
    private IDataListener<T> mDataListener;
    private Class<T> mResponseClass;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public JsonDealListener(Class<T> response, IDataListener<T> dataListener){
        mResponseClass = response;
        mDataListener = dataListener;

        if(null == mDataListener || null == mResponseClass){
            throw  new IllegalArgumentException();
        }

    }

    @Override
    public void onSuccess(HttpEntity httpEntity) {
        InputStream inputStream = null;
        try {
            inputStream = httpEntity.getContent();
            String responseContent = getContent(inputStream);
            final T result = JSON.parseObject(responseContent, mResponseClass);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDataListener.onSuccess(result);
                }
            });
        } catch (IOException e) {
            mDataListener.onFail();
        }
    }

    @Override
    public void onFail() {
        mDataListener.onFail();
    }

    private String getContent(InputStream inputStream){
        String content=null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                mDataListener.onFail();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            mDataListener.onFail();
        }
        return content;
    }

}
