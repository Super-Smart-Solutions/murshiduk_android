package com.saatco.murshadik.adapters.DesignV3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ItemCarouselBinding;
import com.saatco.murshadik.databinding.ItemClinicBinding;

import java.util.ArrayList;

public class InfinityImageIndecatorAdapter extends  RecyclerView.Adapter<InfinityImageIndecatorAdapter.ViewHolder> {

    Context context;
    ArrayList<String> imagesUrl;
    OnClickListener onClickListener;

    public InfinityImageIndecatorAdapter(Context context, ArrayList<String> imagesUrl, OnClickListener onClickListener) {
        this.context = context;
        this.imagesUrl = imagesUrl;
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarouselBinding binding = ItemCarouselBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imgUrl = imagesUrl.get(position);

        Glide.with(context).load(imgUrl).placeholder(R.drawable.no_image).into(holder.binding.carouselImageView);
        holder.binding.carouselImageView.setOnClickListener(v -> {
            onClickListener.onClick(imgUrl);
        });
    }



    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }


 
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemCarouselBinding binding;

        public ViewHolder(@NonNull ItemCarouselBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

    }

    public interface OnClickListener {
        void onClick(String imageUrl);
    }
}
