package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class City implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("name_ar")
    private String nameAr;

    @SerializedName("weather_identifier")
    private String weatherIdentifier;

    private String dialCode;

    private String code;

    @SerializedName("region_id")
    private int regionId;

    public String getWeatherIdentifier() {
        return weatherIdentifier == null? "OERK" : weatherIdentifier;
    }

    public void setWeatherIdentifier(String weatherIdentifier) {
        this.weatherIdentifier = weatherIdentifier;
    }

    public City(String nameAr, String dialCode, String code) {
        this.nameAr = nameAr;
        this.dialCode = dialCode;
        this.code = code;
    }

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

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }
}
