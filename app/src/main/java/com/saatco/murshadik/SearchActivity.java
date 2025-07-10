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
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.saatco.murshadik.Helpers.FirebaseObjectHelper;
import com.saatco.murshadik.adapters.SearchAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.CalenderResponse;
import com.saatco.murshadik.databinding.ActivitySearchBinding;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements SearchAdapter.OnSelectItemClickListener {

    SearchView searchView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ImageView btnBack;

    SearchAdapter searchAdapter;
    ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

        progressBar.setVisibility(View.GONE);

        searchView.setOnClickListener(v -> searchView.setIconified(false));

        searchView.performClick();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                progressBar.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(() -> search(query), 1000);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }


        });

        btnBack.setOnClickListener(view -> finish());

    }

    private void initViews() {
        searchView = binding.searchView;
        recyclerView = binding.rvSearch;
        progressBar = binding.progressBar2;
        btnBack = binding.btnBack;
    }

    private void search(String query) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<CalenderResponse> call = apiInterface.search(query, "1", 0);
        call.enqueue(new Callback<CalenderResponse>() {
            @Override
            public void onResponse(@NonNull Call<CalenderResponse> call, @NonNull Response<CalenderResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {
                    assert response.body() != null;
                    getSearchResult(response.body().getItems());
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<CalenderResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getSearchResult(ArrayList<Item> items) {
        searchAdapter = new SearchAdapter(items, getApplicationContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onGroupClick(View view, int position, Item item) {

        Intent intent = new Intent(this, CalendarDetailActivity.class);
        intent.putExtra("CAT_ID", item.getId());
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseObjectHelper.updateLastTerminate(getApplicationContext());
    }
}
