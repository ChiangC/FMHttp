package com.fmtech.app.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.fmtech.app.User;
import com.fmtech.app.UserDao;
import com.fmtech.fmhttp.db.BaseDaoFactory;
import com.fmtech.fmhttp.fileutil.FileUtil;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

public class UpdateManager {
    private static final String INFO_FILE_DIV = "/";
    private List<User> userList ;
    private File parentFile=new File(Environment.getExternalStorageDirectory(),"update");
    private  File bakFile=new File(parentFile,"backDb");
    private String existVersion;
    private String lastBackupVersion;

    public UpdateManager(){
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }
        if(!bakFile.exists()) {
            bakFile.mkdirs();
        }
    }

    public void checkThisVersionTable(Context context){
        UserDao userDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class, User.class);

        userList = userDao.query(new User());
        UpdateDbXml updateDbXml = readDbXml(context);

        String thisVersion = getVersionName(context);
        CreateVersion thisCreateVersion = analyseCreateVersion(updateDbXml, thisVersion);
        try {
            executeCreateVersion(thisCreateVersion, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UpdateDbXml readDbXml(Context context){
        InputStream inputStream = null;
        Document document = null;
        try {
            inputStream = context.getAssets().open("updateXml.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (document == null)
        {
            return null;
        }

        UpdateDbXml updateDbXml = new UpdateDbXml(document);
        return updateDbXml;
    }

    private String getVersionName(Context context){
        String versionName = null;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private CreateVersion analyseCreateVersion(UpdateDbXml updateDbXml, String version){
        CreateVersion createVersion = null;
        if(null == updateDbXml || null == version){
            return createVersion;
        }

        List<CreateVersion> createVersions = updateDbXml.getCreateVersions();
        if(null != createVersions){
            for(CreateVersion item : createVersions){
                String[] versions = item.getVersion().trim().split(",");
                for(int i=0; i < versions.length;i++){
                    if(versions[i].trim().equalsIgnoreCase(version)){
                        createVersion = item;
                        break;
                    }
                }
            }
        }

        return createVersion;
    }

    private void executeCreateVersion(CreateVersion createVersion, boolean isLogic)throws Exception{
        if(null == createVersion || null == createVersion.getCreateDbs()){
            throw new Exception("CreateVersion or createDbs is null;");
        }

        for(CreateDb createDb:createVersion.getCreateDbs()){
            if(null == createDb || null == createDb.getName()){
                throw new Exception("Db or dbName is null when createVersion.");
            }

            if(!"logic".equals(createDb.getName())){
                continue;
            }

            //Sqls for creating database
            List<String> sqls = createDb.getSqlCreates();
            SQLiteDatabase database = null;
            try{
                if(null != userList && !userList.isEmpty()){
                    for(int i=0; i< userList.size();i++){
                        database = getDatabase(createDb, userList.get(i).getUser_id());
                        executeSql(database, sqls);
                        database.close();
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(null != database){
                    database.close();
                }
            }
        }
    }

    private SQLiteDatabase getDatabase(UpdateDb db, String userId) {
        return getDatabase(db.getDbName(), userId);
    }

    private SQLiteDatabase getDatabase(CreateDb db, String userId) {
        return getDatabase(db.getName(), userId);
    }

    private SQLiteDatabase getDatabase(String dbname, String userId) {
        SQLiteDatabase sqlitedb = null;
        String dbFilePath = null;
        File file = new File(parentFile, userId);
        if(!file.exists()){
            file.mkdirs();
        }
        if(dbname.equalsIgnoreCase("logic")){
            dbFilePath = file.getAbsolutePath() + "/logic.db";
        }else if(dbname.equalsIgnoreCase("user")){
            dbFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/user.db";
        }

        if(null != dbFilePath){
            File dbFile = new File(dbFilePath);
            dbFile.mkdirs();
            if(dbFile.isDirectory()){
                dbFile.delete();
            }
            sqlitedb = SQLiteDatabase.openOrCreateDatabase(dbFilePath, null);
        }
        return sqlitedb;
    }

    private void executeSql(SQLiteDatabase sqlitedb, List<String> sqls)throws Exception{
        if(null == sqlitedb || null == sqls || sqls.size() == 0){
            return;
        }

        sqlitedb.beginTransaction();

        for(String sql:sqls){
            sql = sql.replaceAll("\r\n", " ");
            sql = sql.replaceAll("\n", " ");
            if(!"".equals(sql.trim())){
                try{
                    sqlitedb.execSQL(sql);
                }catch (SQLException e){

                }
            }
        }

        sqlitedb.setTransactionSuccessful();
        sqlitedb.endTransaction();
    }

    public void startUpdateDb(Context context){
        UpdateDbXml updateDbXml = readDbXml(context);
        if(getLocalVersionInfo()){
            String thisVersion = getVersionName(context);
            String lastVersion = lastBackupVersion;
            UpdateStep updateStep = analyseUpdateStep(updateDbXml, lastVersion, thisVersion);

            if(null == updateStep){
                return;
            }

            List<UpdateDb> updateDbs = updateStep.getUpdateDbs();
            CreateVersion createVersion = analyseCreateVersion(updateDbXml, thisVersion);

            try {
                //update all user's database
                for(User user:userList){
                    String logicDbDir = parentFile.getAbsolutePath() + "/update" + "/" +user.getUser_id() + "/logic.db";
                    String logicCopy = bakFile.getAbsolutePath() + "/" + user.getUser_id() + "/logic.db";
                    FileUtil.CopySingleFile(logicDbDir, logicCopy);
                }

                //backup user.db
                String user = parentFile.getAbsolutePath() + "/user.db";
                String user_bak = bakFile.getAbsolutePath() + "/user.db";
                FileUtil.CopySingleFile(user, user_bak);

                //delete or backup old tables
                executeDb(updateDbs, -1);

                //check table or create new table
                executeCreateVersion(createVersion, false);

                //Recover data from backup table and delete backup table after recovery.
                executeDb(updateDbs, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Upgrade successfully, delete backup database
            if(null != userList && !userList.isEmpty()){
                for(User user:userList){
                    String loginDbDir = parentFile.getAbsolutePath() + "/update" + "/" + user.getUser_id() + ".db";
                    File file = new File(loginDbDir);
                    if(file.exists()){
                        file.delete();
                    }
                }
            }
            File userFileBak = new File(bakFile.getAbsolutePath() + "user_bak.db");
            if (userFileBak.exists()) {
                userFileBak.delete();
            }
        }
    }

    private boolean getLocalVersionInfo(){
        boolean result = false;
        File file = new File(parentFile, "update.txt");
        if(file.exists()){
            int byteRead = 0;
            byte[] tempBytes = new byte[100];
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = null;
            try{
                inputStream = new FileInputStream(file);
                while ((byteRead = inputStream.read(tempBytes)) != -1){
                    stringBuilder.append(new String(tempBytes, 0, byteRead));
                }
                String[] infos = stringBuilder.toString().split(INFO_FILE_DIV);
                if(infos.length == 2){
                    existVersion = infos[0];
                    lastBackupVersion = infos[1];
                    result = true;
                }
            }catch (Exception ex){

            }finally {
                if(null != inputStream){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    inputStream = null;
                }
            }
        }
        return result;
    }

    private UpdateStep analyseUpdateStep(UpdateDbXml updateDbXml, String lastVersion, String thisVersion){
        if(null == updateDbXml || null == lastVersion ||null == thisVersion){
            return null;
        }

        UpdateStep thisStep = null;
        List<UpdateStep> updateSteps = updateDbXml.getUpdateSteps();
        if(null == updateSteps || updateSteps.size() == 0){
            return null;
        }

        for(UpdateStep step:updateSteps){
            if(null == step.getVersionFrom() || null == step.getVersionTo()){

            }else{
                String[] lastVersionArray = step.getVersionFrom().split(",");
                if(null != lastVersionArray && lastVersionArray.length > 0){
                    for(int i = 0; i < lastVersionArray.length;i++){
                        if(lastVersion.equalsIgnoreCase(lastVersionArray[i]) &&
                                step.getVersionTo().equalsIgnoreCase(thisVersion)){
                            thisStep = step;
                            break;
                        }
                    }
                }
            }
        }

        return thisStep;
    }

    private void executeDb(List<UpdateDb> updateDbs, int type) throws Exception {
        if(null == updateDbs){
            throw new Exception("updateDbs is null");
        }

        for(UpdateDb db : updateDbs){
            if(null == db || null == db.getDbName()){
                throw new Exception("db or dbName is null;");
            }
            List<String> sqls = null;
            if(type < 0){
                sqls = db.getSqlBefores();
            }else if(type > 0){
                sqls = db.getSqlAfters();
            }

            SQLiteDatabase sqlitedb = null;

            try{
                if(null != userList && !userList.isEmpty()){
                    for(int i = 0; i<userList.size(); i++){
                        sqlitedb = getDatabase(db, userList.get(i).getUser_id());
                        executeSql(sqlitedb, sqls);
                        sqlitedb.close();
                    }
                }
            }catch (Exception ex){

            }finally {
                if(null != sqlitedb){
                    sqlitedb.close();
                }
            }
        }
    }

    public boolean saveVersionInfo(Context context, String newVersion){
        boolean result = false;
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(parentFile, "update.txt"), false);
            writer.write("V003" + INFO_FILE_DIV + "V002");
            writer.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != writer){
                try{
                    writer.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
