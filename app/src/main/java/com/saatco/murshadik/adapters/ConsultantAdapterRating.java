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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.MyDiffUtilCallback;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class ConsultantAdapterRating extends RecyclerView.Adapter<ConsultantAdapterRating.ViewHolder> {

    private ArrayList<User> consultants;
    private final ConsultantAdapterRating.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvTitle,tvDesc,tvRegion,tvRating,online_status_tv;
        public LinearLayout layoutBG,btnProfile;
        public ImageView bg,onlineStatus;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            layoutBG = view.findViewById(R.id.layoutBG);
            bg = view.findViewById(R.id.ivBG);
            tvRating = view.findViewById(R.id.tvRating);
            onlineStatus = view.findViewById(R.id.onlineStatus);
            online_status_tv = view.findViewById(R.id.online_status_tv);
            btnProfile = view.findViewById(R.id.btnProfile);
            tvRegion = view.findViewById(R.id.tvRegion);

        }
    }

    public ConsultantAdapterRating(ArrayList<User> list, Context context,ConsultantAdapterRating.OnSelectItemClickListener listener) {
        consultants = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public ConsultantAdapterRating.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consultant_rating, parent, false);
        return new ConsultantAdapterRating.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ConsultantAdapterRating.ViewHolder holder, final int position) {

        final User user = consultants.get(position);

        holder.tvTitle.setText(user.getName().replace("NULL",""));
      //  holder.tvDesc.setText(user.getStatus());
        holder.tvRegion.setText(user.getLocation());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        holder.tvRating.setText(String.format(Locale.US, "%.1f", user.getUserRatings()));

        if(user.getPhotoUrl() != null) {
            holder.setIsRecyclable(false);
            RequestOptions requestOptions = new RequestOptions().override(100, 100).circleCrop();
            Glide.with(context)
                    .asBitmap()
                    .load(APIClient.imageUrl + user.getPhotoUrl())
                    .apply(requestOptions)
                    .placeholder(R.drawable.ag_logo)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            final float roundPx = (float) resource.getWidth() * 2f;
                            roundedBitmapDrawable.setCornerRadius(roundPx);

                            holder.bg.setImageBitmap(resource);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }else{
            holder.setIsRecyclable(true);
        }

        if(user.isOnline()) {
            holder.online_status_tv.setText("متصل الآن");
            holder.online_status_tv.setTextColor(context.getResources().getColor(R.color.colorGreen));
            holder.onlineStatus.setColorFilter(context.getResources().getColor(R.color.colorGreen));
        }
         else {
            holder.online_status_tv.setTextColor(context.getColor(R.color.btn_scroll_icon_color));
            holder.onlineStatus.setColorFilter(context.getColor(R.color.btn_scroll_icon_color));
            holder.online_status_tv.setText("غير متصل");
        }

         holder.layoutBG.setOnClickListener(view -> onFavItemClickListener.onConsultantClick(view,position,user));

         holder.btnProfile.setOnClickListener(view -> onFavItemClickListener.onProfileClick(view,position,user));

    }

    public void updateList(ArrayList<User> list){
        consultants = list;
        notifyDataSetChanged();
    }

    public void updateSingle(int userId,String status){

        for (int i = 0; i < consultants.size(); i++){
            if (consultants.get(i).getId() == userId){
                consultants.get(i).setOnline(status.equals("online"));
                notifyItemChanged(i);
            }
        }
    }

    private int getPosition(int userId){
        int index = 0;
        for(int i=0;i<consultants.size();i++){
            if(consultants.get(i).getId() == userId){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public int getItemCount() {
        return consultants.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnSelectItemClickListener {
        void onConsultantClick(View view, int position, User user);
        void onProfileClick(View view, int position, User user);
    }

    public void updateUserListItems(ArrayList<User> employees) {
        final MyDiffUtilCallback diffCallback = new MyDiffUtilCallback(this.consultants, employees);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(this);
    }
}


