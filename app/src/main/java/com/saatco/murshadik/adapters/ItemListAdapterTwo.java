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
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.LanguageUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class ItemListAdapterTwo extends RecyclerView.Adapter<ItemListAdapterTwo.ViewHolder> {

    private List<Item> categories = new ArrayList<>();
    private final ItemListAdapterTwo.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public View layout;
        public TextView tvTitle,tvDesc,tvDate;
        public LinearLayout layoutBG;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivIcon);
            tvTitle = view.findViewById(R.id.tvTitle);
            layoutBG = view.findViewById(R.id.layoutBG);
        }
    }

    public ItemListAdapterTwo(List<Item> list, Context context, ItemListAdapterTwo.OnSelectItemClickListener listener) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public ItemListAdapterTwo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_2, parent, false);
        return new ItemListAdapterTwo.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ItemListAdapterTwo.ViewHolder holder, final int position) {

        final Item item = categories.get(position);

        holder.tvTitle.setText(item.getNameAr());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String image =  APIClient.imageUrl + item.getBannerAr();
        Glide.with(context)
                .asBitmap()
                .load(image)
                .placeholder(circularProgressDrawable)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //  ivBanner3.setImageBitmap(resource);

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        final float roundPx = (float) resource.getWidth() * 0.04f;
                        roundedBitmapDrawable.setCornerRadius(roundPx);

                        holder.imageView.setImageDrawable(roundedBitmapDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onItem2Click(view, position, item);
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
        void onItem2Click(View view, int position, Item item);
    }

}

