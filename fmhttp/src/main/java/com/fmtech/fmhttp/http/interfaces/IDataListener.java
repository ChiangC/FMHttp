package com.fmtech.fmhttp.http.interfaces;

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

public interface IDataListener<M> {
    void onSuccess(M result);

    void onFail();

}
