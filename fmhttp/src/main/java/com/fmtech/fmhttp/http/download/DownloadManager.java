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
import java.util.ArrayList;
import java.util.Date;
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
            List<DownloadItemInfo> records = mDownloadDao.findRecord(filePath);
            if(records.size() > 0){
                DownloadItemInfo record = records.get(0);
                if(record.getCurrentLen() == record.getTotalLen()){
                    synchronized (mAppListeners){
                        for(IDownloadCallable downloadCallable:mAppListeners){
                            downloadCallable.onDownloadError(record.getId(), 2, "Has been downloaded.");
                        }
                    }
                }
            }

            int recordId = mDownloadDao.addRecord(url, filePath, displayName, priority.getValue());
            if(recordId != -1){
                synchronized (mAppListeners){
                    for(IDownloadCallable downloadCallable:mAppListeners){
                        downloadCallable.onDownloadInfoAdd(downloadItemInfo.getId());
                    }
                }
            }else{
                downloadItemInfo = mDownloadDao.findRecord(url, filePath);
            }

            if(isDownloading(file.getAbsolutePath())){
                synchronized (mAppListeners){
                    for(IDownloadCallable downloadCallable:mAppListeners){
                        downloadCallable.onDownloadError(downloadItemInfo.getId(), 4, "Download task is in proccess.");
                    }
                }
                return downloadItemInfo.getId();
            }

            if(null != downloadItemInfo){
                downloadItemInfo.setPriority(priority.getValue());
                downloadItemInfo.setStopMode(DownloadStopMode.AUTO.getValue());

                if(downloadItemInfo.getStatus() != DownloadStatus.finish.getValue()){
                    if(downloadItemInfo.getTotalLen() == 0L || file.length() == 0L){
                        downloadItemInfo.setStatus(DownloadStatus.failed.getValue());
                    }

                    if(downloadItemInfo.getTotalLen() == file.length() && downloadItemInfo.getTotalLen() != 0){
                        downloadItemInfo.setStatus(DownloadStatus.finish.getValue());
                        synchronized (mAppListeners){
                            for(IDownloadCallable downloadCallable:mAppListeners){
                                try {
                                    downloadCallable.onDownloadError(downloadItemInfo.getId(), 4, "Already downloaded.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }else{
                    if(!file.exists() || (downloadItemInfo.getTotalLen()) != downloadItemInfo.getCurrentLen()){
                        downloadItemInfo.setStatus(DownloadStatus.failed.getValue());
                    }
                }

                mDownloadDao.updateRecord(downloadItemInfo);

                if(downloadItemInfo.getStatus() == DownloadStatus.finish.getValue()){
                    final int downloadId = downloadItemInfo.getId();
                    synchronized (mAppListeners){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                for (IDownloadCallable downloadCallable : mAppListeners) {
                                    downloadCallable.onDownloadStatusChanged(downloadId, DownloadStatus.finish);
                                }
                            }
                        });
                    }
                    mDownloadDao.removeRecordFromMemory(downloadId);
                    return downloadItemInfo.getId();
                }

                List<DownloadItemInfo> allDownloadingTask = mDownloadTaskList;
                if(priority != Priority.HIGH){
                    for(DownloadItemInfo downloadTask: allDownloadingTask){
                        downloadTask = mDownloadDao.findSingleRecord(downloadTask.getFilePath());
                        if(null != downloadTask && downloadTask.getPriority() == Priority.HIGH.getValue()){
                            if(downloadTask.getFilePath().equals(downloadItemInfo.getFilePath())){
                                break;
                            }else{
                                return downloadItemInfo.getId();
                            }
                        }
                    }
                }

                reallyDownLoad(downloadItemInfo);
                if(priority == Priority.HIGH || priority == Priority.MEDIUM){
                    synchronized (allDownloadingTask){
                        for(DownloadItemInfo task:allDownloadingTask){
                            if(!downloadItemInfo.getFilePath().equals(task.getFilePath())){
                                DownloadItemInfo downloadItemInfo1 = mDownloadDao.findSingleRecord(task.getFilePath());
                                if(null != downloadItemInfo1){
                                    pauseTask(downloadItemInfo1.getId(), DownloadStopMode.AUTO);
                                }
                            }
                        }
                    }
                    return downloadItemInfo.getId();
                }
            }
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
        DownloadItemInfo downloadInfo = mDownloadDao.findSingleRecord(downloadItemInfo.getFilePath());
        if(null != downloadInfo){
            downloadInfo.setCurrentLen(new File(downloadItemInfo.getFilePath()).length());
            downloadInfo.setFinishTime(mSimpleDateFormat.format(new Date()));
            downloadInfo.setStopMode(DownloadStopMode.HAND.getValue());
            downloadInfo.setStatus(DownloadStatus.finish.getValue());
            mDownloadDao.updateRecord(downloadInfo);

            synchronized (mAppListeners){
                for(IDownloadCallable downloadCallable:mAppListeners){
                    downloadCallable.onDownloadSuccess(downloadItemInfo.getId());
                }
            }

            resumeAutoCancelItem();
        }
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

    private void resumeAutoCancelItem(){
        List<DownloadItemInfo> allAutoCancelList = mDownloadDao.findAllAutoCancelRecords();
        List<DownloadItemInfo> notDownloadingList = new ArrayList<>();
        for(DownloadItemInfo downloadItemInfo:allAutoCancelList){
            if(!isDownloading(downloadItemInfo.getFilePath())){
                notDownloadingList.add(downloadItemInfo);
            }
        }

        for(DownloadItemInfo downloadItemInfo:notDownloadingList){
            if(downloadItemInfo.getPriority() == Priority.HIGH.getValue()){
                resumeItem(downloadItemInfo.getId(), Priority.HIGH);
                return;
            }else if(downloadItemInfo.getPriority() == Priority.MEDIUM.getValue()){
                resumeItem(downloadItemInfo.getId(), Priority.MEDIUM);
            }
        }
    }

    public void resumeItem(final int downloadId, Priority priority){
        DownloadItemInfo downloadItemInfo = mDownloadDao.findRecordById(downloadId);
        if(null == downloadItemInfo){
            return;
        }

        if(null == priority){
            priority = Priority.getInstance(downloadItemInfo.getPriority() == null?
                    Priority.LOW.getValue():downloadItemInfo.getPriority());
        }

        File file = new File(downloadItemInfo.getFilePath());
        downloadItemInfo.setStopMode(DownloadStopMode.AUTO.getValue());
        mDownloadDao.updateRecord(downloadItemInfo);
        download(downloadItemInfo.getUrl(), file.getAbsolutePath(), null, priority);

    }

}
