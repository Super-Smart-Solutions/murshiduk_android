package com.saatco.murshadik.models;

import com.google.gson.annotations.SerializedName;

public class UploadImageResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("message")
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}