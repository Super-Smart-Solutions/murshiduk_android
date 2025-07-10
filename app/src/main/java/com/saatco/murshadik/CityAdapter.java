package com.saatco.murshadik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private final OnSelectItemClickListener onFavItemClickListener;
    Context context;
    private ArrayList<Item> categories;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public View layout;
        public ImageView ivChild,ivCheck;

        public ViewHolder(View view) {
            super(view);
            ivChild = view.findViewById(R.id.ivChild);
            ivCheck = view.findViewById(R.id.ivCheck);
            name = view.findViewById(R.id.dropdown);
            layout = view.findViewById(R.id.layout_city);
        }
    }

    public CityAdapter(ArrayList<Item> list, Context context, OnSelectItemClickListener listener) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_layout, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Item item = categories.get(position);

        holder.name.setText(item.getNameAr());

        holder.layout.setOnClickListener(view -> onFavItemClickListener.onCitySelect(view,position,item));

        if(item.isChild()){
            holder.ivChild.setVisibility(View.VISIBLE);
        }else{
            holder.ivChild.setVisibility(View.GONE);
        }

        if(item.isSelected()) {
            holder.ivCheck.setImageResource(R.drawable.checkbox_selected);
            holder.ivCheck.setVisibility(View.VISIBLE);
        }else{
            holder.ivCheck.setImageResource(R.drawable.checkbox_empty);
        }

    }

    public void updateList(ArrayList<Item> list){
        categories = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnSelectItemClickListener {
        void onCitySelect(View view, int position, Item city);
    }

}
