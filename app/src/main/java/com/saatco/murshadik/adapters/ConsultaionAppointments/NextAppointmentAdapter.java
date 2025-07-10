package com.saatco.murshadik.adapters.ConsultaionAppointments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.databinding.ItemNextAppointmentBinding;
import com.saatco.murshadik.model.ConsultationAppointments.ConsultationAppointment;

import java.util.ArrayList;

public class NextAppointmentAdapter extends RecyclerView.Adapter<NextAppointmentAdapter.ViewHolder> {

    private ArrayList<ConsultationAppointment> appointments;
    private final Context context;
    private final OnAppointmentClickListener onAppointmentClickListener;
    private final Boolean isConsultant;

    public NextAppointmentAdapter(ArrayList<ConsultationAppointment> appointments, Context context, Boolean isConsultant, OnAppointmentClickListener onAppointmentClickListener) {
        this.appointments = appointments;
        this.context = context;
        this.onAppointmentClickListener = onAppointmentClickListener;
        this.isConsultant = isConsultant;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNextAppointmentBinding binding = ItemNextAppointmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        holder.binding.ibCancelAppointment.setOnClickListener(v -> {
            onAppointmentClickListener.onAppointmentCancel(appointment);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemNextAppointmentBinding binding;

        public ViewHolder(ItemNextAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnAppointmentClickListener {
        void onAppointmentCancel(ConsultationAppointment appointment);
    }
}
