package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.model.City;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private final CountryAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;
    private ArrayList<City> categories;

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

    public CountryAdapter(ArrayList<City> list, Context context, CountryAdapter.OnSelectItemClickListener listener) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_layout, parent, false);
        return new CountryAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final CountryAdapter.ViewHolder holder, final int position) {

        final City city = categories.get(position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onCountrySelect(view,position,city);
            }
        });

            holder.ivChild.setVisibility(View.GONE);
            holder.ivCheck.setVisibility(View.GONE);

            holder.name.setText(city.getNameAr());

    }

    public void updateList(ArrayList<City> list){
        categories = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnSelectItemClickListener {
        void onCountrySelect(View view, int position, City city);
    }

}

