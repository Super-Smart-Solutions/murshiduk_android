package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

public class RegionResponse {

    @SerializedName("data")
    private ArrayList<Item> regions;

    @SerializedName("regions")
    private ArrayList<Item> regionList;

    public ArrayList<Item> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Item> regions) {
        this.regions = regions;
    }

    public ArrayList<Item> getRegionList() {
        return regionList;
    }

    public void setRegionList(ArrayList<Item> regionList) {
        this.regionList = regionList;
    }
}
