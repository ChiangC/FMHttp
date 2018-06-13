package com.fmtech.app.update;

import org.w3c.dom.Document;
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

public class UpdateDbXml {
    private List<UpdateStep> updateSteps;
    private List<CreateVersion> createVersions;

    public UpdateDbXml(Document document){
        {
            NodeList updateStepList = document.getElementsByTagName("updateStep");
            updateSteps = new ArrayList<>();
            for(int i=0; i<updateStepList.getLength();i++){
                Element element = (Element)(updateStepList.item(i));
                UpdateStep step = new UpdateStep(element);
                updateSteps.add(step);
            }
        }

        {
            NodeList createVersionList = document.getElementsByTagName("createVersion");
            createVersions = new ArrayList<>();
            for(int i=0; i<createVersionList.getLength();i++){
                Element element = (Element)(createVersionList.item(i));
                CreateVersion cv = new CreateVersion(element);
                createVersions.add(cv);
            }
        }
    }

    public List<UpdateStep> getUpdateSteps() {
        return updateSteps;
    }

    public void setUpdateSteps(List<UpdateStep> updateSteps) {
        this.updateSteps = updateSteps;
    }

    public List<CreateVersion> getCreateVersions() {
        return createVersions;
    }

    public void setCreateVersions(List<CreateVersion> createVersions) {
        this.createVersions = createVersions;
    }
}
