package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.dialogs.FilterDialogModel;
import com.saatco.murshadik.views.CustomFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class FilterDialogAdapter extends RecyclerView.Adapter<FilterDialogAdapter.ViewHolder> {

    ArrayList<FilterDialogModel> list;
    Context context;
    OnItemListener listener;

    public FilterDialogAdapter(Context context, ArrayList<FilterDialogModel> list, OnItemListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_dialog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterDialogModel data = list.get(position);
        holder.textView.setText(data.getText());
        holder.cflView.setOnClickListener(v -> listener.onClick(data));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<FilterDialogModel> list){
        this.list = (ArrayList<FilterDialogModel>) list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CustomFrameLayout cflView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            cflView = itemView.findViewById(R.id.cfl_view);
        }
    }

    public interface OnItemListener{
        void onClick(FilterDialogModel filterData);
    }
}
