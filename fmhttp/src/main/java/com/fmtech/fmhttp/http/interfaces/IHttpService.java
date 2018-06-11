package com.fmtech.fmhttp.http.interfaces;

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

public interface IHttpService {
    void setUrl(String url);

    void excute();

    void setHttpListener(IHttpListener listener);

    void setRequestData(byte[] requestData);

    void pause();

    boolean cancel();

    boolean isCanceled();

    boolean isPaused();

    Map<String, String> getRequestHeaderMap();

}
