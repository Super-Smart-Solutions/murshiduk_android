package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saatco.murshadik.Helpers.FirebaseObjectHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ItemListAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityItemListBinding;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemListActivity extends BaseActivity implements ItemListAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvTitle;
    SearchView searchView;
    FrameLayout searchLayout;

    private int id;
    private List<Item> items = new ArrayList<>();

    ActivityItemListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemListBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        initViews();
        id = getIntent().getIntExtra("CAT_ID", 0);

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbar));

        getCategory();

        tvTitle.setText(getIntent().getStringExtra("TITLE"));

        if (getIntent().getStringExtra("TITLE").equals("المكتبة الرقمية"))
            searchLayout.setVisibility(View.VISIBLE);
        else
            searchLayout.setVisibility(View.GONE);


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filter(newText);
                return false;
            }


        });
    }

    private void initViews() {
        recyclerView = binding.rvItems;
        progressBar = binding.progressBar.getRoot();
        tvTitle = binding.title;
        searchView = binding.searchView;
        searchLayout = binding.searchLayout;
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

    private void setData(Response<List<Item>> response) {

        items = response.body();
        ItemListAdapter adapter = new ItemListAdapter(items, getApplicationContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
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

        FirebaseObjectHelper.updateLastTerminate(getApplicationContext());
    }

    void filter(String text) {
        ArrayList<Item> temp = new ArrayList<>();

        for (Item d : items) {
            if (d.getTitle_ar().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        ((ItemListAdapter) Objects.requireNonNull(recyclerView.getAdapter())).updateList(temp);
    }


}
