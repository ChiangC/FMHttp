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

public class CreateVersion {
    private String version;

    private List<CreateDb> createDbs;

    public CreateVersion(Element element){
        version = element.getAttribute("version");
        {
            createDbs = new ArrayList<>();
            NodeList cs = element.getElementsByTagName("createDb");
            for(int i = 0; i< cs.getLength(); i++){
                Element ci = (Element)(cs.item(i));
                CreateDb cd = new CreateDb(ci);
                createDbs.add(cd);
            }
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<CreateDb> getCreateDbs() {
        return createDbs;
    }

    public void setCreateDbs(List<CreateDb> createDbs) {
        this.createDbs = createDbs;
    }
}
