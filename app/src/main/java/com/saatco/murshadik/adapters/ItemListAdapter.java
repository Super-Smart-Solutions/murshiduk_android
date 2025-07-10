package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.LanguageUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    public final static int MAX_DATA_TO_SHOW = 60;
    private List<Item> categories = new ArrayList<>();
    private final ItemListAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public View layout;
        public TextView tvTitle, tvDesc, tvDate;
        public LinearLayout layoutBG;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivIcon);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDate = view.findViewById(R.id.tvDate);
            layoutBG = view.findViewById(R.id.layoutBG);
            tvDesc = view.findViewById(R.id.tvDesc);
        }
    }

    public ItemListAdapter(List<Item> list, Context context, ItemListAdapter.OnSelectItemClickListener listener) {
        categories = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calender_detail_item, parent, false);
        return new ItemListAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ItemListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final Item item = categories.get(position);

        holder.tvTitle.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getTitle_ar() : item.getTitle());
        holder.tvDesc.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getDescriptionAr() : item.getDescription());
        holder.tvDate.setText(item.getDateFormatted());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String image = APIClient.imageUrl + item.getImage();

        Glide.with(context)
                .asBitmap()
                .load(image)
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                .into(holder.imageView);


        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onItemClick(view, position, item);
            }
        });

    }

    public void updateList(List<Item> list) {
        categories = list;
        notifyDataSetChanged();
    }


    public void addMoreDataAtTheEnd(List<Item> list) {
        if (list.size() < 1) return;
        if (categories.addAll(list)){
            notifyItemRangeInserted(categories.size() - list.size() - 1, list.size());
        }

        list.clear();
//        return checkDataOverLoad(true);
    }


    public int addMoreDataAtTheStart(List<Item> list) {
        if (list.size() < 1) return 0;
        if (categories.addAll(0, list))
            notifyItemRangeInserted(0, list.size());

        list.clear();
        return checkDataOverLoad(false);
    }

    /**
     * @param is_remove_from_start
     * @return
     */
    public int checkDataOverLoad(boolean is_remove_from_start) {
        int size_gap_count = categories.size() - MAX_DATA_TO_SHOW;

        if (categories.size() > MAX_DATA_TO_SHOW) {
            if (is_remove_from_start) {
                while (categories.size() > MAX_DATA_TO_SHOW)
                    categories.remove(0);

                if (size_gap_count > 0)
                    notifyItemRangeRemoved(0, size_gap_count);
            } else {
                while (categories.size() > MAX_DATA_TO_SHOW)
                    categories.remove(categories.size() - 1);

                if (size_gap_count > 0)
                    notifyItemRangeRemoved(categories.size(), size_gap_count);
            }
        }

        return Math.max(size_gap_count, 0);
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    public Item getItem(int pos){
        return categories.get(pos);
    }

    public interface OnSelectItemClickListener {
        void onItemClick(View view, int position, Item item);
    }

}
