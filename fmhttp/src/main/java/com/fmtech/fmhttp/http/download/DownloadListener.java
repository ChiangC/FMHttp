package com.fmtech.fmhttp.http.download;

import android.os.Handler;
import android.os.Looper;

import com.fmtech.fmhttp.http.download.enums.DownloadStatus;
import com.fmtech.fmhttp.http.download.iterfaces.IDownloadListener;
import com.fmtech.fmhttp.http.download.iterfaces.IDownloadServiceCallable;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

import org.apache.http.HttpEntity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class DownloadListener implements IDownloadListener{

    private DownloadItemInfo mDownloadItemInfo;
    private File mFile;
    protected String mUrl;
    private long mBreakPoint;
    private IDownloadServiceCallable mDownloadServiceCallable;
    private IHttpService mHttpService;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public DownloadListener(DownloadItemInfo downloadItemInfo){
        mDownloadItemInfo = downloadItemInfo;
    }

    public DownloadListener(DownloadItemInfo downloadItemInfo, IDownloadServiceCallable downloadServiceCallable, IHttpService httpService){
        mDownloadItemInfo = downloadItemInfo;
        mDownloadServiceCallable = downloadServiceCallable;
        mHttpService = httpService;
        mFile = new File(downloadItemInfo.getFilePath());
        mBreakPoint = mFile.length();
    }

    @Override
    public void onSuccess(HttpEntity httpEntity) {
        InputStream inputStream = null;
        try {
            inputStream = httpEntity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long startTime = System.currentTimeMillis();

        //download speed k/s
        long speed = 0L;
        long useTime = 0L;
        long getLen = 0L;
        long receiveLen = 0L;
        boolean bufferLen = false;
        long dataLength = httpEntity.getContentLength();
        long calcSpeedLen = 0L;
        long totalLength = mBreakPoint + dataLength;

        receivedTotalLength(totalLength);

        updateDownloadStatus(DownloadStatus.downloading);

        byte[] buffer = new byte[512];
        int count = 0;
        long currentTime = System.currentTimeMillis();
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        try {
            if(!makeDir(getFile().getParentFile())){
                mDownloadServiceCallable.onDownloadError(mDownloadItemInfo, 1, "Create directory failed.");
            }else{
                fos = new FileOutputStream(getFile(), true);
                bos = new BufferedOutputStream(fos);
                int length = 1;
                while((length = inputStream.read(buffer)) != -1){
                    if(getHttpService().isCanceled()){
                        mDownloadServiceCallable.onDownloadError(mDownloadItemInfo, 1,"Download task canceled");
                        return;
                    }

                    if(getHttpService().isPaused()){
                        mDownloadServiceCallable.onDownloadError(mDownloadItemInfo, 1, "Download task paused.");
                        return;
                    }

                    bos.write(buffer, 0 , length);
                    getLen += (long) length;
                    receiveLen += (long)length;
                    calcSpeedLen += (long) length;
                    ++count;

                    if(receiveLen * 10L/ totalLength >= 1L || count >= 5000){
                        currentTime = System.currentTimeMillis();
                        useTime = currentTime - startTime;
                        startTime = currentTime;
                        speed = 1000L * calcSpeedLen/useTime;
                        count = 0;
                        calcSpeedLen = 0L;
                        receiveLen = 0L;
                        downloadLengthChanged(mBreakPoint + getLen, totalLength, speed);
                    }
                }

                bos.close();
                inputStream.close();
                if(dataLength != getLen){
                    mDownloadServiceCallable.onDownloadError(mDownloadItemInfo, 1, "Download length errorr.");
                }else {
                    downloadLengthChanged(mBreakPoint + getLen, totalLength, speed);
                    mDownloadServiceCallable.onDownloadSuccess(mDownloadItemInfo.copy());
                }
            }
        }catch (Exception e) {
            if(null != getHttpService()){

            }
        }finally {
            try {
                if(null != bos){
                    bos.close();
                }

                if(null != inputStream){
                    inputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receivedTotalLength(long totalLength){
        mDownloadItemInfo.setTotalLen(totalLength);
        final DownloadItemInfo downloadItemInfo = mDownloadItemInfo.copy();
        if(null != mDownloadServiceCallable){
            synchronized (mDownloadServiceCallable) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadServiceCallable.onTotalLengthRecieved(downloadItemInfo);
                    }
                });
            }
        }
    }

    private void updateDownloadStatus(DownloadStatus downloadStatus){
        mDownloadItemInfo.setStatus(downloadStatus.getValue());
        final DownloadItemInfo downloadItemInfo = mDownloadItemInfo.copy();
        if(null != mDownloadServiceCallable){
            synchronized (mDownloadServiceCallable){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadServiceCallable.onDownloadStatusChanged(downloadItemInfo);
                    }
                });
            }
        }
    }

    private boolean makeDir(File parentFile){
        return parentFile.exists()&&!parentFile.isFile()?
                parentFile.exists()&& parentFile.isDirectory():
                parentFile.mkdirs();
    }

    private void downloadLengthChanged(final long downlength, final long totalLength, final long speed){
        mDownloadItemInfo.setCurrentLength(downlength);
        if(null != mDownloadServiceCallable){
            final DownloadItemInfo downloadItemInfo = mDownloadItemInfo.copy();
            synchronized (mDownloadServiceCallable){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadServiceCallable.onCurrentSizeChanged(downloadItemInfo, (double)downlength/(double)totalLength, speed);
                    }
                });
            }
        }
    }

    @Override
    public void onFail() {

    }

    @Override
    public void addHttpHeader(Map<String, String> headerMap) {
        long lenght = getFile().length();
        if(lenght > 0L){
            headerMap.put("RANGE", "bytes="+lenght+"-");
        }
    }

    @Override
    public void setHttpService(IHttpService httpService) {
        mHttpService = httpService;
    }

    @Override
    public void setCancelCallable() {

    }

    @Override
    public void setPauseCallable() {

    }

    public File getFile() {
        return mFile;
    }

    public IHttpService getHttpService() {
        return mHttpService;
    }

}
