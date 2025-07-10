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

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Product;

import java.util.List;

public class ProductAlertAdapter extends RecyclerView.Adapter<ProductAlertAdapter.ViewHolder> {

    private List<Product> products;
    private final ProductAlertAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView,btnDelete;
        public View layout;
        public TextView tvName,tvPrice,tvUnit,tvMarketName;
        public LinearLayout layoutBG;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivProduct);
            tvName = view.findViewById(R.id.tvTitle);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvMarketName = view.findViewById(R.id.tvMarketName);
            btnDelete = view.findViewById(R.id.btnDelete);
            layoutBG = view.findViewById(R.id.layoutBG);
        }
    }

    public ProductAlertAdapter(List<Product> list, Context context, ProductAlertAdapter.OnSelectItemClickListener listener) {
        products = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public ProductAlertAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_alert, parent, false);
        return new ProductAlertAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductAlertAdapter.ViewHolder holder, final int position) {

        final Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("SAR "+product.getPrice());
        holder.tvMarketName.setText(product.getMarketName());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String image = APIClient.imageUrl + product.getImage();

        Glide.with(context)
                .asBitmap()
                .load(image)
                .placeholder(circularProgressDrawable)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //  ivBanner3.setImageBitmap(resource);

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        final float roundPx = (float) resource.getWidth() * 0.04f;
                        roundedBitmapDrawable.setCornerRadius(roundPx);

                        holder.imageView.setImageDrawable(roundedBitmapDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFavItemClickListener.onItemClick(view, position, product);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavItemClickListener.onDelete(view,position,product);
            }
        });

    }

    public void updateList(List<Product> list) {
        products = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnSelectItemClickListener {
        void onItemClick(View view, int position, Product product);
        void onDelete(View view, int position, Product product);
    }

}


