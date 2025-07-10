package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.MesiboTokenObject;
import com.saatco.murshadik.model.User;

public class UserResponse extends BaseResponse {

    @SerializedName("data")
    private  User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
