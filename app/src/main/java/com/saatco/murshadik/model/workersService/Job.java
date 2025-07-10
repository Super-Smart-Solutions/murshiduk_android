package com.saatco.murshadik.model.workersService;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Job implements Serializable {
    @SerializedName("Id")
    private int id;

    @SerializedName("Name")
    private String name;

    @SerializedName("Description")
    private String description;

    private boolean isAdded = false;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Job)) {
            return false;
        }
        return name.equals(((Job) obj).getName());
    }
}

