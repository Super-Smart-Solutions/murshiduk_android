package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.api.ApiToken;

/**
 * Created by bassam
 */

public class BaseResponse {


    @SerializedName("message")
    protected String message;

    @SerializedName("api_token")
    protected ApiToken token;

    @SerializedName("status")
    protected boolean status;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public ApiToken getApiToken() {
        return token;
    }

    public void setToken(ApiToken token) {
        this.token = token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "message='" + message + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
