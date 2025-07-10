package com.saatco.murshadik.model;

public class CommentVideoClip {
    public String commenter_name, comment, date;

    public CommentVideoClip(String commenter_name, String comment, String date){
        this.comment = comment;
        this.commenter_name = commenter_name;
        this.date = date;
    }
}
