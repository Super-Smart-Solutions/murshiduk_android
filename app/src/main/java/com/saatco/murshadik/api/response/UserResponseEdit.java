package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.MesiboTokenObject;
import com.saatco.murshadik.model.User;

public class UserResponseEdit extends BaseResponse {

    @SerializedName("auth")
    private User user;

    @SerializedName("mesiboToken")
    private MesiboTokenObject mesiboTokenObject;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MesiboTokenObject getMesiboTokenObject() {
        return mesiboTokenObject;
    }

    public void setMesiboTokenObject(MesiboTokenObject mesiboTokenObject) {
        this.mesiboTokenObject = mesiboTokenObject;
    }
}
