package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

public class MesiboTokenObject {

    @SerializedName("uid")
    private int uid;

    @SerializedName("token")
    private String token;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
