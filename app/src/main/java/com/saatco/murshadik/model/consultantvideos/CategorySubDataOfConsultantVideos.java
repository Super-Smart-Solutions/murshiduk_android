package com.saatco.murshadik.model.consultantvideos;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategorySubDataOfConsultantVideos implements Serializable {
    @SerializedName("Id")
    private String id;

    @SerializedName("Name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}