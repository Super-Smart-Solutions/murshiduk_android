package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ContactUs implements Serializable {

    @SerializedName("AP_CUSTOMER_SUPPORT_NUMBER")
    public String supportNumber;

    @SerializedName("AP_CUSTOMER_SUPPORT_WA_NUMBER")
    public String whatsApp;

    @SerializedName("AP_SYSTEM_EMAIL")
    public String email;

    @SerializedName("AP_FACEBOOK_URL")
    public String facebook;

    @SerializedName("AP_TWITTER_URL")
    public String twitter;

    @SerializedName("AP_SNAP_URL")
    public String snapChat;

    @SerializedName("AP_INSTA_URL")
    public String instagram;

    @SerializedName("AP_YOUTUBE_URL")
    public String youtube;

    public ContactUs(){}
    public ContactUs(String supportNumber){
        this.supportNumber = supportNumber;
        this.whatsApp = supportNumber;
    }

}
