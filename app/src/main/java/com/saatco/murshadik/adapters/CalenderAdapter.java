package com.saatco.murshadik.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.LanguageUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.ViewHolder> {

    private List<Item> categories = new ArrayList<>();
    private final CalenderAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public View layout;
        public TextView tvTitle,tvDesc;
        public LinearLayout layoutBG;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivIcon);
            tvTitle = view.findViewById(R.id.tvTitle);
            layoutBG = view.findViewById(R.id.layoutBG);
            tvDesc = view.findViewById(R.id.tvDesc);
        }
    }

    public CalenderAdapter(List<Item> list, Context context, CalenderAdapter.OnSelectItemClickListener listener) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public CalenderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calender, parent, false);
        return new CalenderAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final CalenderAdapter.ViewHolder holder, final int position) {

        final Item item = categories.get(position);

        holder.tvTitle.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getNameAr() : item.getName());
        holder.tvDesc.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getDescriptionAr() : item.getDescription());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String image = APIClient.imageUrl + item.getIcon();

        Glide.with(context).load(image).placeholder(circularProgressDrawable).into(holder.imageView);
        holder.tvDesc.setTextColor(Color.parseColor(item.getTextColor()));
        holder.layoutBG.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(item.getBgColor().equals("#ffffff") ? "#defaea" : item.getBgColor())));

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onCalenderItemClick(view, position, item);
            }
        });

    }

    public void updateList(List<Item> list) {
        categories = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnSelectItemClickListener {
        void onCalenderItemClick(View view, int position, Item item);
    }

}
