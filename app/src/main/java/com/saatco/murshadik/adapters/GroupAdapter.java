package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.model.GroupChat;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private ArrayList<GroupChat> groups;
    private final GroupAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvTitle,tvDesc,tvDate;
        public LinearLayout layoutBG;
        public ImageView bg;

        public ViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_package);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            tvDate = view.findViewById(R.id.tvDate);
            layoutBG = view.findViewById(R.id.layoutBG);
            bg = view.findViewById(R.id.ivBG);
        }
    }

    public GroupAdapter(ArrayList<GroupChat> list, Context context,GroupAdapter.OnSelectItemClickListener listener) {
        groups = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final GroupAdapter.ViewHolder holder, final int position) {

        final GroupChat group = groups.get(position);

        holder.tvTitle.setText(group.getName());
        holder.tvDesc.setText("");

       /* CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();*/

       /* String banner = APIClient.imageUrl + item.getAvatar();
        Glide.with(context)
                .asBitmap()
                .load(banner)
                .placeholder(circularProgressDrawable)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //  ivBanner3.setImageBitmap(resource);

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        final float roundPx = (float) resource.getWidth() * 1f;
                        roundedBitmapDrawable.setCornerRadius(roundPx);

                        holder.bg.setImageDrawable(roundedBitmapDrawable);

                    }
                });*/

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onGroupClick(view,position,group);
            }
        });

    }

    public void updateList(ArrayList<GroupChat> list){
        groups = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public interface OnSelectItemClickListener {
        void onGroupClick(View view, int position, GroupChat group);
    }

}
