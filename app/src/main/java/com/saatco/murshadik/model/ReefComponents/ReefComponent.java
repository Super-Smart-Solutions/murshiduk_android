package com.saatco.murshadik.model.ReefComponents;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReefComponent implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("icon")
    private String icon;
    private boolean isAdded = false;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }


    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReefComponent)) {
            return false;
        }
        return name.equals(((ReefComponent) obj).getName());
    }
}
