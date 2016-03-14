package com.cyan.bean;

/**
 * Created by Administrator on 2016/3/13.
 */
public class RefreshData {
    private String type;
    private String id;
    private Object value;

    public RefreshData(String id, String type, Object value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
