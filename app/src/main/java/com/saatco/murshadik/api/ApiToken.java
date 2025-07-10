package com.saatco.murshadik.api;

import com.google.gson.annotations.SerializedName;

public class ApiToken {

    @SerializedName("api_token")
    protected String apiToken;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
