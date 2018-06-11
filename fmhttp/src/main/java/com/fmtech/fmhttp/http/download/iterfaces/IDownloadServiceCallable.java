package com.fmtech.fmhttp.http.download.iterfaces;

import com.fmtech.fmhttp.http.download.DownloadItemInfo;

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

public interface IDownloadServiceCallable {
    void onDownloadStatusChanged(DownloadItemInfo downloadItemInfo);

    void onTotalLengthRecieved(DownloadItemInfo downloadItemInfo);

    void onCurrentSizeChanged(DownloadItemInfo downloadItemInfo, double downLength, long speed);

    void onDownloadSuccess(DownloadItemInfo downloadItemInfo);

    void onDownloadPause(DownloadItemInfo downloadItemInfo);

    void onDownloadError(DownloadItemInfo downloadItemInfo, int errorrCode, String message);
}
