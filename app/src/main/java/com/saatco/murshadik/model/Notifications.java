package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notifications implements Serializable , Comparable<Notifications> {

    @SerializedName("id")
    private int id;

    @SerializedName("text")
    private String text;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private int type;

    @SerializedName("type_id")
    private int typeId;

    @SerializedName("created_at")
    private String date;

    @SerializedName("notification_at")
    private String notificationAt;

    @SerializedName("link")
    private String link;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationAt() {
        return notificationAt;
    }

    public void setNotificationAt(String notificationAt) {
        this.notificationAt = notificationAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getDate() {
      /* SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        try {
            date  = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        return  date;
    }

    private Date getDateTime(){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.US);

        Date date = null;

        try {
            date  = fromUser.parse(this.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date == null ? new Date() : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int compareTo(Notifications notifications) {
        return getDateTime().compareTo(notifications.getDateTime());
    }
}
