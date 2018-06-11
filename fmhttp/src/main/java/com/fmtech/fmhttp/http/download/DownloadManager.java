package com.fmtech.fmhttp.http.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fmtech.fmhttp.db.BaseDaoFactory;
import com.fmtech.fmhttp.http.HttpTask;
import com.fmtech.fmhttp.http.RequestHodler;
import com.fmtech.fmhttp.http.download.dao.DownloadDao;
import com.fmtech.fmhttp.http.download.enums.DownloadStatus;
import com.fmtech.fmhttp.http.download.enums.DownloadStopMode;
import com.fmtech.fmhttp.http.download.enums.Priority;
import com.fmtech.fmhttp.http.download.iterfaces.IDownloadCallable;
import com.fmtech.fmhttp.http.download.iterfaces.IDownloadServiceCallable;
import com.fmtech.fmhttp.http.interfaces.IHttpListener;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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

public class DownloadManager implements IDownloadServiceCallable {
    private static final String TAG = "DownloadManager";

    private byte[] mLock = new byte[0];
    private DownloadDao mDownloadDao = BaseDaoFactory.getInstance().getDataHelper(DownloadDao.class, DownloadItemInfo.class);
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private final List<IDownloadCallable> mAppListeners = new CopyOnWriteArrayList<>();
    private static List<DownloadItemInfo> mDownloadTaskList = new CopyOnWriteArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public int download(String url) {
        String[] preFix = url.split("/");
        return this.download(url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + preFix[preFix.length - 1]);
    }

    public int download(String url, String filePath) {
        String[] preFix = url.split("/");
        String displayName = preFix[preFix.length - 1];
        return this.download(url, filePath, displayName);
    }

    public int download(String url, String filePath, String displayName) {
        return this.download(url, filePath, displayName, Priority.MEDIUM);
    }

    public int download(String url, String filePath, String displayName, Priority priority) {
        if(null == priority){
            priority = Priority.LOW;
        }

        File file = new File(filePath);
        DownloadItemInfo downloadItemInfo = mDownloadDao.findRecord(url, filePath);
        if(null == downloadItemInfo){

        }
        return -1;
    }

    @Override
    public void onDownloadStatusChanged(DownloadItemInfo downloadItemInfo) {

    }

    @Override
    public void onTotalLengthRecieved(DownloadItemInfo downloadItemInfo) {

    }

    @Override
    public void onCurrentSizeChanged(DownloadItemInfo downloadItemInfo, double downLength, long speed) {
        Log.i(TAG, "Download speed：" + speed / 1000 + "k/s");

    }

    @Override
    public void onDownloadSuccess(DownloadItemInfo downloadItemInfo) {

    }

    @Override
    public void onDownloadPause(DownloadItemInfo downloadItemInfo) {

    }

    @Override
    public void onDownloadError(DownloadItemInfo downloadItemInfo, int errorrCode, String message) {

    }

    private boolean isDownloading(String absolutePath){
        for(DownloadItemInfo downloadItemInfo: mDownloadTaskList){
            if(downloadItemInfo.getFilePath().equals(absolutePath)){
                return true;
            }
        }
        return false;
    }

    public void pauseTask(int downloadId, DownloadStopMode stopMode){
        if(null == stopMode){
            stopMode = DownloadStopMode.AUTO;
        }

        final DownloadItemInfo downloadItemInfo = mDownloadDao.findRecordById(downloadId);
        if(null != downloadItemInfo){
            downloadItemInfo.setStopMode(stopMode.getValue());
            downloadItemInfo.setStatus(DownloadStatus.pause.getValue());
            mDownloadDao.updateRecord(downloadItemInfo);
        }
        for(DownloadItemInfo downloadTask:mDownloadTaskList){
            if(downloadId == downloadTask.getId()){
                downloadTask.getHttpTask().pause();
            }
        }
    }

    public DownloadItemInfo reallyDownLoad(DownloadItemInfo downloadItemInfo){
        synchronized (mLock){
            RequestHodler requestHodler = new RequestHodler();
            IHttpService httpService = new DownloadHttpService();
            Map<String, String> map = httpService.getRequestHeaderMap();
            IHttpListener httpListener = new DownloadListener(downloadItemInfo);

            requestHodler.setHttpService(httpService);
            requestHodler.setHttpListener(httpListener);
            requestHodler.setUrl(downloadItemInfo.getUrl());
            HttpTask httpTask = new HttpTask(requestHodler);
            downloadItemInfo.setHttpTask(httpTask);

            mDownloadTaskList.add(downloadItemInfo);
            httpTask.start();

        }

        return downloadItemInfo;
    }

    public void setDownloadCallable(IDownloadCallable downloadCallable){
        synchronized (mAppListeners){
            mAppListeners.add(downloadCallable);
        }
    }

}
