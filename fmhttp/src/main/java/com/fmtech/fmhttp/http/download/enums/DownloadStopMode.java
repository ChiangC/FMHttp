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

public enum DownloadStopMode {
    AUTO(0),
    HAND(1);

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    DownloadStopMode(Integer value){
        this.value = value;
    }

    public static DownloadStopMode getInstance(int value){
        for(DownloadStopMode downloadStopMode:DownloadStopMode.values()){
            if(downloadStopMode.getValue() == value){
                return downloadStopMode;
            }
        }
        return DownloadStopMode.AUTO;
    }
}
