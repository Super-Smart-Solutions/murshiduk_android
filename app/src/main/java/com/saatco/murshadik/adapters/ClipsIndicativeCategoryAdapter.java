package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.R;
import com.saatco.murshadik.model.consultantvideos.CategoryDataOfConsultantVideos;

import java.util.List;

public class ClipsIndicativeCategoryAdapter extends RecyclerView.Adapter<ClipsIndicativeCategoryAdapter.ViewHolder> {

    private final List<CategoryDataOfConsultantVideos> categoryDataList;
    private final Context context;
    private final ClipsIndicativeCategoryAdapter.OnSelectItemClickListener onSelectItemClickListener;

    public ClipsIndicativeCategoryAdapter(List<CategoryDataOfConsultantVideos> list, Context context, ClipsIndicativeCategoryAdapter.OnSelectItemClickListener listener) {
        categoryDataList = list;
        this.context = context;
        onSelectItemClickListener = listener;
    }

    @NonNull
    @Override
    public ClipsIndicativeCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_consultants_video, parent, false);
        return new ClipsIndicativeCategoryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClipsIndicativeCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final CategoryDataOfConsultantVideos categoryData = categoryDataList.get(position);


        holder.tv_area_indicative.setText(categoryData.getName());
        holder.progress_bar_clips_category.setVisibility(View.VISIBLE);
        holder.img_category_clips.setImageResource(R.drawable.no_image);


        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCorners(10));

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(categoryData.getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progress_bar_clips_category.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progress_bar_clips_category.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.img_category_clips);

        holder.layout_indicative_item.setOnClickListener(v -> onSelectItemClickListener.onCategoryClipsCardItemClick(v, position, categoryData));


    }

    @Override
    public int getItemCount() {
        return categoryDataList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout layout_indicative_item;
        public TextView tv_area_indicative;
        public ProgressBar progress_bar_clips_category;
        public ImageView img_category_clips;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout_indicative_item = itemView.findViewById(R.id.layout_indicative_item);
            tv_area_indicative = itemView.findViewById(R.id.tv_area_indicative);
            progress_bar_clips_category = itemView.findViewById(R.id.progress_bar_clips_category);
            img_category_clips = itemView.findViewById(R.id.img_category_clips);


        }
    }

    public interface OnSelectItemClickListener {
        void onCategoryClipsCardItemClick(View view, int position, CategoryDataOfConsultantVideos item);
    }
}
