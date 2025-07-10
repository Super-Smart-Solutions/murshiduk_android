package com.saatco.murshadik.model.clinicService;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClinicAppointmentTime implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("AppointmentTime")
    private String time;

    public int getId() {
        return id;
    }

    public String getTime() {
        return time.substring(0, 5);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ClinicAppointmentTime && ((ClinicAppointmentTime) obj).getTime().equals(this.getTime());
    }


}
