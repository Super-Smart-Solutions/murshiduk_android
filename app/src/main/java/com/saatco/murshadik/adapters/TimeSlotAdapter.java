package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.model.Appointment;

import java.util.ArrayList;
import java.util.Locale;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private ArrayList<Appointment> slots;
    private final TimeSlotAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;
    boolean isSection8;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTime;
        public LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            tvTime = view.findViewById(R.id.tvTime);
            layout = view.findViewById(R.id.layoutBG);
        }
    }

    public TimeSlotAdapter(ArrayList<Appointment> list, Context context, TimeSlotAdapter.OnSelectItemClickListener listener) {
        slots = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public TimeSlotAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final TimeSlotAdapter.ViewHolder holder, final int position) {

        final Appointment item = slots.get(position);
        holder.tvTime.setText(String.format(Locale.US,item.getTime()));


        if(item.isBooked())
        {
            holder.layout.setBackgroundColor(context.getColor(R.color.light_gray));
            holder.tvTime.setTextColor(context.getColor(R.color.grey));
        }
        else if(item.isSelected())
        {
            holder.layout.setBackgroundColor(context.getColor(R.color.colorPrimary));
            holder.tvTime.setTextColor(context.getColor(R.color.white));
        }
        else
        {
            holder.layout.setBackgroundColor(context.getColor(R.color.white));
            holder.tvTime.setTextColor(context.getColor(R.color.colorPrimary));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onCategoryClick(view, position, item);

            }
        });

    }

    public void updateList(ArrayList<Appointment> list) {
        slots = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return slots.size();
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
        void onCategoryClick(View view, int position, Appointment slot);
    }

}
