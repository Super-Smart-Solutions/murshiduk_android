package com.saatco.murshadik.model.consultantvideos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewAPIsResponse<T> implements Serializable {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    @SerializedName("page_info")
    private String page_info;

    @SerializedName("info")
    private Object info;


    public boolean getStatus() {
        return status;
    }


    public String getMessage() {
        return message;
    }

    /**
     * @return: data
     * */
    public T getData() {
        return data;
    }


    public String getPageInfo() {
        return page_info;
    }

    public Object getInfo() {
        return info;
    }
}
