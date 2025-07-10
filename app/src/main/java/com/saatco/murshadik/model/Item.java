package com.saatco.murshadik.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Item  implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("category_id")
    private int categoryID;

    @SerializedName("name")
    private String name;

    @SerializedName("name_ar")
    private String nameAr;

    @SerializedName("title")
    private String title;

    @SerializedName("title_ar")
    private String title_ar;

    @SerializedName("icon")
    private String icon;

    @SerializedName("image")
    private String image;

    @SerializedName("desc")
    private String description;

    @SerializedName("desc_ar")
    private String descriptionAr;

    @SerializedName("banner_ar")
    private String bannerAr;

    @SerializedName("bg_color")
    private String bgColor;

    @SerializedName("text_color")
    private String textColor;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("children")
    private ArrayList<Item> childrens = new ArrayList<>();

    @SerializedName("banner")
    private String banner;

    @SerializedName("Image")
    private String articleImage;

    @SerializedName("category_name")
    private String categoryName;

    private boolean isSelected;

    private boolean isChild;

    private boolean isUser;

    @SerializedName("is_approved")
    private boolean isApproved;

    @SerializedName("skill_id")
    private int skillId;

    @SerializedName("cities")
    private ArrayList<City> cities;

    public boolean hasChildren(){
        return childrens != null && !childrens.isEmpty();
    }


    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    private User user;

    public Item(){

    }

    public Item(String name,ArrayList<Item> childrens,boolean isSelected){
        this.nameAr = name;
        this.childrens = childrens;
        this.isSelected = isSelected;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr == null ? name : nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBannerAr() {
        return bannerAr;
    }

    public void setBannerAr(String bannerAr) {
        this.bannerAr = bannerAr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_ar() {
        return title_ar;
    }

    public void setTitle_ar(String title_ar) {
        this.title_ar = title_ar;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDateFormatted(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        DateFormat myDateFormat = new SimpleDateFormat("MMMM d, yyyy");
        String result = this.createdAt;
        Date date;
        try {
            if(this.createdAt != null) {
                date = dateFormat.parse(this.createdAt);
                result = myDateFormat.format(date);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Date getDate(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Date date = new Date();
        try {
            if(this.createdAt != null) {
                date = dateFormat.parse(this.createdAt);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<Item> getChildrens() {
        return childrens;
    }

    public void setChildrens(ArrayList<Item> childrens) {
        this.childrens = childrens;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean child) {
        isChild = child;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    public String getArticleImage() {
        return articleImage;
    }

    public void setArticleImage(String articleImage) {
        this.articleImage = articleImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public String toString() {
        return nameAr;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item){
            return ((Item) obj).getId() == this.getId();
        }
        return false;
    }
}
