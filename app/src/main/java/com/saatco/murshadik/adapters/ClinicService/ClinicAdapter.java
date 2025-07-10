package com.saatco.murshadik.adapters.ClinicService;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.databinding.ItemClinicBinding;
import com.saatco.murshadik.model.clinicService.Clinic;


import java.util.ArrayList;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ViewHolder> {

    ArrayList<Clinic> clinic;
    Context context;
    OnClickListener onClickListener;

    int selectedPosition = -1;

    public ClinicAdapter(ArrayList<Clinic> clinic, Context context, OnClickListener onClickListener) {
        this.clinic = clinic;

        this.context = context;
        this.onClickListener = onClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClinicBinding binding = ItemClinicBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicAdapter.ViewHolder holder, int position) {
        Clinic clinic = this.clinic.get(position);
        Glide.with(context).load(APIClient.imageUrl + "/" + clinic.getImg()).placeholder(R.drawable.no_image).into(holder.binding.ivIcon);
        holder.binding.title.setText(clinic.getName());
        holder.binding.getRoot().setOnClickListener(v -> {
            setSelectedPosition(position);
            onClickListener.onClick(clinic);
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
        notifyItemChanged(selectedPosition);
        notifyItemChanged(temp);
    }

    @Override
    public int getItemCount() {
        return clinic.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemClinicBinding binding;

        public ViewHolder(@NonNull ItemClinicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onClick(Clinic clinic);
    }

}
