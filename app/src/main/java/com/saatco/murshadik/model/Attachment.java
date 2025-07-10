package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Attachment implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
