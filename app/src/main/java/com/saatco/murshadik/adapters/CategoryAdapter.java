package com.saatco.murshadik.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.views.CustomFrameLayout;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<Item> categories;
    private final CategoryAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;
    boolean isSection8;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public View layout;
        public TextView tvTitle;
        public CardView layoutBG;
        public ImageView bg;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivIcon);
            tvTitle = view.findViewById(R.id.title);
            layoutBG = view.findViewById(R.id.layoutBG);
            bg = view.findViewById(R.id.ivBG);
        }
    }

    public CategoryAdapter(ArrayList<Item> list, Context context, CategoryAdapter.OnSelectItemClickListener listener, boolean isSection8) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
        this.isSection8 = isSection8;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consulting, parent, false);
        return new CategoryAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder holder, final int position) {

        final Item item = categories.get(position);

        holder.tvTitle.setText(item.getName());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(2f);
        circularProgressDrawable.setCenterRadius(15f);
        circularProgressDrawable.start();

        String image = APIClient.imageUrl + item.getIcon();
        String banner = APIClient.imageUrl + item.getBanner();


        RequestOptions requestOptions = new RequestOptions().override(Build.VERSION.SDK_INT < Build.VERSION_CODES.P ? 200 : 400, Build.VERSION.SDK_INT < Build.VERSION_CODES.P ? 200 : 400);


        Glide.with(context).load(image).apply(requestOptions).placeholder(circularProgressDrawable).into(holder.imageView);


        holder.layoutBG.setOnClickListener(view -> onFavItemClickListener.onCategoryClick(view, position, item));


    }

    public void updateList(ArrayList<Item> list) {
        categories = list;
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
    }

}