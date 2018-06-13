package com.fmtech.fmhttp.db;

import android.os.Environment;

import java.io.File;

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

public enum PrivateDataBaseEnums {
    database("data/data/database/");

    private String value;

    PrivateDataBaseEnums(String value) {
        this.value = value;
    }

    public String getValue() {
        /*UserDao userDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class, User.class);
        if (userDao != null) {
            User currentUser = userDao.getCurrentUser();
            if (currentUser != null) {
                File file = new File(Environment.getExternalStorageDirectory(), "update");
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath() + "/" + currentUser.getUser_id() + "/logic.db";
            }

        }*/
        return value;
    }
}
