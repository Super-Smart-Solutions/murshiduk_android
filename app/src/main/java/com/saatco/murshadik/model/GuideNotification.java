package com.saatco.murshadik.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GuideNotification implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("consultantId")
    private String consultant_id;

    @SerializedName("guideAlert")
    private String guide_alert_text;

    @SerializedName("cities")
    private String cities;

    @SerializedName("skills")
    private String skills;

    @SerializedName("regonId")
    private int region_id;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("RecipientsCount")
    private String recipients_count;

    public int getId() {
        return id;
    }

    public String getConsultant_id() {
        return consultant_id;
    }

    public String getGuide_alert_text() {
        return guide_alert_text;
    }

    public String getCities() {
        return cities;
    }

    public String getSkills() {
        return skills;
    }

    public int getRegion_id() {
        return region_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getRecipients_count() {
        return recipients_count;
    }

    public Date getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        try {
            return format.parse(created_at);
        } catch (Exception e) {
            Log.v("error converting date: ", e.getMessage());
        }
        return null;
    }
}
