package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

public class CalenderResponse extends BaseResponse {

    @SerializedName("data")
    private ArrayList<Item> items;

    @SerializedName("news")
    private ArrayList<Item> news;

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Item> getNews() {
        return news;
    }

    public void setNews(ArrayList<Item> news) {
        this.news = news;
    }
}
