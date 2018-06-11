package com.fmtech.fmhttp.http.download.enums;

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

public enum Priority {
    LOW(0),

    MEDIUM(1),

    HIGH(2);

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    Priority(int value){
        this.value = value;
    }

    public static Priority getInstance(int value){
        for(Priority priority: Priority.values()){
            if(priority.getValue() == value){
                return priority;
            }
        }
        return Priority.MEDIUM;
    }

}
