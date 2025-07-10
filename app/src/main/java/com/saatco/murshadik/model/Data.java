package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {

    @SerializedName("lr")
    private LabReport labReport;

    @SerializedName("l")
    private Lab lab;

    @SerializedName("market")
    private Market market;

    @SerializedName("products")
    private ArrayList<Product> products;

    @SerializedName("product_price")
    private ArrayList<Product> productPrices;

    @SerializedName("group_message")
    private ArrayList<Message> groupMessages;

    @SerializedName("consultant")
    private ArrayList<User> consultants;

    @SerializedName("user")
    private User user;

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(ArrayList<Product> productPrices) {
        this.productPrices = productPrices;
    }

    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public LabReport getLabReport() {
        return labReport;
    }

    public void setLabReport(LabReport labReport) {
        this.labReport = labReport;
    }

    public ArrayList<Message> getGroupMessages() {
        return groupMessages;
    }

    public void setGroupMessages(ArrayList<Message> groupMessages) {
        this.groupMessages = groupMessages;
    }

    public ArrayList<User> getConsultants() {
        return consultants;
    }

    public void setConsultants(ArrayList<User> consultants) {
        this.consultants = consultants;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
