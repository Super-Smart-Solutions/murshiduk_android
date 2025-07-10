package com.saatco.murshadik.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.model.Appointment;

import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private final AppointmentAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvLabName,tvStatus,tvDate,tvTime;
        public ImageView btnCancel;
        public LinearLayout layoutBG;

        public ViewHolder(View view) {
            super(view);

            tvLabName = view.findViewById(R.id.tvLabName);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvTime = view.findViewById(R.id.tvTime);
            tvDate = view.findViewById(R.id.tvDate);
            btnCancel = view.findViewById(R.id.btnCancel);
            layoutBG = view.findViewById(R.id.layoutBG);

        }
    }

    public AppointmentAdapter(List<Appointment> list, Context context, AppointmentAdapter.OnSelectItemClickListener listener) {
        appointments = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public AppointmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final AppointmentAdapter.ViewHolder holder, final int position) {

        final Appointment appointment = appointments.get(position);

        holder.tvLabName.setText(appointment.getLabName());

        holder.tvDate.setText(appointment.getDate());
        holder.tvTime.setText(String.format(Locale.ENGLISH,appointment.getTime()));
        holder.tvStatus.setText(appointment.getStatus());
        holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(appointment.getStatusColor())));

        if(!appointment.isCompleted()){
            holder.btnCancel.setVisibility(View.VISIBLE);
        }else
            holder.btnCancel.setVisibility(View.GONE);


        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onClickAppointment(view,position,appointment);
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onCancelAppointment(view,position,appointment);
            }
        });

    }

    public void updateList(List<Appointment> list) {
        appointments = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public interface OnSelectItemClickListener {
        void onClickAppointment(View view, int position, Appointment appointment);
        void onCancelAppointment(View view, int position, Appointment appointment);
    }

}
