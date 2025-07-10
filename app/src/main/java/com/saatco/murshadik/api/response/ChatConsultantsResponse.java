package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Group;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;

public class ChatConsultantsResponse extends BaseResponse {

    @SerializedName("consultants")
    private ArrayList<User> consultants;

    @SerializedName("groups")
    private ArrayList<Group> groups;

    @SerializedName("categories")
    private ArrayList<Item> categories;

    public ArrayList<User> getConsultants() {
        return consultants;
    }

    public void setConsultants(ArrayList<User> consultants) {
        this.consultants = consultants;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public ArrayList<Item> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Item> categories) {
        this.categories = categories;
    }
}
