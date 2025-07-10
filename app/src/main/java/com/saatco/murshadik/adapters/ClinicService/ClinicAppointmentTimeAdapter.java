package com.saatco.murshadik.adapters.ClinicService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemTimeSlotBinding;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class ClinicAppointmentTimeAdapter extends RecyclerView.Adapter<ClinicAppointmentTimeAdapter.ViewHolder> {

    ArrayList<ClinicAppointmentTime> clinicAppointmentTimes;
    Context context;
    OnClickListener onClickListener;
    OnDataChangeListener onDataChangeListener;

    int selectedPosition = -1;

    public ClinicAppointmentTimeAdapter(ArrayList<ClinicAppointmentTime> clinicAppointmentTimes, Context context,
                                        OnClickListener onClickListener, OnDataChangeListener onDataChangeListener) {
        this.clinicAppointmentTimes = clinicAppointmentTimes;

        this.context = context;
        this.onClickListener = onClickListener;
        this.onDataChangeListener = onDataChangeListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTimeSlotBinding binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicAppointmentTimeAdapter.ViewHolder holder, int position) {
        ClinicAppointmentTime clinicAppointmentTimes = this.clinicAppointmentTimes.get(position);
        holder.binding.tvTime.setText(clinicAppointmentTimes.getTime());
        if (selectedPosition == position) {
            holder.binding.cardView.setCardBackgroundColor(context.getColor(R.color.greenColor));
        } else {
            holder.binding.cardView.setCardBackgroundColor(context.getColor(R.color.white));
        }

        holder.binding.cardView.setOnClickListener(v -> {
            onClickListener.onTimeClick(clinicAppointmentTimes, position);
            setSelectedPosition(position);
        });
    }

    @Override
    public int getItemCount() {
        return clinicAppointmentTimes.size();
    }

    public void setSelectedPosition(int selectedPosition) {
        int temp = this.selectedPosition;
        this.selectedPosition = selectedPosition;
        notifyItemChanged(selectedPosition);
        notifyItemChanged(temp);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(ArrayList<ClinicAppointmentTime> clinicAppointmentTimes, String serverTime, String selectedDate) {
        removeDuplicates(clinicAppointmentTimes);
        if (selectedDate.compareTo(serverTime.substring(0, 10)) == 0)
            removeItemsBeforeServerTime(serverTime);

        sortItems();
        notifyDataSetChanged();
        this.onDataChangeListener.onTimeDataChanged(clinicAppointmentTimes);
        this.selectedPosition = -1;
    }

    private void removeDuplicates(ArrayList<ClinicAppointmentTime> clinicAppointmentTimes) {
        ArrayList<ClinicAppointmentTime> times = new ArrayList<>();
        for (ClinicAppointmentTime clinicAppointmentTime : clinicAppointmentTimes) {
            if (!times.contains(clinicAppointmentTime)) {
                times.add(clinicAppointmentTime);
            }
        }
        this.clinicAppointmentTimes = times;
    }


    private void sortItems() {
        clinicAppointmentTimes.sort(Comparator.comparing(ClinicAppointmentTime::getTime));
    }

    private void removeItemsBeforeServerTime(String serverTime) {

        String currentTime = getTimeOnly(serverTime);
        ArrayList<ClinicAppointmentTime> times = new ArrayList<>();
        for (ClinicAppointmentTime clinicAppointmentTime : clinicAppointmentTimes) {
            if (clinicAppointmentTime.getTime().compareTo(currentTime) > 0) {
                times.add(clinicAppointmentTime);
            }
        }
        this.clinicAppointmentTimes = times;
    }

    private String getTimeOnly(String serverTime) {
        return serverTime.substring(11, 16);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTimeSlotBinding binding;

        public ViewHolder(@NonNull ItemTimeSlotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onTimeClick(ClinicAppointmentTime clinicAppointmentTime, int position);
    }

    public interface OnDataChangeListener {
        void onTimeDataChanged(ArrayList<ClinicAppointmentTime> clinicAppointmentTimes);
    }

}
