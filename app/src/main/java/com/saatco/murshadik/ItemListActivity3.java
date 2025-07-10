package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ItemListAdapter;
import com.saatco.murshadik.adapters.ItemListAdapterTwo;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.CalenderResponse;
import com.saatco.murshadik.databinding.ActivityItemList3Binding;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity3 extends BaseActivity implements ItemListAdapter.OnSelectItemClickListener, ItemListAdapterTwo.OnSelectItemClickListener {

    RecyclerView recyclerViewSection;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout btnCategory;

    ImageView imageView;
    TextView tvTitle;

    private int id;
    private List<Item> items = new ArrayList<>();
    private List<Item> itemsSections = new ArrayList<>();


    ActivityItemList3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemList3Binding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        initViews();
        id = getIntent().getIntExtra("CAT_ID", 0);

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbar));

        getCategory();
        getSubCategory();

        tvTitle.setText(getIntent().getStringExtra("TITLE"));

        btnCategory.setOnClickListener(view -> {

            int id = 0;
            String name = "القواعد الارشادية";

            if (itemsSections != null) {
                if (itemsSections.size() > 0) {
                    id = itemsSections.get(0).getId();
                    name = itemsSections.get(0).getNameAr();
                }
            }

            Intent intent = new Intent(ItemListActivity3.this, ItemListActivity.class);
            intent.putExtra("TITLE", name);
            intent.putExtra("CAT_ID", id);
            startActivity(intent);


        });

    }

    private void initViews() {
        recyclerViewSection = binding.rvItemSection;
        recyclerView = binding.rvItems;
        progressBar = binding.progressBar.getRoot();
        btnCategory = binding.btnCategory;
        imageView = binding.ivIcon;
        tvTitle = binding.title;
    }

    private void getCategory() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getCategory(id);
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {

                progressBar.setVisibility(View.GONE);

                try {
                    assert response.body() != null;
                    if (response.body().size() > 0)
                        setData(response);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getSubCategory() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<CalenderResponse> call = apiInterface.getSubCategory(id);
        call.enqueue(new Callback<CalenderResponse>() {
            @Override
            public void onResponse(@NonNull Call<CalenderResponse> call, @NonNull Response<CalenderResponse> response) {

                try {
                    assert response.body() != null;
                    if (response.body().getItems().size() > 0) {
                        itemsSections = items;
                        setDataSection(response.body().getItems());

                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(APIClient.imageUrl + itemsSections.get(0).getBannerAr())
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                        final float roundPx = (float) resource.getWidth() * 0.04f;
                                        roundedBitmapDrawable.setCornerRadius(roundPx);
                                        imageView.setImageDrawable(roundedBitmapDrawable);
                                        return false;
                                    }
                                })
                                .into(imageView);
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<CalenderResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void setData(Response<List<Item>> response) {

        items = response.body();
        ItemListAdapter adapter = new ItemListAdapter(items, getApplicationContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }

    private void setDataSection(ArrayList<Item> items) {

        itemsSections = items;
        ItemListAdapterTwo adapter = new ItemListAdapterTwo(items, getApplicationContext(), this);
        recyclerViewSection.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewSection.setAdapter(adapter);
        recyclerViewSection.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSection.setHasFixedSize(true);
    }

    @Override
    public void onItemClick(View view, int position, Item item) {

        Intent intent = new Intent(this, CalendarDetailActivity.class);
        intent.putExtra("CAT_ID", item.getId());
        startActivity(intent);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onItem2Click(View view, int position, Item item) {

        Intent intent = new Intent(this, ItemListActivity.class);
        intent.putExtra("TITLE", item.getNameAr());
        intent.putExtra("CAT_ID", item.getId());
        startActivity(intent);

    }
}
