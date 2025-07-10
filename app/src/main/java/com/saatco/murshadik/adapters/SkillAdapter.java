package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {

    private final SkillAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;
    private ArrayList<Item> categories;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name,tvStatus;
        public View layout;
        public ImageView ivChild,ivCheck;

        public ViewHolder(View view) {
            super(view);
            ivChild = view.findViewById(R.id.ivChild);
            ivCheck = view.findViewById(R.id.ivCheck);
            name = view.findViewById(R.id.dropdown);
            tvStatus = view.findViewById(R.id.tvStatus);
            layout = view.findViewById(R.id.layout_city);
        }
    }

    public SkillAdapter(ArrayList<Item> list, Context context, SkillAdapter.OnSelectItemClickListener listener) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public SkillAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill, parent, false);
        return new SkillAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final SkillAdapter.ViewHolder holder, final int position) {

        final Item item = categories.get(position);

        holder.name.setText(item.getNameAr());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onSkillSelect(view,position,item);
            }
        });

        if(item.isChild()){
            holder.ivChild.setVisibility(View.VISIBLE);
        }else{
            holder.ivChild.setVisibility(View.GONE);
        }

        if(item.isSelected()) {
            if(item.isApproved()) {
                holder.ivCheck.setVisibility(View.VISIBLE);
                holder.ivCheck.setImageResource(R.drawable.checkbox_selected);
                holder.tvStatus.setVisibility(View.GONE);
            }
            else{
                holder.ivCheck.setVisibility(View.GONE);
                holder.ivCheck.setImageResource(R.drawable.checkbox_empty);
                holder.tvStatus.setVisibility(View.VISIBLE);
            }

        }else{
            holder.ivCheck.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.GONE);
            holder.ivCheck.setImageResource(R.drawable.plus);
        }


    }

    public void updateList(ArrayList<Item> list){
        categories = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnSelectItemClickListener {
        void onSkillSelect(View view, int position, Item skill);
    }

}

