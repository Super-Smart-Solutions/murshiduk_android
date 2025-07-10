package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingDataAdapter;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.LanguageUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;
import java.util.List;

import kotlin.coroutines.Continuation;

public class ItemListAdapterNew extends PagingDataAdapter<Item, ItemListAdapterNew.ViewHolder> {

    private final List<Item> categories = new ArrayList<>();
    private ItemListAdapterNew.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public ItemListAdapterNew(@NonNull DiffUtil.ItemCallback<Item> diffCallback) {
        super(diffCallback);
    }







    @Override
    public ItemListAdapterNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calender_detail_item, parent, false);
        return new ItemListAdapterNew.ViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Item item = getItem(position);

        if (item == null)
            return;

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

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnSelectItemClickListener {
        void onItemClick(View view, int position, Item item);
    }


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

    public class ItemDataSource extends PagingSource<Integer, Item> {


        @Nullable
        @Override
        public Integer getRefreshKey(@NonNull PagingState<Integer, Item> pagingState) {

            return null;
        }

        @Nullable
        @Override
        public Object load(@NonNull LoadParams<Integer> loadParams, @NonNull Continuation<? super LoadResult<Integer, Item>> continuation) {
            return null;
        }
    }


}
