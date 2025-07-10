package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Group implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("region_id")
    private int regionId;

    @SerializedName("name_ar")
    private String nameAr;

    @SerializedName("region")
    private Group region;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public Group getRegion() {
        return region;
    }

    public void setRegion(Group region) {
        this.region = region;
    }
}
