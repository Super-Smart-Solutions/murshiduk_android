package com.saatco.murshadik.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saatco.murshadik.model.ConsultationAppointments.ConsultationAppointment;

public class ConsultationAppointmentViewModel extends ViewModel {
    private final MutableLiveData<ConsultationAppointment> nextAppointment = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isConsultant = new MutableLiveData<>();
    public void updateNextAppointment(ConsultationAppointment appointment) {
        nextAppointment.setValue(appointment);
    }
    public void updateIsConsultant(Boolean isConsultant) {
        this.isConsultant.setValue(isConsultant);
    }

    public MutableLiveData<Boolean> getIsConsultant() {
        return isConsultant;
    }

    public MutableLiveData<ConsultationAppointment> getNextAppointment() {
        return nextAppointment;
    }
}
