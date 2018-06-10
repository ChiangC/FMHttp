package com.fmtech.fmhttp.http.interfaces;

import org.apache.http.HttpEntity;

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

public interface IHttpListener {
    void onSuccess(HttpEntity httpEntity);

    void onFail();
}
