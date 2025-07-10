package com.saatco.murshadik.adapters.DesignV3;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.databinding.ItemNewsV3Binding;
import com.saatco.murshadik.model.Item;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DashboardNewsAdapter extends RecyclerView.Adapter<DashboardNewsAdapter.ViewHolder> {

    private final List<Item> newsList;
    private final Context context;
    private final OnClickListener onClickListener;
    private final Locale arabicLocale = new Locale("ar");
    private final Locale englishLocale = new Locale("en");

    public DashboardNewsAdapter(Context context, List<Item> newsList, OnClickListener onClickListener) {
        this.context = context;
        this.newsList = newsList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewsV3Binding binding = ItemNewsV3Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = newsList.get(position);
        holder.binding.tvNewsTitle.setText(item.getTitle_ar());

        String image = APIClient.imageUrl + item.getImage();
        Glide.with(context).load(image).placeholder(R.drawable.no_image).into(holder.binding.ivNewsImage);

        holder.binding.getRoot().setOnClickListener(v -> {
            onClickListener.onClick(position, item);
        });

        // Assuming 'item' is an object with a 'getDate()' method returning a Date object
        Date date = item.getDate();



        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", arabicLocale);
        String month = dateFormat.format(date);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy", englishLocale);

        String fullDate = dateFormat.format(date);

        String day = fullDate.split("-")[0];
        String year = fullDate.split("-")[2];

//        convert arabic number to english number


        holder.binding.tvDay.setText(day);
        holder.binding.tvMonth.setText(month);
        holder.binding.tvYear.setText(year);

        String[] colors = context.getResources().getStringArray(R.array.news_colors);
        int color = Color.parseColor(colors[item.getId() % colors.length]);
        int alphaColor = Color.argb(90, Color.red(color), Color.green(color), Color.blue(color));
        holder.binding.cvNews.setCardBackgroundColor(color);
        holder.binding.llNewsDate.setBackgroundColor(alphaColor);

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemNewsV3Binding binding;

        public ViewHolder(@NonNull ItemNewsV3Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onClick(int position, Item item);
    }
}
