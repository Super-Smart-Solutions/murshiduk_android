package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.databinding.ItemWorkersBinding;
import com.saatco.murshadik.model.workersService.Worker;

import java.util.ArrayList;
import java.util.List;

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.ViewHolder> {

    ArrayList<Worker> list;
    Context context;
    OnItemListener listener;

    public WorkersAdapter(Context context, ArrayList<Worker> list, OnItemListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkersBinding binding = ItemWorkersBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Worker worker = list.get(position);
        String job = worker.getJob();
        holder.binding.tvWorkerName.setText(worker.getName());
        holder.binding.tvDotSkill.setText(
                job == null ?
                        !worker.getJobs().isEmpty() ?
                        worker.getJobs().get(0).getName() :
                        "":
                        job
        );
        holder.binding.tviRegion.setText(worker.getAddress().split("-")[0]);
        holder.binding.cflLayout.setOnClickListener(v -> listener.onClick(worker));

        RequestOptions requestOptions = new RequestOptions().override(200, 200).circleCrop();

        Glide.with(context)
                .load(APIClient.baseUrl + worker.getImgUrl())
                .apply(requestOptions)
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.logo))
                .into(holder.binding.imgWorker);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Worker> list) {
        this.list = (ArrayList<Worker>) list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemWorkersBinding binding;

        public ViewHolder(ItemWorkersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemListener {
        void onClick(Worker worker);
    }
}
