package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

public class Mobile {

    @SerializedName("id")
    private int id;

    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
