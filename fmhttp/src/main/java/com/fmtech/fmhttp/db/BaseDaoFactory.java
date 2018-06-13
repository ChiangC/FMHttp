package com.fmtech.fmhttp.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.util.Collections;
import java.util.HashMap;
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

public class BaseDaoFactory {
    private String mDataBasePath;
    private SQLiteDatabase mSQLiteDatabase;
    private SQLiteDatabase mUserDatabase;
    private static BaseDaoFactory sInstance = new BaseDaoFactory();
    private Map<String,BaseDao> map= Collections.synchronizedMap(new HashMap<String, BaseDao>());

    private BaseDaoFactory(){
        mDataBasePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/user.db";
        openDatabase();
    }

    private void openDatabase(){
        mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(mDataBasePath, null);
    }

    public static BaseDaoFactory getInstance(){
        return sInstance;
    }

    public synchronized <T extends BaseDao<M>, M> T getDataHelper(Class<T> baseDaoClazz, Class<M> entityClazz){
        BaseDao baseDao = null;
        if(null != map.get(baseDaoClazz.getSimpleName())){
            return (T) map.get(baseDaoClazz.getSimpleName());
        }
        try {
            baseDao = baseDaoClazz.newInstance();
            baseDao.init(entityClazz, mSQLiteDatabase);
            map.put(baseDaoClazz.getSimpleName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T)baseDao;
    }

    public synchronized <T extends BaseDao<M>, M> T getUserHelper(Class<T> baseDaoClazz, Class<M> entityClazz){
        mUserDatabase = SQLiteDatabase.openOrCreateDatabase(PrivateDataBaseEnums.database.getValue(), null);
        BaseDao baseDao = null;
        try {
            baseDao = baseDaoClazz.newInstance();
            baseDao.init(entityClazz, mUserDatabase);
            map.put(baseDaoClazz.getSimpleName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T)baseDao;
    }
}
