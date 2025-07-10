package com.saatco.murshadik.adapters.ConsultaionAppointments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemSelectCityBinding;
import com.saatco.murshadik.model.Item;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {

    private List<Item> skills;
    private final Context context;
    private final SkillAdapter.OnSkillClickListener onSkillClickListener;
    public Item selectedItem;
    private int selectedPosition = -1;

    public SkillAdapter(List<Item> skills, Context context, Item selectedItem, SkillAdapter.OnSkillClickListener onSkillClickListener) {
        this.skills = skills;
        this.context = context;
        this.onSkillClickListener = onSkillClickListener;
        this.selectedItem = selectedItem;
    }

    @NonNull
    @Override
    public SkillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSelectCityBinding binding = ItemSelectCityBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SkillAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillAdapter.ViewHolder holder, int position) {
        int pos = holder.getAbsoluteAdapterPosition();
        holder.binding.tvCityName.setText(skills.get(pos).getNameAr());
        holder.binding.getRoot().setOnClickListener(v -> {
            onSkillClickListener.onSkillClick(skills.get(pos), pos);
            selectedItem = skills.get(pos);
            notifyItemChanged(pos);
            if (selectedPosition != -1) {
                notifyItemChanged(selectedPosition);
            }
            selectedPosition = pos;
        });

        if (selectedItem == null) {
            holder.binding.tvCityName.setTextAppearance(R.style.unselectedText);
            holder.binding.vCheck.setBackgroundTintList(context.getColorStateList(R.color.gray));
        } else if (selectedItem.equals(skills.get(pos))) {
            holder.binding.tvCityName.setTextAppearance(R.style.selectedText);
            holder.binding.vCheck.setBackgroundTintList(context.getColorStateList(R.color.colorPrimary));
        } else {
            holder.binding.tvCityName.setTextAppearance(R.style.unselectedText);
            holder.binding.vCheck.setBackgroundTintList(context.getColorStateList(R.color.gray));
        }
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

    public List<Item> getItems() {
        return skills;
    }

    public void setItems(List<Item> items) {
        this.skills = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemSelectCityBinding binding;

        public ViewHolder(@NonNull ItemSelectCityBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

        }

    }

    public interface OnSkillClickListener {
        void onSkillClick(Item skill, int position);
    }
}