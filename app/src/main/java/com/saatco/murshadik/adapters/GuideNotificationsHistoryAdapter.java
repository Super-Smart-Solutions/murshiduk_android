package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.Helpers.DataHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.model.GuideNotification;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.consultantvideos.CommentOfConsultantVideo;

import java.util.ArrayList;
import java.util.List;

public class GuideNotificationsHistoryAdapter extends RecyclerView.Adapter<GuideNotificationsHistoryAdapter.ViewHolder> {

    private final List<GuideNotification> guideNotifications;
    private final Context context;
    private final OnClickListener onReplayItemClickListener;
    private final ArrayList<Item> regions;

    public GuideNotificationsHistoryAdapter(List<GuideNotification> list, Context context, OnClickListener onReplayItemClickListener) {
        guideNotifications = list;
        this.context = context;
        this.regions = DataHelper.getRegions();
        this.onReplayItemClickListener = onReplayItemClickListener;
    }

    @NonNull
    @Override
    public GuideNotificationsHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guide_notification, parent, false);
        return new GuideNotificationsHistoryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideNotificationsHistoryAdapter.ViewHolder holder, int position) {
        final GuideNotification guideNotification = guideNotifications.get(position);
        holder.tv_date.setText(guideNotification.getCreated_at().split("T")[0]);
        holder.tv_cities.setText(guideNotification.getCities().replaceAll(",", ", "));
        holder.tv_notify_text.setText(guideNotification.getGuide_alert_text());
        holder.tv_region.setText(DataHelper.getRegionById(regions, guideNotification.getRegion_id()));
        holder.tv_skills.setText(guideNotification.getSkills().replaceAll(",", ", "));
    }

    @Override
    public int getItemCount() {
        return guideNotifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_date, tv_region, tv_cities, tv_skills, tv_notify_text;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_region = itemView.findViewById(R.id.tv_region);
            tv_cities = itemView.findViewById(R.id.tv_cities);
            tv_skills = itemView.findViewById(R.id.tv_skills);
            tv_notify_text = itemView.findViewById(R.id.tv_notify_text);

        }
    }


    public interface OnClickListener {
        void onClick(View view, int position, GuideNotification item);
    }

}
