package com.saatco.murshadik.model.clinicService;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClinicAppointment implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String docName;

    @SerializedName("avatar")
    private String docAvatar;

    @SerializedName("clinic_name")
    private String clinicName;

    @SerializedName("clinic_icon")
    private String clinicIcon;


    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;


    @SerializedName("call_duration")
    private int callDuration;

    @SerializedName("chatId")
    private String chatId;



    public int getId() {
        return id;
    }

    public String getDocName() {
        return docName;
    }

    public String getDocAvatar() {
        return docAvatar;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getClinicIcon() {
        return clinicIcon;
    }

    public String getDateTime() {
        return date.split("T")[0] + "T" + time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getCallDuration() {
        return callDuration;
    }

    public String getChatId() {
        return chatId;
    }
}
