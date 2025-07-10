package com.saatco.murshadik.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager.widget.PagerAdapter;

public class AgriProductAdapter extends PagerAdapter {

    private ArrayList<Item> products;
    private final AgriProductAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;
    private final LayoutInflater inflater;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvTitle, dateTime;
        public LinearLayout layoutBG;
        public ImageView bg;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            dateTime = view.findViewById(R.id.tv_date_time);
            layoutBG = view.findViewById(R.id.layoutBG);
            bg = view.findViewById(R.id.ivBG);
        }
    }

    public AgriProductAdapter(ArrayList<Item> list, Context context,AgriProductAdapter.OnSelectItemClickListener listener) {
        products = list;
        this.context = context;
        this.onFavItemClickListener = listener;
        inflater = LayoutInflater.from(context);
    }



    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ViewGroup imageLayout = (ViewGroup) inflater.inflate(R.layout.item_testimonial, collection, false);

        final Item item = products.get(position);

        TextView tvTitle = imageLayout.findViewById(R.id.tvTitle);
        TextView dateTime = imageLayout.findViewById(R.id.tv_date_time);
        LinearLayout layoutBG = imageLayout.findViewById(R.id.layoutBG);
        ImageView bg = imageLayout.findViewById(R.id.ivBG);

        dateTime.setText(item.getDateFormatted());

        tvTitle.setText(item.getTitle_ar());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String banner = APIClient.imageUrl + item.getImage();

        RequestOptions requestOptions = new RequestOptions().override(300,300).transforms(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context)
                .asBitmap()
                .load(banner)
                .override(300, 100)
                .placeholder(circularProgressDrawable)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //  ivBanner3.setImageBitmap(resource);

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        final float roundPx = (float) resource.getWidth() * 0.1f;
                        roundedBitmapDrawable.setCornerRadius(roundPx);

                        bg.setImageDrawable(roundedBitmapDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        layoutBG.setOnClickListener(view -> onFavItemClickListener.onProductClick(view,position,item));


        collection.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    public void updateList(ArrayList<Item> list){
        products = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return products.get(position).getTitle_ar();
    }

    public interface OnSelectItemClickListener {
        void onProductClick(View view, int position, Item item);
    }

}

