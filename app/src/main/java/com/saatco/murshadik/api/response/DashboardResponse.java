package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.ContactUs;
import com.saatco.murshadik.model.Section;
import com.saatco.murshadik.model.SectionBanner;
import com.saatco.murshadik.model.Stats;

import java.util.ArrayList;

public class DashboardResponse {

    @SerializedName("liveTransmission")
    private SectionBanner liveTransmission;

    @SerializedName("sectionOne")
    private Section sectionOne;

    @SerializedName("sectionTwo")
   private Section sectionTwo;

    @SerializedName("sectionThree")
    private Section sectionThree;

    @SerializedName("sectionFour")
    private Section sectionFour;

    @SerializedName("stats")
    private ArrayList<Stats> stats;

    @SerializedName("contactUsSection")
    private ContactUs contactUs;

    private Section adB;

    private int agCalendarCatID;
    private int agGuideCatID;
    private int mediaLibraryCatID;

    public SectionBanner getLiveTransmission() {
        return liveTransmission;
    }

    public void setLiveTransmission(SectionBanner liveTransmission) {
        this.liveTransmission = liveTransmission;
    }

    public Section getSectionOne() {
        return sectionOne;
    }

    public void setSectionOne(Section sectionOne) {
        this.sectionOne = sectionOne;
    }

    public Section getSectionTwo() {
        return sectionTwo;
    }

    public void setSectionTwo(Section sectionTwo) {
        this.sectionTwo = sectionTwo;
    }

    public Section getSectionThree() {
        return sectionThree;
    }

    public void setSectionThree(Section sectionThree) {
        this.sectionThree = sectionThree;
    }

    public Section getSectionFour() {
        return sectionFour;
    }

    public void setSectionFour(Section sectionFour) {
        this.sectionFour = sectionFour;
    }

    public ArrayList<Stats> getStats() {
        return stats;
    }

    public void setStats(ArrayList<Stats> stats) {
        this.stats = stats;
    }

    public int getAgCalendarCatID() {
        return agCalendarCatID;
    }

    public void setAgCalendarCatID(int agCalendarCatID) {
        this.agCalendarCatID = agCalendarCatID;
    }

    public int getAgGuideCatID() {
        return agGuideCatID;
    }

    public void setAgGuideCatID(int agGuideCatID) {
        this.agGuideCatID = agGuideCatID;
    }

    public int getMediaLibraryCatID() {
        return mediaLibraryCatID;
    }

    public void setMediaLibraryCatID(int mediaLibraryCatID) {
        this.mediaLibraryCatID = mediaLibraryCatID;
    }

    public ContactUs getContactUs() {
        return contactUs;
    }

    public void setContactUs(ContactUs contactUs) {
        this.contactUs = contactUs;
    }

    public Section getAdB() {
        return adB;
    }

    public void setAdB(Section adB) {
        this.adB = adB;
    }
}
