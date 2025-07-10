package com.saatco.murshadik.model.consultantvideos;


import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class CategoryDataOfConsultantVideos implements Serializable {
    @SerializedName("Id")
    private String id;

    @SerializedName("Name")
    private String name;

    @SerializedName("Image")
    private String image;

    @SerializedName("Sup")
    private ArrayList<CategorySubDataOfConsultantVideos> subData;

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }


    public ArrayList<CategorySubDataOfConsultantVideos> getSubData() {
        return subData;
    }

    @Override
    public String toString() {
        return name;
    }

}