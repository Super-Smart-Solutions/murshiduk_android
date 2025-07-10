package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ProductAlertAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.databinding.ActivitySubscribeProductBinding;
import com.saatco.murshadik.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscribeProductActivity extends AppCompatActivity implements ProductAlertAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;

    ProductAlertAdapter productAlertAdapter;
    ArrayList<Product> products = new ArrayList<>();

    ActivitySubscribeProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscribeProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));

        getSubscribedProducts();

    }

    private void initViews() {
        recyclerView = binding.rvProducts;
        progressBar = binding.progressBar.getRoot();
    }

    private void getSubscribedProducts() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Product>> call = apiInterface.getSubscribedProducts("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {

                progressBar.setVisibility(View.GONE);
                if (response.body() == null) return;
                try {

                    products = (ArrayList<Product>) response.body();
                    setAdapter();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void setAdapter() {

        productAlertAdapter = new ProductAlertAdapter(products, getApplicationContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(productAlertAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void removeProduct(int productId, int marketId) {
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.removeSubscribeProduct("Bearer " + TokenHelper.getToken(), productId, marketId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                try {
                    progressBar.setVisibility(View.GONE);

                    getSubscribedProducts();

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position, Product product) {

        Intent intent = new Intent(getApplicationContext(), MarketDetailActivity.class);
        intent.putExtra("ID", product.getMarketId());
        startActivity(intent);
    }

    @Override
    public void onDelete(View view, int position, Product product) {

        removeProduct(product.getId(), product.getMarketId());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }
}