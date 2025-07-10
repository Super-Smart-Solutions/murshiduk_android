package com.saatco.murshadik.model.consultantvideos;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoDataOfConsultantVideos implements Serializable {
    @SerializedName("Id")
    private String id;

    @SerializedName("Title")
    private String title;

    @SerializedName("CreatAt")
    private String creatAt;

    @SerializedName("Description")
    private String description;

    @SerializedName("Location")
    private String location;

    @SerializedName("ConsultantName")
    private String consultantName;

    @SerializedName("Category")
    private String category;

    @SerializedName("Department")
    private String department;

    @SerializedName("UserId")
    private String userId;

    @SerializedName("Tag")
    private String tag;

    @SerializedName("VideoStatus")
    private int videoStatus;

    @SerializedName("VideoLike")
    private int videoLike;

    @SerializedName("IsHidden")
    private boolean isHidden;

    public boolean isHidden() {
        return isHidden;
    }

    public int getVideoLike() {
        return videoLike;
    }

    public int getVideoStatus() {
        return videoStatus;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getConsultantName() {
        return consultantName;
    }

    public String getCategory() {
        return category;
    }

    public String getDepartment() {
        return department;
    }

    public String getUserId() {
        return userId;
    }

    public String getTag() {
        return tag;
    }

    public Date getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        try {
            return format.parse(creatAt);
        } catch (Exception e) {
            Log.v("error converting date: ", e.getMessage());
        }
        return null;
    }


}
