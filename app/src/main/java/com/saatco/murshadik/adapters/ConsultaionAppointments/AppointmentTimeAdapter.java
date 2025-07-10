package com.saatco.murshadik.adapters.ConsultaionAppointments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemTimeSlotBinding;

import java.util.ArrayList;

public class AppointmentTimeAdapter extends RecyclerView.Adapter<AppointmentTimeAdapter.ViewHolder> {

    ArrayList<String> appointmentTimes;
    Context context;
    OnClickListener onClickListener;

    int selectedPosition = -1;

    public AppointmentTimeAdapter(ArrayList<String> appointmentTimes, Context context,
                                  OnClickListener onClickListener) {
        this.appointmentTimes = appointmentTimes;

        this.context = context;
        this.onClickListener = onClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTimeSlotBinding binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentTimeAdapter.ViewHolder holder, int position) {
        String appointmentTime = this.appointmentTimes.get(position);
        holder.binding.tvTime.setText(appointmentTime.substring(0, 5));
        if (selectedPosition == position) {
            holder.binding.cardView.setCardBackgroundColor(context.getColor(R.color.greenColor));
            holder.binding.tvTime.setTextColor(context.getColor(R.color.white));
        } else {
            holder.binding.cardView.setCardBackgroundColor(context.getColor(R.color.white));
            holder.binding.tvTime.setTextColor(context.getColor(R.color.black));
        }

        holder.binding.cardView.setOnClickListener(v -> {
            onClickListener.onClick(appointmentTime, position);
            setSelectedPosition(position);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentTimes.size();
    }

    public void setSelectedPosition(int selectedPosition) {
        int temp = this.selectedPosition;
        this.selectedPosition = selectedPosition;
        notifyItemChanged(selectedPosition);
        notifyItemChanged(temp);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTimeSlotBinding binding;

        public ViewHolder(@NonNull ItemTimeSlotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onClick(String appointmentTime, int position);
    }


}
