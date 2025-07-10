package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Appointment implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("appointment_time")
    private String time;

    @SerializedName("is_booked")
    private boolean isBooked;

    @SerializedName("appointment_date")
    private String date;

    @SerializedName("is_canceled")
    private boolean isCanceled;

    @SerializedName("is_completed")
    private boolean isCompleted;

    @SerializedName("is_sample_collected")
    private boolean isSampleCollected;

    @SerializedName("lab_name")
    private String labName;

    @SerializedName("lab")
    private Lab lab;

    private boolean isSelected;

    public String getDate() {
        return date.replace("T00:00:00","");
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public String getStatus()
    {
        if(isCanceled)
            return "ألغيت";
        else if(isCompleted)
            return "تفاصيل";
        else if(isSampleCollected)
            return "تم استلام العينة";
        return "قيد الانتظار";
    }

    public String getStatusColor()
    {
        if(isCanceled)
            return "#eb285c";
        else if(isCompleted || isSampleCollected)
            return "#55a154";
        return "#b6842f";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {

        SimpleDateFormat fromUser = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
        SimpleDateFormat myFormat = new SimpleDateFormat("hh:mm a",Locale.ENGLISH);

        try {
            time  = myFormat.format(fromUser.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }
}
