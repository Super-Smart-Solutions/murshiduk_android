package com.saatco.murshadik.model.FarmVisit;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FarmVisit implements Serializable {

    @SerializedName("FarmName")
    private String farmName;

    @SerializedName("FarmerName")
    private String farmerName;

    @SerializedName("Description")
    private String description;

    @SerializedName("PurposeOfVisit")
    private String purposeOfVisit;

    @SerializedName("Location")
    private String location;

    @SerializedName("Region")
    private String region;

    @SerializedName("City")
    private String city;

    @SerializedName("OrderStatus")
    private StatusOfVisitFarm orderStatus;

    @SerializedName("OrderDate")
    private String orderDate;

    @SerializedName("VisitDate")
    private String visitDate;

    @SerializedName("Medias")
    private ArrayList<MediaOfVisitFarm> medias;

    @SerializedName("Report")
    private ArrayList<MediaOfVisitFarm> report;

    public String getFarmName() {
        return farmName;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public String getDescription() {
        return description;
    }

    public String getPurposeOfVisit() {
        return purposeOfVisit;
    }

    public String getLocation() {
        return location;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public StatusOfVisitFarm getOrderStatus() {
        return orderStatus;
    }

    public ArrayList<MediaOfVisitFarm> getMedias() {
        return medias;
    }

    public ArrayList<MediaOfVisitFarm> getReport() {
        return report;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPurposeOfVisit(String purposeOfVisit) {
        this.purposeOfVisit = purposeOfVisit;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setOrderStatus(StatusOfVisitFarm orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public void setMedias(ArrayList<MediaOfVisitFarm> medias) {
        this.medias = medias;
    }

    public void setReport(ArrayList<MediaOfVisitFarm> report) {
        this.report = report;
    }

    public Date getOrderDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return format.parse(orderDate);
        } catch (Exception e) {
            Log.v("error converting date: ", e.getMessage());
        }
        return null;
    }

    public Date getVisitDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return format.parse(visitDate);
        } catch (Exception e) {
            Log.v("error converting date: ", e.getMessage());
        }
        return null;
    }

    public String getVisitDateString(){
        return visitDate;
    }

}
