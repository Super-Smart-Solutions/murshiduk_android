package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;

public class CoffeePlantResponse {

    @SerializedName("class_id")
    private int type;

    @SerializedName("confidence")
    private double confidence;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public String getTypeClass() {

        switch (type)
        {
            case 0:
                return "Healthy";
            case 1:
                return "Miner";
            case 2:
                return "Rust";
            case 3:
                return "Phoma";
            case 4:
                return "Cercospora";
            case 5:
                return "Red Spider Mite";
            default:
                return "Healthy";
        }

    }

    public int getClassId() {

        switch (type)
        {
            case 0:
                return 0;
            case 1:
                return 1078;
            case 2:
                return 1077;
            case 3:
                return 1079;
            case 4:
                return 1080;
            case 5:
                return 0;
            default:
                return 0;
        }

    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
