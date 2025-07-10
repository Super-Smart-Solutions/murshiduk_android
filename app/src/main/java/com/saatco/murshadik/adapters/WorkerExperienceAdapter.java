package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemWorkerExperienceBinding;
import com.saatco.murshadik.model.workersService.Experience;

import java.util.ArrayList;

public class WorkerExperienceAdapter extends RecyclerView.Adapter<WorkerExperienceAdapter.ViewHolder> {

    ArrayList<Experience> list;
    Context context;
    WorkerExperienceAdapter.OnItemListener listener;

    public WorkerExperienceAdapter(Context context, ArrayList<Experience> list, WorkerExperienceAdapter.OnItemListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;

    }

    @NonNull
    @Override
    public WorkerExperienceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkerExperienceBinding binding = ItemWorkerExperienceBinding.inflate(LayoutInflater.from(context), parent, false);
        return new WorkerExperienceAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerExperienceAdapter.ViewHolder holder, int position) {
        Experience experience = list.get(position);
        holder.binding.tvText.setText(experience.getDescription());
        String date = experience.getFromDate() + " - " + experience.getToDate();
        holder.binding.tvDate.setText(date);
        holder.binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(experience, position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<Experience> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int removeItem(Experience experience) {
        int position = list.indexOf(experience);
        list.remove(position);
        notifyItemRemoved(position);
        return position;
    }

    public void addItemToFirst(Experience experience) {
        list.add(0, experience);
        notifyItemInserted(0);
    }

    public void addItemAt(int index, Experience experience) {
        list.add(index, experience);
        notifyItemInserted(index);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemWorkerExperienceBinding binding;

        public ViewHolder(ItemWorkerExperienceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemListener {
        void onDeleteClick(Experience experience, int position);
    }
}
