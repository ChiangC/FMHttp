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

public class CreateDb {
    //Database name
    private String name;

    // sql to create database table
    private List<String> sqlCreates;

    public CreateDb(Element element){
        name = element.getAttribute("name");
        {
            sqlCreates = new ArrayList<>();
            NodeList sqls = element.getElementsByTagName("sql_createTable");
            for(int i = 0; i < sqls.getLength(); i++){
                String sqlCreate = sqls.item(i).getTextContent();
                sqlCreates.add(sqlCreate);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSqlCreates() {
        return sqlCreates;
    }

    public void setSqlCreates(List<String> sqlCreates) {
        this.sqlCreates = sqlCreates;
    }
}
