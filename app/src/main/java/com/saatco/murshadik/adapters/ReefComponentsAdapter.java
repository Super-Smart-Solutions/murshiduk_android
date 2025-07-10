package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemReefComponentBinding;
import com.saatco.murshadik.model.ReefComponents.ReefComponent;

import java.util.List;

public class ReefComponentsAdapter extends RecyclerView.Adapter<ReefComponentsAdapter.ViewHolder> {

    private final List<ReefComponent> reefComponents;
    private Context context;
    private final ReefComponentsAdapterListener listener;

    public ReefComponentsAdapter(Context context, List<ReefComponent> reefComponents, ReefComponentsAdapterListener listener) {
        this.reefComponents = reefComponents;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReefComponentBinding binding = ItemReefComponentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReefComponent reefComponent = reefComponents.get(position);
        holder.binding.tvReefName.setText(reefComponent.getName());
//        holder.iv_icon.setImageResource(reefComponent.getIcon());
        holder.itemView.setOnClickListener(v -> listener.onReefComponentClicked(reefComponent));
        if (position % 2 == 0){
            holder.binding.ivBgReefComponent.setRotationY(180);
        }
    }

    @Override
    public int getItemCount() {
        return reefComponents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemReefComponentBinding binding;

        public ViewHolder(@NonNull ItemReefComponentBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface ReefComponentsAdapterListener {
        void onReefComponentClicked(ReefComponent reefComponent);
    }
}
