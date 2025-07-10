package com.saatco.murshadik.model.workersService;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Nationality implements Serializable {
    @SerializedName("Id")
    private String id;

    @SerializedName("Name")
    private String name;

    @SerializedName("NameArabic")
    private String nameArabic;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameArabic() {
        return nameArabic;
    }

    @NonNull
    @Override
    public String toString() {
        return nameArabic;
    }
}
