package com.saatco.murshadik.model.consultantvideos;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.User;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentOfConsultantVideo implements Serializable {
    @SerializedName("Id")
    private int id;

    @SerializedName("Comment")
    private String comment;

    @SerializedName("User")
    private String user;

    @SerializedName("Date")
    private String date;

    @SerializedName("Reply")
    private ArrayList<CommentOfConsultantVideo> reply;

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }

    public Date getDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return format.parse(date);
        }catch (Exception e){
            Log.v("error converting date: ", e.getMessage());
        }
        return null;
    }

    public String getStringDate(){
        return date;
    }

    public ArrayList<CommentOfConsultantVideo> getReply() {
        return reply;
    }


}
