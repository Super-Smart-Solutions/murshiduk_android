package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Data;

public class LabReportResponse {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
