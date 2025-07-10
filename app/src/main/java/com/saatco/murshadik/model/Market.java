package com.saatco.murshadik.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class Market implements Serializable {


    @SerializedName("id")
    private int id;

    @SerializedName("marketname")
    private String name;

    @SerializedName("location")
    private String location;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("address")
    private String address;

    @SerializedName("open_at")
    private String openAt;

    @SerializedName("close_at")
    private String closeAt;

    @SerializedName("region_id")
    private int regionId;

    @SerializedName("market_image")
    private String marketImage;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenAt() {

        SimpleDateFormat fromUser = new SimpleDateFormat("hh:mm:ss", Locale.US);
        // make clock 12 hours format with AM/PM
        SimpleDateFormat myFormat = new SimpleDateFormat("hh:mm a", new Locale("ar", "SA"));
        String temp = "";
        try {
            temp  = myFormat.format(Objects.requireNonNull(fromUser.parse(openAt)));
        } catch (ParseException e) {
            Log.e("error", "format time", e);
        }

        return temp;
    }

    public Boolean isOpened() {
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.US).format(System.currentTimeMillis());

        return currentTime.compareTo(getOpenAt()) >= 0 && currentTime.compareTo(getCloseAt()) <= 0;
    }

    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }

    public String getCloseAt() {

        SimpleDateFormat fromUser = new SimpleDateFormat("hh:mm:ss", Locale.US);
        SimpleDateFormat myFormat = new SimpleDateFormat("hh:mm a", new Locale("ar", "SA"));

        String temp = "";
        try {
            temp  = myFormat.format(Objects.requireNonNull(fromUser.parse(closeAt)));
        } catch (ParseException e) {
            Log.e("error", "format time", e);
        }

        return temp;
    }

    public void setCloseAt(String closeAt) {
        this.closeAt = closeAt;
    }

    public Market(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        String lati = "24.5806831";

        if(location.matches(".*[0-9].*") && location.length() > 2) {
            String[] newLoc = location.replace(" ", "").split(",");
            if(newLoc[0] != null && !newLoc[0].equals(""))
              lati = newLoc[0];
        }
        return lati;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        String longi = "46.5183167";
        if(location.matches(".*[0-9].*") && location.length() > 2) {
            String[] newLoc = location.replace(" ", "").split(",");
            if(newLoc[1] != null && !newLoc[1].isEmpty())
               longi = newLoc[1];
        }
        return longi;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getMarketImage() {
        return marketImage;
    }

    public void setMarketImage(String marketImage) {
        this.marketImage = marketImage;
    }
}
