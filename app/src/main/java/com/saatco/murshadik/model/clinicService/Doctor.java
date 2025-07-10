package com.saatco.murshadik.model.clinicService;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Doctor implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("rating")
    private float rating;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public float getRating() {
        return rating;
    }

    public String getRatingString() {
        return String.valueOf(rating);
    }
}
