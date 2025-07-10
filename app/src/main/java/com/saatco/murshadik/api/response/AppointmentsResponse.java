package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.Appointment;

import java.util.ArrayList;

public class AppointmentsResponse extends BaseResponse{

    @SerializedName("data")
    private ArrayList<Appointment> appointments;

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }
}
