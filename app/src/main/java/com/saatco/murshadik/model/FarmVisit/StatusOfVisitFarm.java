package com.saatco.murshadik.model.FarmVisit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusOfVisitFarm implements Serializable {

    @SerializedName("Status")
    private int status;

    @SerializedName("description")
    private String description;

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
