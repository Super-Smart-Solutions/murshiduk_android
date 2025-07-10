package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Question;

import java.util.ArrayList;

public class QuestionResponse extends BaseResponse {

    @SerializedName("data")
    private  ArrayList<Question> questions;

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
