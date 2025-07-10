package com.saatco.murshadik.adapters.ConsultaionAppointments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemSelectCityBinding;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<String> cities;
    private final Context context;
    private final OnCityClickListener onCityClickListener;
    public String selectedItem = "";
    private int selectedPosition = -1;

    public CityAdapter(List<String> cities, Context context, String selectedItem, OnCityClickListener onCityClickListener) {
        this.cities = cities;
        this.context = context;
        this.onCityClickListener = onCityClickListener;
        this.selectedItem = selectedItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSelectCityBinding binding = ItemSelectCityBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = holder.getAbsoluteAdapterPosition();
        holder.binding.tvCityName.setText(cities.get(pos));
        holder.binding.getRoot().setOnClickListener(v -> {
            onCityClickListener.onCityClick(cities.get(pos), pos);
            selectedItem = cities.get(pos);
            notifyItemChanged(pos);
            if (selectedPosition != -1) {
                notifyItemChanged(selectedPosition);
            }
            selectedPosition = pos;
        });

        if (selectedItem.equals(cities.get(pos))) {
            holder.binding.tvCityName.setTextAppearance(R.style.selectedText);
            holder.binding.vCheck.setBackgroundTintList(context.getColorStateList(R.color.colorPrimary));
        } else {
            holder.binding.tvCityName.setTextAppearance(R.style.unselectedText);
            holder.binding.vCheck.setBackgroundTintList(context.getColorStateList(R.color.gray));
        }
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(String selectedItem) {
        this.selectedItem = selectedItem;
    }

    public List<String> getItems() {
        return cities;
    }

    public void setItems(List<String> items) {
        this.cities = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemSelectCityBinding binding;

        public ViewHolder(@NonNull ItemSelectCityBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

        }

    }

    public interface OnCityClickListener {
        void onCityClick(String city,int position);
    }
}
