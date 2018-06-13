package com.fmtech.fmhttp.http.download.dao;

import android.database.Cursor;

import com.fmtech.fmhttp.db.BaseDao;
import com.fmtech.fmhttp.http.download.DownloadItemInfo;
import com.fmtech.fmhttp.http.download.enums.DownloadStatus;
import com.fmtech.fmhttp.http.download.enums.DownloadStopMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

public class DownloadDao extends BaseDao<DownloadItemInfo> {

    private List<DownloadItemInfo> mDownloadItemInfos = Collections.synchronizedList(new ArrayList<DownloadItemInfo>());
    private DownloadItemInfoComparator mDownloadItemInfoComparator = new DownloadItemInfoComparator();

    @Override
    protected String getCreateTableSQL() {
        return "create table if not exists  t_downloadInfo(" + "id Integer primary key, " + "url TEXT not null," + "filePath TEXT not null, " + "displayName TEXT, " + "status Integer, " + "totalLen Long, " + "currentLen Long," + "startTime TEXT," + "finishTime TEXT," + "userId TEXT, " + "httpTaskType TEXT," + "priority  Integer," + "stopMode Integer," + "downloadMaxSizeKey TEXT," + "unique(filePath))";
    }

    @Override
    public List<DownloadItemInfo> query(String sql) {
        return null;
    }

    public int addRecord(String url, String filePath, String displayName, int priority){
        synchronized (DownloadDao.class){
            DownloadItemInfo existDownloadItemInfo = findRecord(url, filePath);
            if(null == existDownloadItemInfo){
                DownloadItemInfo record = new DownloadItemInfo();
                record.setId(generateRecordId());
                record.setUrl(url);
                record.setFilePath(filePath);
                record.setDisplayName(displayName);
                record.setStatus(DownloadStatus.waitting.getValue());
                record.setTotalLen(0L);
                record.setCurrentLen(0L);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                record.setStartTime(simpleDateFormat.format(new Date()));
                record.setFinishTime("0");
                record.setPriority(priority);
                insert(record);

                mDownloadItemInfos.add(record);
                return record.getId();
            }
        }
        return -1;
    }

    public DownloadItemInfo findRecord(String url, String filePath){
        synchronized (DownloadDao.class){
            for(DownloadItemInfo record:mDownloadItemInfos){
                if(record.getUrl().equals(url) && record.getFilePath().equals(filePath)){
                    return record;
                }
            }

            //If no record in memory cache, query record from databse
            DownloadItemInfo where = new DownloadItemInfo();
            where.setUrl(url);
            where.setFilePath(filePath);
            List<DownloadItemInfo> resultList = super.query(where);
            if(resultList.size() > 0){
                return resultList.get(0);
            }
            return null;
        }
    }

    public List<DownloadItemInfo> findRecord(String filePath){
        synchronized (DownloadDao.class){
            DownloadItemInfo where = new DownloadItemInfo();
            where.setFilePath(filePath);
            return query(where);
        }
    }

    public DownloadItemInfo findRecordById(int recordId){
        synchronized (DownloadDao.class){
            for(DownloadItemInfo record:mDownloadItemInfos){
                if(record.getId() == recordId){
                    return record;
                }
            }

            DownloadItemInfo where = new DownloadItemInfo();
            where.setId(recordId);
            List<DownloadItemInfo> resultList = query(where);
            if(resultList.size() > 0){
                return resultList.get(0);
            }
        }
        return null;
    }

    public DownloadItemInfo findSingleRecord(String filePath){
        List<DownloadItemInfo> resultList = findRecord(filePath);
        return null != resultList && resultList.size() > 0?resultList.get(0):null;
    }

    private Integer generateRecordId(){
        int maxId = 0;
        String sql = "select max(id) from" + getTableName();
        synchronized (DownloadDao.class){
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int index = cursor.getColumnIndex("max(id)");
                if(index != -1){
                    Object value = cursor.getInt(index);
                    if(null != value){
                        maxId = Integer.parseInt(String.valueOf(value));
                    }
                }
            }
        }
        return maxId + 1;
    }

    public boolean removeRecordFromMemory(int id){
        synchronized (DownloadItemInfo.class) {
            for(int i = 0; i< mDownloadItemInfos.size(); i++){
                if(mDownloadItemInfos.get(i).getId() == id){
                    mDownloadItemInfos.remove(i);
                    return true;
                }
            }

            return false;
        }
    }

    public int updateRecord(DownloadItemInfo record){
        DownloadItemInfo where = new DownloadItemInfo();
        where.setId(record.getId());
        int result = -1;
        synchronized (DownloadDao.class){
            try {
                result = update(record, where);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(result > 0){
                for(int i = 0; i < mDownloadItemInfos.size(); i++) {
                    if (mDownloadItemInfos.get(i).getId().intValue() == record.getId()) {
                        mDownloadItemInfos.remove(i);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public List<DownloadItemInfo> findAllAutoCancelRecords(){
        List<DownloadItemInfo> resultList = new ArrayList<>();
        synchronized (DownloadDao.class){
            DownloadItemInfo downloadItemInfo = null;
            for(int i=0; i< mDownloadItemInfos.size(); i++){
                downloadItemInfo = mDownloadItemInfos.get(i);
                if(downloadItemInfo.getStatus() != DownloadStatus.failed.getValue()
                        && downloadItemInfo.getStopMode()!= DownloadStopMode.AUTO.getValue()
                        ){
                    resultList.add(downloadItemInfo);
                }
            }
        }

        if(!resultList.isEmpty()){
            Collections.sort(resultList, mDownloadItemInfoComparator);
        }

        return resultList;
    }

    class DownloadItemInfoComparator implements Comparator<DownloadItemInfo>{
        @Override
        public int compare(DownloadItemInfo o1, DownloadItemInfo o2) {
            return o1.getId() - o2.getId();
        }
    }

}
