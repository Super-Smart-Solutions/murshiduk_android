package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.R;
import com.saatco.murshadik.model.consultantvideos.VideoDataOfConsultantVideos;
import com.saatco.murshadik.views.TextViewWithDotAndBg;
import com.saatco.murshadik.views.TextViewWithIcon;


import java.util.List;

public class ConsultantClipVideoAdapter extends RecyclerView.Adapter<ConsultantClipVideoAdapter.ViewHolder> {

    private List<VideoDataOfConsultantVideos> consultantClipVideoList;
    private final Context context;
    private final ConsultantClipVideoAdapter.OnSelectItemClickListener onSelectItemClickListener;

    public ConsultantClipVideoAdapter(List<VideoDataOfConsultantVideos> list, Context context, ConsultantClipVideoAdapter.OnSelectItemClickListener listener) {
        consultantClipVideoList = list;
        this.context = context;
        onSelectItemClickListener = listener;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<VideoDataOfConsultantVideos> filter_list) {
        consultantClipVideoList = filter_list;
        notifyDataSetChanged();
    }

    public List<VideoDataOfConsultantVideos> getCurrentDataList() {
        return consultantClipVideoList;
    }

    @NonNull
    @Override
    public ConsultantClipVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consultant_clips_card_with_edit_btn, parent, false);
        return new ConsultantClipVideoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final VideoDataOfConsultantVideos consultantClipVideo = consultantClipVideoList.get(position);


        holder.tv_date.setText(consultantClipVideo.getCreatAt().substring(0, 10));
        holder.tv_consultant_name.setText(consultantClipVideo.getConsultantName());
        holder.tv_title.setText(consultantClipVideo.getTitle());
        holder.tv_department_indicative.setText(consultantClipVideo.getDepartment());
        holder.progress_bar_clips_card_with_edit_btn.setVisibility(View.VISIBLE);
        holder.img_clip.setImageResource(R.drawable.no_image);


        if (consultantClipVideo.getVideoStatus() == 1) {
            holder.tv_review.setText(context.getString(R.string.requist_accepted));
            holder.tv_review.setDotColor(context.getColor(R.color.greenLight));
            if (consultantClipVideo.isHidden()) {
                holder.tv_is_hidden.setText(context.getString(R.string.clip_invisible_to_all));
                holder.tv_is_hidden.setDotColor(context.getColor(R.color.red));
            } else {
                holder.tv_is_hidden.setText(context.getString(R.string.clip_visible_to_all));
                holder.tv_is_hidden.setDotColor(context.getColor(R.color.greenLight));
            }
        } else {
            holder.tv_review.setText(context.getString(R.string.reviewing));
            holder.tv_review.setDotColor(context.getColor(R.color.gray_light));
            holder.tv_is_hidden.setText(context.getString(R.string.clip_invisible_to_all));
            holder.tv_is_hidden.setDotColor(context.getColor(R.color.red));
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        requestOptions = requestOptions.override(100, 100).centerCrop();
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(consultantClipVideo.getLocation())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.img_clip.setImageDrawable(resource);
                        holder.progress_bar_clips_card_with_edit_btn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }


                });

        holder.btn_edit.setOnClickListener(v -> onSelectItemClickListener.onEditBtnItemClick(v, position, consultantClipVideo));

        holder.layoutBG.setOnClickListener(v -> onSelectItemClickListener.onClipCardItemClick(v, position, consultantClipVideo));

    }

    @Override
    public int getItemCount() {
        return consultantClipVideoList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_clip;
        public TextView tv_title;
        public TextViewWithIcon tv_consultant_name, tv_department_indicative, tv_date;
        public TextViewWithDotAndBg tv_review, tv_is_hidden;
        public Button btn_edit;
        public LinearLayout layoutBG;
        public ProgressBar progress_bar_clips_card_with_edit_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_clip = itemView.findViewById(R.id.iv_clip_img);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_consultant_name = itemView.findViewById(R.id.tvi_consultant_name);
            tv_department_indicative = itemView.findViewById(R.id.tvi_area_indicative);
            tv_date = itemView.findViewById(R.id.tvi_date);
            tv_review = itemView.findViewById(R.id.ctv_review);
            tv_is_hidden = itemView.findViewById(R.id.ctv_is_hidden);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            layoutBG = itemView.findViewById(R.id.layoutBG);
            progress_bar_clips_card_with_edit_btn = itemView.findViewById(R.id.progress_bar_clips_card_with_edit_btn);

        }
    }

    public interface OnSelectItemClickListener {
        void onClipCardItemClick(View view, int position, VideoDataOfConsultantVideos item);

        void onEditBtnItemClick(View view, int position, VideoDataOfConsultantVideos item);
    }

}
