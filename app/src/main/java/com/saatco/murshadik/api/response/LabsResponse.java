package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Lab;

import java.util.ArrayList;

public class LabsResponse {

    @SerializedName("data")
    private ArrayList<Lab> labsList;

    @SerializedName("labs")
    private ArrayList<Lab> labs;

    @SerializedName("regions")
    private ArrayList<Item> regions;

    public ArrayList<Item> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Item> regions) {
        this.regions = regions;
    }

    public ArrayList<Lab> getLabsList() {
        return labsList;
    }

    public void setLabsList(ArrayList<Lab> labsList) {
        this.labsList = labsList;
    }

    public ArrayList<Lab> getLabs() {
        return labs;
    }

    public void setLabs(ArrayList<Lab> labs) {
        this.labs = labs;
    }
}
