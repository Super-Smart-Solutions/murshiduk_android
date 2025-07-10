package com.saatco.murshadik.adapters.ConsultaionAppointments;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.databinding.ItemPastAppointmentBinding;
import com.saatco.murshadik.model.ConsultationAppointments.ConsultationAppointment;

import java.util.List;

public class PastAppointmentAdapter extends RecyclerView.Adapter<PastAppointmentAdapter.ViewHolder> {

    private final List<ConsultationAppointment> appointments;
    private final Boolean isConsultant;

    public PastAppointmentAdapter(List<ConsultationAppointment> appointments, Boolean isConsultant) {
        this.appointments = appointments;
        this.isConsultant = isConsultant;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPastAppointmentBinding binding = ItemPastAppointmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsultationAppointment appointment = appointments.get(position);
        if (isConsultant) {
            holder.binding.tvAppointmentUserName.setText(appointment.getFarmerName());
        } else {
            holder.binding.tvAppointmentUserName.setText(appointment.getConsultantName());
        }
        holder.binding.tvAppointmentCityName.setText(appointment.getAppointmentCity());
        holder.binding.tvAppointmentSkillName.setText(appointment.getAppointmentSkill());
        holder.binding.tvAppointmentDate.setText(appointment.getAppointmentDate());
        holder.binding.tvAppointmentTime.setText(appointment.getAppointmentTime());
        holder.binding.tvAppointmentDuration.setText(appointment.getCallDurationString());


    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemPastAppointmentBinding binding;

        public ViewHolder(ItemPastAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
