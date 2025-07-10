package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Market;

import java.util.ArrayList;

public class MarketResponse {

    @SerializedName("data")
    private ArrayList<Market> markets;

    public ArrayList<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(ArrayList<Market> markets) {
        this.markets = markets;
    }
}
