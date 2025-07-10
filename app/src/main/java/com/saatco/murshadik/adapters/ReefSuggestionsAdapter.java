package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemReefSuggestionsBinding;
import com.saatco.murshadik.model.ReefComponents.ReefComponent;

import java.util.List;

public class ReefSuggestionsAdapter extends RecyclerView.Adapter<ReefSuggestionsAdapter.ViewHolder> {

    private final List<ReefComponent> reefComponents;
    private Context context;
    public ReefSuggestionsAdapter(Context context, List<ReefComponent> reefComponents) {
        this.reefComponents = reefComponents;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReefSuggestionsBinding binding = ItemReefSuggestionsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReefComponent reefComponent = reefComponents.get(position);
        holder.binding.tvReefSuggestion.setText(reefComponent.getName());
//        holder.iv_icon.setImageResource(reefComponent.getIcon());

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.reef_suggestion_animate_status_bar);
        holder.binding.viewStatusIndicator.startAnimation(animation);
        if (position == reefComponents.size() - 1){
            holder.binding.viewStatusIndicator.setVisibility(ViewGroup.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reefComponents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemReefSuggestionsBinding binding;

        public ViewHolder(@NonNull ItemReefSuggestionsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

}
