package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.saatco.murshadik.R;
import com.saatco.murshadik.model.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notifications> notifications;
    private final NotificationAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;
    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView,btnDelete;
        public View layout;
        public TextView tvName,tvDate,tvHeader;
        public LinearLayout layoutBG;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivProduct);
            tvName = view.findViewById(R.id.tvTitle);
            tvDate = view.findViewById(R.id.tvDate);
            layoutBG = view.findViewById(R.id.layoutBG);
            tvHeader = view.findViewById(R.id.tvHeader);
        }
    }

    public NotificationAdapter(List<Notifications> list, Context context, NotificationAdapter.OnSelectItemClickListener listener) {
        notifications = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, final int position) {

        final Notifications notification = notifications.get(position);

        holder.tvName.setText(notification.getType() == 1 ? notification.getMessage() : notification.getText());
        holder.tvDate.setText(notification.getType() == 1 ? getFormattedDate(notification.getNotificationAt()) : getFormattedDate(notification.getDate()));

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavItemClickListener.onItemClick(view, position, notification);
            }
        });

        holder.tvHeader.setVisibility(View.GONE);

        if(notifications.get(position).getType() == 1)
            holder.imageView.setImageResource(R.drawable.temperature);
        else if(notifications.get(position).getType() == 2)
            holder.imageView.setImageResource(R.drawable.dollar);
        else if(notifications.get(position).getType() == 3)
            holder.imageView.setImageResource(R.drawable.flask);
        else if(notifications.get(position).getType() == 6 || notifications.get(position).getType() == 5)
            holder.imageView.setImageResource(R.drawable.help);
        else if(notifications.get(position).getType() == 4)
            holder.imageView.setImageResource(R.drawable.user);
        else if(notifications.get(position).getType() == 7)
            holder.imageView.setImageResource(R.drawable.phone);
        else if(notifications.get(position).getType() == 9)
            holder.imageView.setImageResource(R.drawable.total_users);
        else
            holder.imageView.setImageResource(R.drawable.logo);


        // if not first item check if item above has the same header
      /*  if (position > 0 && notifications.get(position - 1).getType() == 1 || position > 0 && notifications.get(position - 1).getType() == 2 || position > 0 && notifications.get(position - 1).getType() == 3 || position > 0 && notifications.get(position - 1).getType() == 4 || position > 0 && notifications.get(position - 1).getType() == 5 || position > 0 && notifications.get(position - 1).getType() == 6 || position > 0 && notifications.get(position - 1).getType() == 7) {
            holder.tvHeader.setVisibility(View.GONE);

            if(notifications.get(position - 1).getType() == 1)
                holder.tvHeader.setText("إشعارات الطقس");
            else{
                holder.tvHeader.setText("الإشعارات الاخرى");
            }

        } else {

            if(notifications.get(position).getType() == 1)
                holder.tvHeader.setText("إشعارات الطقس");
            else{
                holder.tvHeader.setText("إشعارات الاخرى");
            }

            holder.tvHeader.setVisibility(View.VISIBLE);
        }*/
    }

    private String getFormattedDate(String date){

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.US);

        try {
            date  = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }

    public void updateList(List<Notifications> list) {
        notifications = list;
       // Collections.sort(notifications);
        notifyDataSetChanged();
    }

    public List<Notifications> getData(){
        return notifications;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public interface OnSelectItemClickListener {
        void onItemClick(View view, int position, Notifications notification);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
