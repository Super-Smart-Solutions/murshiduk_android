package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.LanguageUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Category;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class CatagoryGridAdapter  extends RecyclerView.Adapter<CatagoryGridAdapter.ViewHolder> {

    private ArrayList<Category> categories;
    private final CatagoryGridAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public View layout;
        public TextView tvTitle;
        public LinearLayout layoutBG;
        public ImageView bg;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivIcon);
            tvTitle = view.findViewById(R.id.title);
            layoutBG = view.findViewById(R.id.layoutBG);
            bg = view.findViewById(R.id.ivBG);
        }
    }

    public CatagoryGridAdapter(ArrayList<Category> list, Context context, CatagoryGridAdapter.OnSelectItemClickListener listener) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public CatagoryGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_1, parent, false);
        return new CatagoryGridAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final CatagoryGridAdapter.ViewHolder holder, final int position) {

        final Category item = categories.get(position);

        holder.tvTitle.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getItem().getNameAr() : item.getItem().getName());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String image = APIClient.imageUrl + item.getItem().getIcon();
        String banner = APIClient.imageUrl + item.getItem().getBannerAr();

        Glide.with(context).load(image).placeholder(circularProgressDrawable).into(holder.imageView);

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onCategoryGridClick(view, position, item.getItem());
            }
        });

        Glide.with(context).load(banner).placeholder(circularProgressDrawable).into(holder.bg);

    }

    public void updateList(ArrayList<Category> list) {
        categories = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnSelectItemClickListener {
        void onCategoryGridClick(View view, int position, Item item);
    }

}

