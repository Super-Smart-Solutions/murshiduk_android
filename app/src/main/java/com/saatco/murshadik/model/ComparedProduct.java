package com.saatco.murshadik.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.FarmVisit.MediaOfVisitFarm;
import com.saatco.murshadik.model.FarmVisit.StatusOfVisitFarm;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ComparedProduct implements Serializable {

    @SerializedName("Price")
    private String price;

    @SerializedName("ProductName")
    private String productName;

    @SerializedName("MarketName")
    private String marketName;

    @SerializedName("MarketId")
    private int marketId;

    @SerializedName("Unit_Value")
    private String unitValue;

    @SerializedName("Unite")
    private String unit;

    @SerializedName("type")
    private String type;

    public String getUnitValue() {
        return unitValue;
    }

    public String getUnit() {
        return unit;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public String getMarketName() {
        return marketName;
    }

    public int getMarketId() {
        return marketId;
    }

    @SerializedName("date")
    private String date;

    public Date getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return format.parse(date);
        } catch (Exception e) {
            Log.v("error converting date: ", e.getMessage());
        }
        return null;
    }


}
