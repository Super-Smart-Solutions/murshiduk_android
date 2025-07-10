package com.saatco.murshadik.model.consultantvideos;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoLikesOfConsultantVideos implements Serializable {
    @SerializedName("likecount")
    private int likeCount;

    @SerializedName("Dislikecount")
    private int dislikeCount;

    @SerializedName("LikeStatus")
    private int likeStatus;


    public int getLikeCount() {
        return likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public int getLikeStatus() {
        return likeStatus;
    }
}
