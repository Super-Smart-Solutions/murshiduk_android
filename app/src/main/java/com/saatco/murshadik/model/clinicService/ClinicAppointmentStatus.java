package com.saatco.murshadik.model.clinicService;

public enum ClinicAppointmentStatus {
    AVAILABLE(0),
    PENDING(1),
    DONE(2),
    CANCELED(3);

    private final int status;

    ClinicAppointmentStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}