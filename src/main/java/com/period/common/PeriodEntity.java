package com.period.common;

/**
 * Created by vali on 15-6-11.
 */
public class PeriodEntity {

    private String key;

    private String value;

    private String desc;

    public PeriodEntity(String key, String value, String desc) {
        this.key = key;
        this.value = value;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
