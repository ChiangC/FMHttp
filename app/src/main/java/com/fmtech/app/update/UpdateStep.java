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

public class UpdateStep {
    private String versionFrom;
    private String versionTo;
    private List<UpdateDb> updateDbs;

    public UpdateStep(Element element){
        versionFrom = element.getAttribute("versionFrom");
        versionTo = element.getAttribute("versionTo");
        updateDbs = new ArrayList<>();

        NodeList dbs = element.getElementsByTagName("updateDb");
        for(int i=0; i<dbs.getLength(); i++){
            Element db = (Element)(dbs.item(i));
            UpdateDb updateDb = new UpdateDb(db);
            updateDbs.add(updateDb);
        }
    }

    public String getVersionFrom() {
        return versionFrom;
    }

    public void setVersionFrom(String versionFrom) {
        this.versionFrom = versionFrom;
    }

    public String getVersionTo() {
        return versionTo;
    }

    public void setVersionTo(String versionTo) {
        this.versionTo = versionTo;
    }

    public List<UpdateDb> getUpdateDbs() {
        return updateDbs;
    }

    public void setUpdateDbs(List<UpdateDb> updateDbs) {
        this.updateDbs = updateDbs;
    }
}
