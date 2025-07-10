package com.saatco.murshadik.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.CategoryDiffUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChatCategoryAdapter extends RecyclerView.Adapter<ChatCategoryAdapter.ViewHolder> {

    private List<Item> categories;
    private final ChatCategoryAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvTitle;
        public LinearLayout layoutBG;
        public ImageView ivCategory;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            layoutBG = view.findViewById(R.id.layoutBG);
            ivCategory = view.findViewById(R.id.ivCategory);
        }
    }

    public ChatCategoryAdapter(ArrayList<Item> list, Context context,ChatCategoryAdapter.OnSelectItemClickListener listener) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public ChatCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_category_grid, parent, false);
        return new ChatCategoryAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ChatCategoryAdapter.ViewHolder holder, final int position) {

        final Item item = categories.get(position);

        holder.tvTitle.setText(item.getNameAr());

            if(item.getIcon() != null) {
                holder.setIsRecyclable(false);
                RequestOptions requestOptions = new RequestOptions().override(100, 100).circleCrop();
                Glide.with(context)
                        .asBitmap()
                        .load(APIClient.baseUrl + item.getIcon())
                        .apply(requestOptions)
                        .placeholder(R.drawable.ag_logo)
                        .into(holder.ivCategory);
            }else{
                holder.setIsRecyclable(true);
            }


        holder.layoutBG.setOnClickListener(view -> onFavItemClickListener.onCategoryClick(view,position,item));

    }

    public void updateList(List<Item> list){
        categories = list;
        notifyDataSetChanged();
    }

    public void filter(String s){
        categories = categories.stream().filter(item -> item.getNameAr().contains(s)).collect(Collectors.toList());
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return categories.size();
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
        void onCategoryClick(View view, int position, Item item);
        void onUserClick(View view, int position, User item);
    }



}
