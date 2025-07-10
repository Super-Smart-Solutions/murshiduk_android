package com.saatco.murshadik.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Disease {

    @SerializedName("english_name")
    private String englishName;

    @SerializedName("arabic_name")
    private String arabicName;

    @SerializedName("scientific_name")
    private String scientificName;

    @SerializedName("symptoms")
    private String symptoms;

    @SerializedName("description")
    private String description;

    @SerializedName("treatments")
    private String treatments;

    @SerializedName("stages")
    private List<String> stages;

    @SerializedName("id")
    private int id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public String getEnglishName() {
        return englishName;
    }

    public String getArabicName() {
        return arabicName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getDescription() {
        return description;
    }

    public String getTreatments() {
        return treatments;
    }

    public List<String> getStages() {
        return stages;
    }

    public int getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}