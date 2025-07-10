package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Data;
import com.saatco.murshadik.model.Market;
import com.saatco.murshadik.model.Message;

import java.util.ArrayList;

public class GroupMessageResponse {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    /* @SerializedName("data")
    private ArrayList<Message> messages;


    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }*/


}
