package com.saatco.murshadik.dialogs;

public class FilterDialogModel {
    private int id;
    private String text;

    public FilterDialogModel(int id, String text){
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



}
