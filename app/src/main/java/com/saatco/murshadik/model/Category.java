package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("item")
    private Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
