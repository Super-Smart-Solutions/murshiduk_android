package com.saatco.murshadik.adapters.ClinicService;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemClinicAppointmentBinding;
import com.saatco.murshadik.model.clinicService.ClinicAppointment;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentStatus;
import com.saatco.murshadik.utils.Util;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ClinicAppointmentsAdapter extends RecyclerView.Adapter<ClinicAppointmentsAdapter.ViewHolder>{

    ArrayList<ClinicAppointment> clinicAppointments;
    String dateTimeOfServer;
    Context context;

    OnClickListener listener;

    public ClinicAppointmentsAdapter(ArrayList<ClinicAppointment> clinicAppointments, String dateTimeOfServer, Context context, OnClickListener listener) {
        updateList(clinicAppointments);
        this.context = context;
        this.listener = listener;
        this.dateTimeOfServer = dateTimeOfServer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClinicAppointmentBinding binding = ItemClinicAppointmentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicAppointmentsAdapter.ViewHolder holder, int position) {

        ClinicAppointment clinicAppointment = clinicAppointments.get(position);
        boolean isUpcoming = Util.getClinicAppointmentStatus(clinicAppointment.getDateTime(), dateTimeOfServer) == ClinicAppointmentStatus.AVAILABLE;

        holder.binding.appointmentStatusTv.setText(isUpcoming ? context.getText(R.string.available) : context.getText(R.string.waiting));
        holder.binding.appointmentStatusTv.setBackgroundTintList(
                ContextCompat.getColorStateList(
                        context, isUpcoming ? R.color.clinic_available : R.color.clinic_pending
                ));

        if (clinicAppointment.getCallDuration() > 0){
            String strCallDuration = (clinicAppointment.getCallDuration() / 60) + ":" + (clinicAppointment.getCallDuration() % 60);
            holder.binding.appointmentDurationTv.setVisibility( ViewGroup.VISIBLE);
            holder.binding.appointmentDurationTv.setText(context.getString(R.string.call_duration, strCallDuration));
        }

        holder.binding.doctorNameTv.setText(context.getString(R.string.doctor, clinicAppointment.getDocName()));
        holder.binding.clinicNameTv.setText(context.getString(R.string.clinic, clinicAppointment.getClinicName()));

        holder.binding.dateTvwi.setText(clinicAppointment.getDateTime().split("T")[0]);
        holder.binding.timeTvwi.setText(clinicAppointment.getDateTime().split("T")[1].substring(0, 5));

        holder.binding.callActionButtonsLl.setVisibility(isUpcoming ? ViewGroup.VISIBLE : ViewGroup.GONE);
        holder.binding.voiceCallLl.setOnClickListener(v -> listener.onVoiceClick(clinicAppointment));
        holder.binding.videoCallLl.setOnClickListener(v -> listener.onVideoClick(clinicAppointment));

        holder.binding.cancelAppointmentBtn.setOnClickListener(v -> listener.onCancelClick(clinicAppointment));
        holder.binding.cancelAppointmentBtn.setVisibility(isUpcoming ? ViewGroup.GONE : ViewGroup.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return clinicAppointments.size();
    }

    public void updateList(ArrayList<ClinicAppointment> clinicAppointments) {

        this.clinicAppointments = clinicAppointments.stream().sorted((o1, o2) -> {
            DateTime dt1 = DateTime.parse(o1.getDateTime());
            DateTime dt2 = DateTime.parse(o2.getDateTime());
            return dt1.compareTo(dt2);
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        notifyDataSetChanged();
    }

    public void updateDateTimeOfServer(String dateTimeOfServer) {
        this.dateTimeOfServer = dateTimeOfServer;
        notifyItemChanged(0);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemClinicAppointmentBinding binding;
        public ViewHolder(@NonNull ItemClinicAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onVoiceClick(ClinicAppointment clinicAppointment);
        void onVideoClick(ClinicAppointment clinicAppointment);
        void onCancelClick(ClinicAppointment clinicAppointment);
    }
}
