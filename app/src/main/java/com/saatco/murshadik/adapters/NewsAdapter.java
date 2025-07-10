package com.saatco.murshadik.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<Item> news;
    private final NewsAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvTitle,tvDesc;
        public LinearLayout layoutBG;
        public ImageView bg;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            layoutBG = view.findViewById(R.id.layoutBG);
            bg = view.findViewById(R.id.ivBG);
        }
    }

    public NewsAdapter(ArrayList<Item> list, Context context,NewsAdapter.OnSelectItemClickListener listener) {
        news = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final NewsAdapter.ViewHolder holder, final int position) {

        final Item item = news.get(position);

        holder.tvTitle.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getTitle_ar() : item.getTitle());
        holder.tvDesc.setText(LanguageUtil.getLanguage(context).equals("ar") ? item.getDescriptionAr() : item.getDescription());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String image = APIClient.imageUrl + item.getIcon();
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
                        final float roundPx = (float) resource.getWidth() * 0.04f;
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

                onFavItemClickListener.onNewsClick(view,position,item);
            }
        });

       // holder.layoutBG.setBackground(ContextCompat.getDrawable(context,R.drawable.gray));

    }

    public void updateList(ArrayList<Item> list){
        news = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public interface OnSelectItemClickListener {
        void onNewsClick(View view, int position, Item item);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
