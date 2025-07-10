package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

public class Slider {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("title_ar")
    private String titleAr;

    @SerializedName("image")
    private String image;

    @SerializedName("image_ar")
    private String imageAr;

    @SerializedName("desc")
    private String description;

    @SerializedName("desc_ar")
    private String descriptionAr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageAr() {
        return imageAr;
    }

    public void setImageAr(String imageAr) {
        this.imageAr = imageAr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }
}
