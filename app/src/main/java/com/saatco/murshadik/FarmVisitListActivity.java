package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.FarmVisitRequestAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityFarmVisitListBinding;
import com.saatco.murshadik.model.FarmVisit.FarmVisit;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmVisitListActivity extends AppCompatActivity implements FarmVisitRequestAdapter.OnClickListener {

    private ActivityFarmVisitListBinding binding;
    FarmVisitRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFarmVisitListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        binding.fabAddNewFarmVisitRequest.setOnClickListener(v -> {
            startActivity(new Intent(FarmVisitListActivity.this, FarmVisitRequestActivity.class));
        });

        ToolbarHelper.setToolBarTransWithTitle(this, getString(R.string.requests), findViewById(R.id.toolbarTrans));
        LanguageUtil.changeLanguage(this);


    }

    /**
     * {@inheritDoc}
     * <p>
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getFarmVisitRequests();
    }

    void initFarmRequestsList(ArrayList<FarmVisit> farmVisits) {
        if (farmVisits == null || farmVisits.isEmpty()) {
            Util.showToast(getString(R.string.no_data), this);
            return;
        }
        adapter = new FarmVisitRequestAdapter(farmVisits, this, this);
        binding.rvFarmVisitRequests.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFarmVisitRequests.setAdapter(adapter);
        binding.rvFarmVisitRequests.setItemAnimator(new DefaultItemAnimator());
    }

    private void getFarmVisitRequests() {

        binding.llProgressBar.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<FarmVisit>>> call = apiInterface.getFarmVisitRequests("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<FarmVisit>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<FarmVisit>>> call, @NonNull Response<NewAPIsResponse<ArrayList<FarmVisit>>> response) {

                try {
                    if (response.body() != null) {
                        if (adapter == null)
                            initFarmRequestsList(response.body().getData());
                        else {
                            adapter.update(response.body().getData());
                        }
                    }

                } catch (Exception ignored) {
                }

                binding.llProgressBar.getRoot().setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<FarmVisit>>> call, @NonNull Throwable t) {
                binding.llProgressBar.getRoot().setVisibility(View.GONE);

            }
        });

    }

    @Override
    public void onClick(View view, int position, FarmVisit item) {
        Intent intent = new Intent(FarmVisitListActivity.this, FarmVisitDetailsActivity.class);
        intent.putExtra(Consts.FARM_DATA_EXTRA, item);
        startActivity(intent);
    }
}