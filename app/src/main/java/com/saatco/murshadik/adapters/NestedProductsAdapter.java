package com.saatco.murshadik.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.MarketDetailActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.model.ComparedProduct;
import com.saatco.murshadik.model.Product;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NestedProductsAdapter extends RecyclerView.Adapter<NestedProductsAdapter.ViewHolder> {

    private ArrayList<ComparedProduct> products;

    private final int numOfProductsCompareCollected = 0;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPrice, tvUnit, tvCategory, tv_market_name;
        public CardView layoutBG;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvUnit = view.findViewById(R.id.tvUnit);
            tv_market_name = view.findViewById(R.id.tv_market_name);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvCategory = view.findViewById(R.id.tvCategory);
            layoutBG = view.findViewById(R.id.card_view);
        }
    }

    public NestedProductsAdapter(ArrayList<ComparedProduct> list, Context context) {
        products = list;
        this.context = context;
    }

    @Override
    public NestedProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nested_product, parent, false);
        return new NestedProductsAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final NestedProductsAdapter.ViewHolder holder, final int position) {

        final ComparedProduct product = products.get(position);

        String unitValue = product.getUnitValue() == null ? "" : product.getUnitValue();

        holder.tvName.setText(product.getProductName());
        holder.tvUnit.setText(unitValue + " " + product.getUnit());
        holder.tvPrice.setText("SAR " + product.getPrice());

        if (holder.tv_market_name != null ){
            holder.tv_market_name.setText(product.getMarketName());
        }

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        holder.layoutBG.setOnClickListener(v -> {
            Intent intent = new Intent(context, MarketDetailActivity.class);
            intent.putExtra("ID",product.getMarketId());
            intent.putExtra("MARKET_NAME",product.getMarketName());
            context.startActivity(intent);
        });

    }

    public void updateList(ArrayList<ComparedProduct> list) {
        products = list;
        notifyDataSetChanged();
    }

    public ArrayList<ComparedProduct> getProducts() {
        return products;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }



}

