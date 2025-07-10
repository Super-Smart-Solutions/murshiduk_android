package com.saatco.murshadik.model.ConsultationAppointments;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Locale;

public class ConsultationAppointment implements Serializable {

    /**
     *  "id": 38,
     *  "consultantName": "نواف الدعم الفني",
     *  "skillName": "البن",
     *  "appointmentDate": "2024-11-10T00:00:00",
     *  "appointmentTime": "09:00:00",
     *  "bookingReason": "test",
     *  "farmerName": "نواف الدعم الفني",
     *  "createdAt": "0001-01-01T00:00:00",
     *  "consultantChatId": "126342812",
     *  "farmerChatId": "126342812",
     *  "isTimePassed": false,
     *  "isCanceled": false,
     *  "callDuration": 0,
     *  "userCallStart": null,
     *  "userCancel": null,
     *  "cancelReason": null
     * */

    @SerializedName("id")
    private int id;

    @SerializedName("consultantName")
    private String consultantName;

    @SerializedName("skillName")
    private String skillName;

    @SerializedName("appointmentDate")
    private String appointmentDate;

    @SerializedName("appointmentTime")
    private String appointmentTime;

    @SerializedName("bookingReason")
    private String bookingReason;

    @SerializedName("farmerName")
    private String farmerName;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("consultantChatId")
    private String consultantChatId;

    @SerializedName("farmerChatId")
    private String farmerChatId;

    @SerializedName("isTimePassed")
    private boolean isTimePassed;

    @SerializedName("isCanceled")
    private boolean isCanceled;

    @SerializedName("callDuration")
    private int callDuration;

    @SerializedName("userCallStart")
    private String userCallStart;

    @SerializedName("userCancel")
    private String userCancel;

    @SerializedName("cancelReason")
    private String cancelReason;

    public int getId() {
        return id;
    }

    public String getConsultantName() {
        return consultantName;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getAppointmentDate() {
        return appointmentDate.substring(0, 10);
    }

    public String getAppointmentTime() {
        return appointmentTime.substring(0, 5);
    }

    public String getBookingReason() {
        return bookingReason;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getConsultantChatId() {
        return consultantChatId;
    }

    public String getFarmerChatId() {
        return farmerChatId;
    }

    public boolean isTimePassed() {
        return isTimePassed;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public int getCallDuration() {
        return callDuration;
    }

    public String getCallDurationString() {
        if (callDuration == 0) return "--:--";

        // convert call duration to a string in the format "MM:SS"
        int minutes = (callDuration % 3600) / 60;
        int seconds = callDuration % 60;
        return String.format(new Locale("en"),"%02d:%02d", minutes, seconds);
    }

    public String getUserCallStart() {
        return userCallStart;
    }

    public String getUserCancel() {
        return userCancel;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public String getAppointmentCity() {
        return bookingReason.split("\n")[0];
    }

    public String getAppointmentSkill() {
        String[] split = bookingReason.split("\n");
        if (split.length < 2) return "";
        return split[1];
    }

    public String getAppointmentReason() {
        // return the rest of the string after the first two lines
        if (bookingReason.split("\n").length < 3) return "";
        return bookingReason.substring(bookingReason.split("\n")[0].length() + bookingReason.split("\n")[1].length() + 2);
    }

}
