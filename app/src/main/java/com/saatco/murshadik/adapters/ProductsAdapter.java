package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.Helpers.TokenHelper;
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

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private ArrayList<ComparedProduct> productPricesInMarkets;
    private final ProductsAdapter.OnSelectItemClickListener onFavItemClickListener;

    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView, ivAlert;
        public View layout;
        public TextView tvName, tvPrice, tvUnit, tvCategory, tv_market_name;
        public CardView layoutBG;
        public RecyclerView rv_products_in_other_markets;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivProduct);
            ivAlert = view.findViewById(R.id.ivProductAlert);
            tvName = view.findViewById(R.id.tvName);
            tvUnit = view.findViewById(R.id.tvUnit);
            tv_market_name = view.findViewById(R.id.tv_market_name);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvCategory = view.findViewById(R.id.tvCategory);
            layoutBG = view.findViewById(R.id.card_view);
            rv_products_in_other_markets = view.findViewById(R.id.rv_products_in_other_markets);
        }
    }

    public ProductsAdapter(ArrayList<Product> list, Context context, ProductsAdapter.OnSelectItemClickListener listener) {
        products = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductsAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ProductsAdapter.ViewHolder holder, final int position) {

        final Product product = products.get(position);

        String unitValue = product.getUnitValue() == null ? "" : product.getUnitValue();

        holder.tvName.setText(product.getName());
        holder.tvUnit.setText(unitValue + " " + product.getUnit());
        holder.tvPrice.setText("SAR " + product.getPrice());
        holder.tvCategory.setText(product.getProductOrigin() == null ? "" : product.getProductOrigin());

        if (holder.tv_market_name != null) {
            holder.tv_market_name.setText(product.getMarketName());
        }

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        String image = APIClient.imageUrl + product.getImage();

        Glide.with(context)
                .asBitmap()
                .load(image)
                .placeholder(circularProgressDrawable)
                .into(holder.imageView);

        holder.layoutBG.setOnClickListener(view -> {
            if (onFavItemClickListener == null) return;
//            onFavItemClickListener.onItemClick(view, position, product);

            if (holder.rv_products_in_other_markets.getVisibility() == View.GONE) {
                getProductPricesInOtherMarkets(product.getId(), holder.rv_products_in_other_markets);
                holder.rv_products_in_other_markets.setVisibility(View.VISIBLE);
            } else {
                holder.rv_products_in_other_markets.setVisibility(View.GONE);
            }
        });

        holder.ivAlert.setOnClickListener(view -> onFavItemClickListener.onAlertClick(view, position, product));
    }

    public void updateList(ArrayList<Product> list) {
        products = list;
        notifyDataSetChanged();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnSelectItemClickListener {
        void onItemClick(View view, int position, Product product);

        void onAlertClick(View view, int position, Product product);
    }


    //************************ get product prices **********************//
    private void getProductPricesInOtherMarkets(int productId, RecyclerView recyclerView) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<ComparedProduct>>> call = apiInterface.getProductPricesInOtherMarkets("Bearer " + TokenHelper.getToken(), productId);
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<ComparedProduct>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<ComparedProduct>>> call, @NonNull Response<NewAPIsResponse<ArrayList<ComparedProduct>>> response) {
                if (response.body() == null || response.body().getData() == null) return;
                productPricesInMarkets = response.body().getData();
                NestedProductsAdapter adapter = new NestedProductsAdapter(productPricesInMarkets, context);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                recyclerView.setAdapter(adapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<ComparedProduct>>> call, @NonNull Throwable t) {

            }
        });
    }

    private void triggerProductsCollected() {

    }
}

