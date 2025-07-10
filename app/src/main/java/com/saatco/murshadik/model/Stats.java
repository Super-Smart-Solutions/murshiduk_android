package com.saatco.murshadik.model;

import com.saatco.murshadik.api.APIClient;

import java.io.Serializable;

public class Stats implements Serializable {

    private String bgColor;
    private int total;
    private String title;
    private String icon;
    private String desc;

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return APIClient.imageUrl + icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
