package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemWorkerJobsBinding;
import com.saatco.murshadik.databinding.ItemWorkersBinding;
import com.saatco.murshadik.model.workersService.Job;
import com.saatco.murshadik.model.workersService.Worker;

import java.util.ArrayList;

public class WorkerJobsAdapter extends RecyclerView.Adapter<WorkerJobsAdapter.ViewHolder> {

    ArrayList<Job> list;
    Context context;
    WorkerJobsAdapter.OnItemListener listener;

    public WorkerJobsAdapter(Context context, ArrayList<Job> list, WorkerJobsAdapter.OnItemListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkerJobsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkerJobsBinding binding = ItemWorkerJobsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new WorkerJobsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerJobsAdapter.ViewHolder holder, int position) {
        Job job = list.get(position);
        holder.binding.tvText.setText(job.getName());
        if (job.isAdded()){
            holder.binding.btnAction.setImageResource(android.R.drawable.ic_delete);
            holder.binding.btnAction.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
            holder.binding.clLayoutBg.setCardBackgroundColor(context.getColor(R.color.light_green_navbar_bg));
        }else {
            holder.binding.btnAction.setImageResource(android.R.drawable.ic_input_add);
            holder.binding.btnAction.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.themeColor)));
            holder.binding.clLayoutBg.setCardBackgroundColor(context.getColor(R.color.white));
        }

        holder.binding.btnAction.setOnClickListener(v -> listener.onClick(job, !job.isAdded()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<Job> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int removeItem(Job job){
        int position = list.indexOf(job);
        list.remove(position);
        notifyItemRemoved(position);
        return position;
    }

    public void addItemToFirst(Job job){
        list.add(0, job);
        notifyItemInserted(0);
    }

    public void addItemAt(int index, Job job){
        list.add(index, job);
        notifyItemInserted(index);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemWorkerJobsBinding binding;
        public ViewHolder(ItemWorkerJobsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemListener{
        void onClick(Job job, boolean isAdding);
    }
}
