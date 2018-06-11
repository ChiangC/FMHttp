package com.fmtech.fmhttp.http.download.iterfaces;

import com.fmtech.fmhttp.http.interfaces.IHttpListener;
import com.fmtech.fmhttp.http.interfaces.IHttpService;

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

public interface IDownloadListener extends IHttpListener{
    void setHttpService(IHttpService httpService);

    void setCancelCallable();

    void setPauseCallable();

}
