package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ItemListAdapter;
import com.saatco.murshadik.adapters.ItemListAdapterTwo;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.CalenderResponse;
import com.saatco.murshadik.databinding.ActivityItemList2Binding;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

public class ItemListActivity2 extends BaseActivity implements ItemListAdapterTwo.OnSelectItemClickListener, ItemListAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;

    RecyclerView recyclerViewNews;


    ProgressBar progressBar;
    TextView tvTitle;

    ImageButton search;


    private int id;

    ActivityItemList2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemList2Binding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());


        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();

        id = getIntent().getIntExtra("CAT_ID", 0);

        tvTitle.setText(getIntent().getStringExtra("TITLE"));

        ToolbarHelper.setToolBar(this, "", binding.appBar.getRoot());

        getCategory();


        search.setOnClickListener(view -> {

            Intent intent = new Intent(ItemListActivity2.this, SearchActivity.class);
            startActivity(intent);
        });

    }

    private void initViews() {
        recyclerView = binding.rvItems;
        recyclerViewNews = binding.rvNews;
//        nestedScrollView_layout = binding.nsvScrollLayout;
        progressBar = binding.progressBar.getRoot();
        tvTitle = binding.title;
        search = binding.search;
    }


    private void getCategory() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<CalenderResponse> call = apiInterface.getSubCategory(id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CalenderResponse> call, @NonNull Response<CalenderResponse> response) {
                progressBar.setVisibility(View.GONE);

                try {
                    assert response.body() != null;
                    if (!response.body().getItems().isEmpty())
                        setData(response.body().getItems());

                    if (!response.body().getNews().isEmpty()) {
                        setDataNews(response.body().getNews());
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<CalenderResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    private void setData(ArrayList<Item> items) {

        ItemListAdapterTwo adapter = new ItemListAdapterTwo(items, getApplicationContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }

    private void setDataNews(ArrayList<Item> items) {
        ItemListAdapter adapter_news = new ItemListAdapter(items, getApplicationContext(), this);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewNews.setAdapter(adapter_news);
        recyclerViewNews.setItemAnimator(new DefaultItemAnimator());
        recyclerViewNews.setHasFixedSize(true);
    }

    @Override
    public void onItem2Click(View view, int position, Item item) {

        Intent intent = new Intent(ItemListActivity2.this, ItemListActivity2.class);
        intent.putExtra("TITLE", item.getNameAr());
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
    public void onItemClick(View view, int position, Item item) {

        Intent intent = new Intent(ItemListActivity2.this, CalendarDetailActivity.class);
        intent.putExtra("CAT_ID", item.getId());
        startActivity(intent);
    }
}
