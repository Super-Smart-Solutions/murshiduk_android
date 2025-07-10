package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Section implements Serializable {

    @SerializedName("type")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("banner")
    private String banner;

    @SerializedName("url")
    private String url;

    @SerializedName("enabled")
    private String enabled;

    @SerializedName("item")
    private Item item;

    @SerializedName("items")
    private ArrayList<Item> items;

    @SerializedName("categories")
    private ArrayList<Item> categoryList;

    @SerializedName("articles")
    private ArrayList<Item> articleList;

    @SerializedName("article")
    private Section article;

    @SerializedName("Calendar")
    private Section calender;

    @SerializedName("lr")
    private LabReport labReport;

    @SerializedName("l")
    private Lab lab;

    public ArrayList<Item> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<Item> categoryList) {
        this.categoryList = categoryList;
    }

    public ArrayList<Item> getArticleList() {
        return articleList;
    }

    public void setArticleList(ArrayList<Item> articleList) {
        this.articleList = articleList;
    }

    public Section getArticle() {
        return article;
    }

    public void setArticle(Section article) {
        this.article = article;
    }

    public Section getCalender() {
        return calender;
    }

    public void setCalender(Section calender) {
        this.calender = calender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public LabReport getLabReport() {
        return labReport;
    }

    public void setLabReport(LabReport labReport) {
        this.labReport = labReport;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
