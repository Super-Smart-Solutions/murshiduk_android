package com.saatco.murshadik.adapters.ConsultaionAppointments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.databinding.ItemConsultantCardBinding;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.DialogUtil;

import java.util.List;

public class ConsultantAdapter extends RecyclerView.Adapter<ConsultantAdapter.ViewHolder> {

    private List<User> consultants;
    private final Context context;
    private final OnConsultantClickListener onConsultantClickListener;

    public ConsultantAdapter(List<User> consultants, Context context, OnConsultantClickListener onSkillClickListener) {
        this.consultants = consultants;
        this.context = context;
        this.onConsultantClickListener = onSkillClickListener;
    }

    @NonNull
    @Override
    public ConsultantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConsultantCardBinding binding = ItemConsultantCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ConsultantAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultantAdapter.ViewHolder holder, int position) {
        int pos = holder.getAbsoluteAdapterPosition();
        holder.binding.tvConsultantName.setText(consultants.get(pos).getName());
        holder.binding.tvConsultantRegion.setText(consultants.get(pos).getLocation());
        holder.binding.tvConsultantSkills.setText(consultants.get(pos).getStatus());

        holder.binding.tvRating.setText(consultants.get(pos).getUserRatingStr());
        Glide.with(context)
                .load(APIClient.baseUrl + consultants.get(pos).getPhotoUrl())
                .placeholder(R.drawable.logo)
                .into(holder.binding.ivConsultantImage);

        holder.binding.btnShowProfile.setOnClickListener(v -> {
            DialogUtil.showInfoDialog(context, 2, context.getString(R.string.profile), consultants.get(pos).getProfile());
        });

        holder.binding.getRoot().setOnClickListener(v -> {
            onConsultantClickListener.onConsultantClick(consultants.get(pos), pos);
        });
    }

    @Override
    public int getItemCount() {
        return consultants.size();
    }


    public List<User> getItems() {
        return consultants;
    }

    public void setConsultants(List<User> consultants) {
        this.consultants = consultants;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemConsultantCardBinding binding;

        public ViewHolder(@NonNull ItemConsultantCardBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

        }

    }

    public interface OnConsultantClickListener {
        void onConsultantClick(User consultant, int position);
    }
}