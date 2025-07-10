package com.saatco.murshadik.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private final SearchAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvTitle,tvDesc;
        public LinearLayout layoutBG;
        public ImageView bg;

        public ViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_package);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            layoutBG = view.findViewById(R.id.layoutBG);
            bg = view.findViewById(R.id.ivBG);
        }
    }

    public SearchAdapter(ArrayList<Item> list, Context context,SearchAdapter.OnSelectItemClickListener listener) {
        items = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final SearchAdapter.ViewHolder holder, final int position) {

        final Item item = items.get(position);

        holder.tvTitle.setText(item.getTitle_ar());
        holder.tvDesc.setText(item.getCategoryName());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String banner = APIClient.imageUrl + item.getArticleImage();
        RequestOptions requestOptions = new RequestOptions().override(100, 100);

        Glide.with(context)
                .asBitmap()
                .load(banner)
                .apply(requestOptions)
                .placeholder(circularProgressDrawable)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //  ivBanner3.setImageBitmap(resource);
                        holder.bg.setImageBitmap(resource);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onGroupClick(view,position,item);
            }
        });

    }

    public void updateList(ArrayList<Item> list){
        items = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnSelectItemClickListener {
        void onGroupClick(View view, int position, Item item);
    }

}

