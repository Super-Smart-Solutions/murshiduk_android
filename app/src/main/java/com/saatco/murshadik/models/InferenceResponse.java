package com.saatco.murshadik.models;

import com.google.gson.annotations.SerializedName;

public class InferenceResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}