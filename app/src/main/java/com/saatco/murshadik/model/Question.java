package com.saatco.murshadik.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Question implements Serializable , Comparable<Question>{

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("date")
    private String date;

    @SerializedName("keywords")
    private String keywords;

    @SerializedName("keyword1")
    private String keyword;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("is_verified")
    private boolean isVerified;

    @SerializedName("is_approved")
    private boolean isApproved;

    @SerializedName("verified_by")
    private String verifiedBy;

    @SerializedName("approved_by")
    private String approvedBy;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("description")
    private String description;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("attachments")
    private ArrayList<Attachment> attachments;

    @SerializedName("answers")
    private ArrayList<Question> answers;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("qa_category_id")
    private int categoryId;

    @SerializedName("name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);

        try {
            date  = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKeywords() {
        StringBuilder tags = new StringBuilder();
        String[] tagsList = keywords.trim().split(",");
        for(String tag : tagsList)
            tags.append("#").append(tag).append(" ");

        return tags.toString();
    }

    public List<String> getTagList() {
        List<String> taglist = new ArrayList<>();

        StringBuilder tags = new StringBuilder();
        String[] tagsList = keywords.trim().split(",");
        for(String tag : tagsList) {
            tags.append("#").append(tag).append(" ");
            taglist.add("#"+tag);
        }

        return taglist;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getVoteCountString() {
        return String.valueOf(voteCount);
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ArrayList<Question> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Question> answers) {
        this.answers = answers;
    }

    @Override
    public int compareTo(Question question) {
        return Integer.compare(this.getVoteCount(),question.getVoteCount());
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
