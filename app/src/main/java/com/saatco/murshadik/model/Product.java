package com.saatco.murshadik.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class Product implements Serializable , Comparable<Product>{

    @SerializedName("id")
    private int id;

    @SerializedName("product_name")
    private String name;

    @SerializedName("unit_value")
    private String unitValue;

    @SerializedName("unit")
    private String unit;

    @SerializedName("type")
    private String type;

    @SerializedName("price")
    private String price;

    @SerializedName("price_date")
    private String priceDate;

    @SerializedName("category")
    private String category;

    @SerializedName("image")
    private String image;

    @SerializedName("market_id")
    private int marketId;

    @SerializedName("marketname")
    private String marketName;

    @SerializedName("product_price")
    private ArrayList<Product> priceList;

    @SerializedName("product_origin")
    private String productOrigin;


    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public double getConvertedPrice() {
        return (double)Double.parseDouble(price);
    }


    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

   /* @Override
    public int compareTo(Product product) {
        return  Integer.compare(this.getConvertedPrice(), product.getConvertedPrice());
       // return compare(Integer.parseInt(this.getPrice()),Integer.parseInt(product.getPrice()));
    }*/

    public String getPriceDate() {
        return priceDate;
    }

    public String getFormattedDate(){

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String date = null;
        try {
            date  = myFormat.format(fromUser.parse(priceDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public int compareTo(@NonNull Product product) {
        return Double.compare(this.getConvertedPrice(),product.getConvertedPrice());
    }

    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
    }

    public ArrayList<Product> getPriceList() {
        return priceList;
    }

    public void setPriceList(ArrayList<Product> priceList) {
        this.priceList = priceList;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    public String getProductOrigin() {
        return productOrigin;
    }

    public void setProductOrigin(String productOrigin) {
        this.productOrigin = productOrigin;
    }
}
