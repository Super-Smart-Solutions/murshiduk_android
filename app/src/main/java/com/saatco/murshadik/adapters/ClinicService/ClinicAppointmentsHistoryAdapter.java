package com.saatco.murshadik.adapters.ClinicService;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemClinicAppointmentBinding;
import com.saatco.murshadik.databinding.ItemClinicAppointmentHistoryBinding;
import com.saatco.murshadik.model.clinicService.ClinicAppointment;
import com.saatco.murshadik.utils.Util;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ClinicAppointmentsHistoryAdapter extends RecyclerView.Adapter<ClinicAppointmentsHistoryAdapter.ViewHolder> {

    List<ClinicAppointment> clinicAppointments;
    Context context;


    public ClinicAppointmentsHistoryAdapter(List<ClinicAppointment> clinicAppointments, Context context) {
        this.clinicAppointments = clinicAppointments.stream().sorted((o1, o2) -> {
            DateTime dt1 = DateTime.parse(o1.getDateTime());
            DateTime dt2 = DateTime.parse(o2.getDateTime());
            return dt1.compareTo(dt2);
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        this.clinicAppointments = this.clinicAppointments.reversed();

        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClinicAppointmentHistoryBinding binding = ItemClinicAppointmentHistoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicAppointmentsHistoryAdapter.ViewHolder holder, int position) {
        ClinicAppointment clinicAppointment = clinicAppointments.get(position);

        if (clinicAppointment.getCallDuration() > 0) {
            holder.binding.appointmentStatusTv.setText(context.getText(R.string.call_done));
            holder.binding.appointmentStatusTv.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            context, R.color.clinic_completed
                    ));
        } else {
            holder.binding.appointmentStatusTv.setText(context.getText(R.string.call_not_done));
            holder.binding.appointmentStatusTv.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            context, R.color.clinic_not_completed
                    ));
        }

        holder.binding.doctorNameTv.setText(context.getString(R.string.doctor, clinicAppointment.getDocName()));
        holder.binding.clinicNameTv.setText(context.getString(R.string.clinic, clinicAppointment.getClinicName()));

        holder.binding.dateTvwi.setText(clinicAppointment.getDateTime().split("T")[0]);
        holder.binding.timeTvwi.setText(clinicAppointment.getDateTime().split("T")[1].substring(0, 5));

        String callDuration = (clinicAppointment.getCallDuration() / 60) + ":" + (clinicAppointment.getCallDuration() % 60);
        holder.binding.appointmentDurationTv.setText(context.getString(R.string.call_duration, callDuration));


    }

    @Override
    public int getItemCount() {
        return clinicAppointments.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemClinicAppointmentHistoryBinding binding;

        public ViewHolder(@NonNull ItemClinicAppointmentHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
