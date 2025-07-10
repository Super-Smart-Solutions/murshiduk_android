package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.OnReceiveContentListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.saatco.murshadik.databinding.ItemConsultantClipsCardBinding;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.R;
import com.saatco.murshadik.views.CustomFrameLayout;
import com.saatco.murshadik.model.consultantvideos.VideoDataOfConsultantVideos;

import java.util.List;

public class ConsultantVideoClipsListAdapter extends RecyclerView.Adapter<ConsultantVideoClipsListAdapter.ViewHolder> {

    private List<VideoDataOfConsultantVideos> videoData;
    private final Context context;
    private final ConsultantVideoClipsListAdapter.OnSelectItemClickListener onSelectItemClickListener;

    public ConsultantVideoClipsListAdapter(List<VideoDataOfConsultantVideos> list, Context context, ConsultantVideoClipsListAdapter.OnSelectItemClickListener listener) {
        videoData = list;
        this.context = context;
        onSelectItemClickListener = listener;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<VideoDataOfConsultantVideos> filter_list) {
        videoData = filter_list;
        notifyDataSetChanged();
    }

    public List<VideoDataOfConsultantVideos> getCurrentDataList(){
        return videoData;
    }

    @NonNull
    @Override
    public ConsultantVideoClipsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConsultantClipsCardBinding itemView = ItemConsultantClipsCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ConsultantVideoClipsListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultantVideoClipsListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final VideoDataOfConsultantVideos consultantClipVideo = videoData.get(position);

        holder.tv_date.setText(consultantClipVideo.getCreatAt().split(" ")[0]);
        holder.tv_consultant_name.setText(consultantClipVideo.getConsultantName());
        holder.tv_title.setText(consultantClipVideo.getTitle());
        holder.tv_department_indicative.setText(consultantClipVideo.getDepartment());
        holder.progress_bar_clips_card.setVisibility(View.VISIBLE);
        holder.img_clip.setImageResource(R.drawable.no_image);


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(consultantClipVideo.getLocation()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progress_bar_clips_card.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progress_bar_clips_card.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.img_clip);


        holder.layoutBG.setOnClickListener(v -> onSelectItemClickListener.onClipCardItemClick(v, position, consultantClipVideo));


    }

    @Override
    public int getItemCount() {
        return videoData.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView img_clip;
        public TextView tv_title, tv_consultant_name, tv_department_indicative, tv_date, tv_review;
        public Button btn_edit;
        public FrameLayout layoutBG;
        public ProgressBar progress_bar_clips_card;

        public ViewHolder(@NonNull ItemConsultantClipsCardBinding itemView) {
            super(itemView.getRoot());

            img_clip = itemView.ivClipImg;
            tv_title = itemView.tvTitle;
            tv_consultant_name = itemView.tvConsultantName;
            tv_department_indicative = itemView.tvAreaIndicative;
            tv_date = itemView.tvDate;
            layoutBG = itemView.layoutBG;
            progress_bar_clips_card = itemView.progressBarClipsCard;

        }
    }

    public interface OnSelectItemClickListener {
        void onClipCardItemClick(View view, int position, VideoDataOfConsultantVideos item);
    }
}
