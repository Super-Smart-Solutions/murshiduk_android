package com.saatco.murshadik.adapters.ClinicService;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemDoctorBinding;
import com.saatco.murshadik.model.clinicService.Doctor;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    ArrayList<Doctor> doctors;
    Context context;
    OnClickListener onClickListener;

    int selectedPosition = -1;

    public DoctorAdapter(ArrayList<Doctor> doctors, Context context, OnClickListener onClickListener) {
        this.doctors = doctors;

        this.context = context;
        this.onClickListener = onClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDoctorBinding binding = ItemDoctorBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAdapter.ViewHolder holder, int position) {
        Doctor doctor = this.doctors.get(position);
//        Glide.with(context).load(APIClient.imageUrl + "/" + doctor.getImg()).placeholder(R.drawable.no_image).into(holder.binding.ivIcon);
        holder.binding.title.setText(doctor.getName());
        holder.binding.getRoot().setOnClickListener(v -> {
            setSelectedPosition(position);
            onClickListener.onClick(doctor);
        });
        if (selectedPosition == position) {
            holder.binding.card.setCardBackgroundColor(context.getColor(R.color.item_clinic_bg_color_selected));
        } else {
            holder.binding.card.setCardBackgroundColor(context.getColor(R.color.white));
        }
    }

    public void setSelectedPosition(int selectedPosition) {
        int temp = this.selectedPosition;
        this.selectedPosition = selectedPosition;
        notifyItemChanged(temp);
        notifyItemChanged(selectedPosition);
    }


    @Override
    public int getItemCount() {
        return doctors.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDoctorBinding binding;

        public ViewHolder(@NonNull ItemDoctorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onClick(Doctor doctor);
    }

}
