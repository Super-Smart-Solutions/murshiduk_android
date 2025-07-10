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
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.LanguageUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class SeminarAdapter extends RecyclerView.Adapter<SeminarAdapter.ViewHolder> {

    private ArrayList<Item> seminars;
    private final SeminarAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvTitle,tvDesc;
        public LinearLayout layoutBG;
        public ImageView bg;

        public ViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_package);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            layoutBG = view.findViewById(R.id.layoutBG);
            bg = view.findViewById(R.id.ivBG);
        }
    }

    public SeminarAdapter(ArrayList<Item> list, Context context,SeminarAdapter.OnSelectItemClickListener listener) {
        seminars = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public SeminarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seminar, parent, false);
        return new SeminarAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final SeminarAdapter.ViewHolder holder, final int position) {

        final Item item = seminars.get(position);

        holder.tvTitle.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getTitle_ar() : item.getTitle());
        holder.tvDesc.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getDescriptionAr() : item.getDescription());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String banner = APIClient.imageUrl + item.getImage();

        Glide.with(context)
                .asBitmap()
                .load(banner)
                .placeholder(circularProgressDrawable)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //  ivBanner3.setImageBitmap(resource);

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        final float roundPx = (float) resource.getWidth() * 0.02f;
                        roundedBitmapDrawable.setCornerRadius(roundPx);

                        holder.bg.setImageDrawable(roundedBitmapDrawable);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onSeminarClick(view,position,item);
            }
        });

    }

    public void updateList(ArrayList<Item> list){
        seminars = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return seminars.size();
    }

    public interface OnSelectItemClickListener {
        void onSeminarClick(View view, int position, Item item);
    }

}
