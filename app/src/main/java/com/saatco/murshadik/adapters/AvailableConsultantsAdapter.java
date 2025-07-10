package com.saatco.murshadik.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.DialogUtil;

import java.util.ArrayList;
import java.util.Locale;

public class AvailableConsultantsAdapter extends RecyclerView.Adapter<AvailableConsultantsAdapter.ViewHolder> {

    private ArrayList<User> consultants;
    private final OnSelectCallListener onSelectCallListener;
    Activity activity;
    String skill;

    public static class ViewHolder extends RecyclerView.ViewHolder {

       public ImageView iv_consultant_image;
       public TextView tv_consultant_name;
       public TextView tv_consultant_skills;
       public TextView tv_consultant_region;
       public TextView tv_rating;
       public FrameLayout fl_video_call;
       public FrameLayout fl_voice_call;
       public Button btn_show_profile;


        public ViewHolder(View view) {
            super(view);
            iv_consultant_image = view.findViewById(R.id.iv_consultant_image);
            tv_consultant_name = view.findViewById(R.id.tv_consultant_name);
            tv_consultant_skills = view.findViewById(R.id.tv_consultant_skills);
            tv_consultant_region = view.findViewById(R.id.tv_consultant_region);
            tv_rating = view.findViewById(R.id.tv_rating);
            fl_video_call = view.findViewById(R.id.fl_video_call);
            fl_voice_call = view.findViewById(R.id.fl_voice_call);
            btn_show_profile = view.findViewById(R.id.btn_show_profile);
        }
    }

    public AvailableConsultantsAdapter(ArrayList<User> list, Activity context, String skill, OnSelectCallListener listener) {
        consultants = list;
        this.activity = context;
        this.onSelectCallListener = listener;
        this.skill = skill;
    }

    @Override
    public AvailableConsultantsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_call_consult_founded, parent, false);
        return new AvailableConsultantsAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final AvailableConsultantsAdapter.ViewHolder holder, final int position) {

        final User consultant = consultants.get(position);

        holder.tv_consultant_region.setText(consultant.getLocation());
        holder.tv_consultant_skills.setText(skill);
        holder.tv_consultant_name.setText(consultant.getFirstName());
        holder.tv_rating.setText(String.format(Locale.US, "%.1f", consultant.getUserRatings()));

        RequestOptions requestOptions = new RequestOptions().override(100, 100).circleCrop();
        Glide.with(activity)
                .asBitmap()
                .load(APIClient.imageUrl + consultant.getPhotoUrl())
                .apply(requestOptions)
                .placeholder(R.drawable.ag_logo)
                .into(holder.iv_consultant_image);


        holder.fl_video_call.setOnClickListener(v -> onSelectCallListener.onSelectCall(v, position, consultant, true));

        holder.fl_voice_call.setOnClickListener(v -> onSelectCallListener.onSelectCall(v, position, consultant, false));

        holder.btn_show_profile.setOnClickListener(v -> {
            DialogUtil.showInfoDialog(activity, 2, activity.getString(R.string.profile), consultant.getProfile());
        });

    }

    public void updateList(ArrayList<User> list){
        consultants = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return consultants.size();
    }

    public interface OnSelectCallListener {
        void onSelectCall(View view, int position, User consultant, boolean isVideoCall);
    }

}
