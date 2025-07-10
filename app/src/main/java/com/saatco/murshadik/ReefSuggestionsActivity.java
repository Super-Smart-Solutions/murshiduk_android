package com.saatco.murshadik;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ReefComponentsAdapter;
import com.saatco.murshadik.adapters.ReefSuggestionsAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityReefSuggestionsBinding;
import com.saatco.murshadik.model.ReefComponents.ReefComponent;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReefSuggestionsActivity extends BaseActivity {

    ActivityReefSuggestionsBinding binding;

    ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReefSuggestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("name");

        ToolbarHelper.setToolBarTransWithTitle(this, "", binding.appBarTransparent.toolbarTrans);
        binding.appBarTransparent.toolbarTrans.setBackgroundColor(getColor(R.color.PrimaryColorReef));
        binding.headerView.setTitle(getString(R.string.reef_component, name));

        // change status bar color
        getWindow().setStatusBarColor(getColor(R.color.PrimaryColorReef));




    }

    @Override
    protected void onResume() {
        super.onResume();

        int id = getIntent().getIntExtra("id", 0);
        getReefSuggestions(id);

    }

    void initRecyclerView(List<ReefComponent> reefComponents){
        if (reefComponents.isEmpty()) return;

        ReefSuggestionsAdapter adapter = new ReefSuggestionsAdapter(this, reefComponents);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(adapter);
    }

    void getReefSuggestions(int id){
        // Call API to get reef components
        binding.progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<ReefComponent>>> call = apiInterface.getReefComponentSuggestionsById(TokenHelper.getBearerToken(), id);

        call.enqueue(new Callback<NewAPIsResponse<ArrayList<ReefComponent>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<ReefComponent>>> call, @NonNull Response<NewAPIsResponse<ArrayList<ReefComponent>>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    initRecyclerView(response.body().getData());
                } else {
                    ToastUtils.shortToast("Failed to get reef components");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<ReefComponent>>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                ToastUtils.shortToast("Failed to get reef components");
            }
        });

    }
}
