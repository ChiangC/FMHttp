package com.fmtech.fmhttp.http.download.enums;

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

public enum DownloadStatus {
    waitting(0),
    starting(1),
    downloading(2),
    pause(3),
    finish(4),
    failed(5);

    private int value;

    private DownloadStatus(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
