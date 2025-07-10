package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Section;
import com.saatco.murshadik.model.SectionBanner;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;

public class DashboardResult {

    @SerializedName("firstSection")
    private Section firstSection;

    @SerializedName("secondSection")
    private Section secondSection;

    @SerializedName("fourthSection")
    private Section fourthSection;

    @SerializedName("fifthSection")
    private Section fifthSection;

    @SerializedName("sixthSection")
    private Section sixthSection;

    @SerializedName("thirdSection")
    private SectionBanner thirdSection;

    @SerializedName("seventhSection")
    private SectionBanner seventhSection;

    @SerializedName("eightSection")
    private Section eighthSection;

    @SerializedName("ninthSection")
    private Section ninthSection;

    @SerializedName("tenthSection")
    private SectionBanner tenthsection;

    @SerializedName("regions")
    private Section regions;

    @SerializedName("skills")
    private Section skills;

    @SerializedName("groups")
    private Section groups;

    @SerializedName("categories")
    private ArrayList<Item> categories;

    @SerializedName("consultants")
    private ArrayList<User> consultants;

    @SerializedName("farmers")
    private ArrayList<User> farmers;

    @SerializedName("me")
    private User user;

    public void setRegions(Section regions) {
        this.regions = regions;
    }

    public Section getSkills() {
        return skills;
    }

    public void setSkills(Section skills) {
        this.skills = skills;
    }

    public Section getGroups() {
        return groups;
    }

    public void setGroups(Section groups) {
        this.groups = groups;
    }

    public ArrayList<User> getFarmers() {
        return farmers;
    }

    public void setFarmers(ArrayList<User> farmers) {
        this.farmers = farmers;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Section getFirstSection() {
        return firstSection;
    }

    public void setFirstSection(Section firstSection) {
        this.firstSection = firstSection;
    }

    public Section getSecondSection() {
        return secondSection;
    }

    public void setSecondSection(Section secondSection) {
        this.secondSection = secondSection;
    }

    public Section getFifthSection() {
        return fifthSection;
    }

    public void setFifthSection(Section fifthSection) {
        this.fifthSection = fifthSection;
    }

    public Section getSixthSection() {
        return sixthSection;
    }

    public void setSixthSection(Section sixthSection) {
        this.sixthSection = sixthSection;
    }

    public SectionBanner getThirdSection() {
        return thirdSection;
    }

    public void setThirdSection(SectionBanner thirdSection) {
        this.thirdSection = thirdSection;
    }

    public SectionBanner getSeventhSection() {
        return seventhSection;
    }

    public void setSeventhSection(SectionBanner seventhSection) {
        this.seventhSection = seventhSection;
    }

    public Section getEighthSection() {
        return eighthSection;
    }

    public void setEighthSection(Section eighthSection) {
        this.eighthSection = eighthSection;
    }

    public SectionBanner getTenthsection() {
        return tenthsection;
    }

    public void setTenthsection(SectionBanner tenthsection) {
        this.tenthsection = tenthsection;
    }

    public ArrayList<Item> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Item> categories) {
        this.categories = categories;
    }

    public ArrayList<User> getConsultants() {
        return consultants;
    }

    public void setConsultants(ArrayList<User> consultants) {
        this.consultants = consultants;
    }

    public Section getFourthSection() {
        return fourthSection;
    }

    public void setFourthSection(Section fourthSection) {
        this.fourthSection = fourthSection;
    }

    public Section getRegions() {
        return regions;
    }

    public Section getNinthSection() {
        return ninthSection;
    }

    public void setNinthSection(Section ninthSection) {
        this.ninthSection = ninthSection;
    }
}
