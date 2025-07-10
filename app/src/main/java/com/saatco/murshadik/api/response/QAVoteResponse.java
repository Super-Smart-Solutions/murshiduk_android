package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;

public class QAVoteResponse extends BaseResponse {

    @SerializedName("data")
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
