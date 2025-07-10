package com.saatco.murshadik.model.workersService;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Experience implements Serializable {
    @SerializedName("Id")
    private int id;

    @SerializedName("WorkerId")
    private int workerId;

    @SerializedName("Description")
    private String description;

    @SerializedName("FromDate")
    private String fromDate;

    @SerializedName("ToDate")
    private String toDate;


    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getWorkerId() {
        return workerId;
    }

    public String getFromDate() {
        return fromDate.split("[Tt]")[0];
    }

    public String getToDate() {
        return toDate.split("[Tt]")[0];
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}

