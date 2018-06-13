package com.fmtech.app;

import com.fmtech.fmhttp.annotation.DatabaseTable;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @version v1.0.0
 * @email chiangchuna@gmail.com
 * @create_date 2018/6/13 09:52
 * <p>
 * ==================================================================
 */

@DatabaseTable("tb_photo")
public class Photo {
    public String time;

    public String path;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
