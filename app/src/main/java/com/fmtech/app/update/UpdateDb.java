package com.fmtech.app.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
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

public class UpdateDb {
    private String dbName;
    private List<String> sqlBefores;
    private List<String> sqlAfters;

    public UpdateDb(Element element){
        dbName = element.getAttribute("name");
        sqlBefores = new ArrayList<>();
        sqlAfters = new ArrayList<>();

        {
            NodeList sqls = element.getElementsByTagName("sql_before");
            for(int i=0; i<sqls.getLength();i++){
                String sql_before = sqls.item(i).getTextContent();
                sqlBefores.add(sql_before);
            }
        }

        {
            NodeList sqls = element.getElementsByTagName("sql_after");
            for(int i=0; i<sqls.getLength();i++){
                String sql_after = sqls.item(i).getTextContent();
                sqlAfters.add(sql_after);
            }
        }
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<String> getSqlBefores() {
        return sqlBefores;
    }

    public void setSqlBefores(List<String> sqlBefores) {
        this.sqlBefores = sqlBefores;
    }

    public List<String> getSqlAfters() {
        return sqlAfters;
    }

    public void setSqlAfters(List<String> sqlAfters) {
        this.sqlAfters = sqlAfters;
    }
}
