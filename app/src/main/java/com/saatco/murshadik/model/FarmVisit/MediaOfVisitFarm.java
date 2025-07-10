package com.saatco.murshadik.model.FarmVisit;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MediaOfVisitFarm implements Serializable {

    @SerializedName("FileName")
    private String fileName;

    @SerializedName("FileCategory")
    private String fileCategory;

    @SerializedName("Description")
    private String description;

    @SerializedName("Path")
    private String path;

    public String getFileName() {
        return fileName;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public String getPath() {
        return path;
    }

    @NonNull
    @Override
    public String toString() {
        return description;
    }
}
