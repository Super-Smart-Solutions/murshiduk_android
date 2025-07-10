package com.saatco.murshadik.model.clinicService;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Clinic implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String img;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImg() {
        return img;
    }
}
