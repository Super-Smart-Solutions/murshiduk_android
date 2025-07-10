package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Notifications;

import java.util.ArrayList;

public class WeatherNotificationResponse {

    @SerializedName("data")
    private ArrayList<Notifications> notifications;

    public ArrayList<Notifications> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notifications> notifications) {
        this.notifications = notifications;
    }
}
