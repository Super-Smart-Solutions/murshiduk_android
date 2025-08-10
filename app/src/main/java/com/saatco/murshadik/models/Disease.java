package com.saatco.murshadik.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Disease {

    public Disease() {
    }

    public Disease(int id, String englishName, String arabicName, String scientificName, String symptoms, String description, String treatments, List<String> stages, String createdAt, String updatedAt) {
        this.id = id;
        this.englishName = englishName;
        this.arabicName = arabicName;
        this.scientificName = scientificName;
        this.symptoms = symptoms;
        this.description = description;
        this.treatments = treatments;
        this.stages = stages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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