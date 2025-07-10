package com.saatco.murshadik.models;

import com.google.gson.annotations.SerializedName;

public class InferenceResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("plant_id")
    private int plantId;

    @SerializedName("image_id")
    private int imageId;

    @SerializedName("disease_id")
    private int diseaseId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("api_key_id")
    private int apiKeyId;

    @SerializedName("org_id")
    private int orgId;

    @SerializedName("confidence_level")
    private double confidenceLevel;

    @SerializedName("status")
    private int status;

    @SerializedName("approved")
    private boolean approved;

    @SerializedName("attention_map_key")
    private String attentionMapKey;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("attention_map_url")
    private String attentionMapUrl;

    public int getId() {
        return id;
    }

    public int getPlantId() {
        return plantId;
    }

    public int getImageId() {
        return imageId;
    }

    public int getDiseaseId() {
        return diseaseId;
    }

    public String getUserId() {
        return userId;
    }

    public int getApiKeyId() {
        return apiKeyId;
    }

    public int getOrgId() {
        return orgId;
    }

    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    public int getStatus() {
        return status;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getAttentionMapKey() {
        return attentionMapKey;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAttentionMapUrl() {
        return attentionMapUrl;
    }
}
