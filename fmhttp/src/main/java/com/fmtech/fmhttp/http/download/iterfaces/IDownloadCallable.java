package com.fmtech.fmhttp.http.download.iterfaces;

import com.fmtech.fmhttp.http.download.enums.DownloadStatus;

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

public interface IDownloadCallable {

    void onDownloadInfoAdd(int downloadId);

    void onDownloadInfoRemoved(int downloadId);

    void onDownloadStatusChanged(int downloadId, DownloadStatus status);

    void onTotalLengthReceived(int downloadId, long totalLenght);

    void onCurrentSizeChagned(int downloadId, double downloadPercent, long speed);

    void onDownloadSuccess(int downloadId);

    void onDownloadError(int downloadId, int errorCode, String errorMsg);

}
