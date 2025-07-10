package com.saatco.murshadik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.MarketsFragmentsAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.MarketResponse;
import com.saatco.murshadik.databinding.ActivityMarketsBinding;
import com.saatco.murshadik.fragments.MarketMapFragment;
import com.saatco.murshadik.fragments.MarketsFragment;
import com.saatco.murshadik.model.Market;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketsActivity extends AppCompatActivity {

    ActivityMarketsBinding binding;

    ArrayList<Market> markets = new ArrayList<>();

    MarketsFragmentsAdapter marketsFragmentsAdapter;
    private boolean isList = false;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MarketsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMarketsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        isList = true;
        getMarkets();


        binding.ibMarketsMap.setOnClickListener(view1 -> {

            binding.viewPager.setCurrentItem(1);
            isList = false;

            binding.ibMarketsList.setVisibility(View.VISIBLE);
            binding.ibMarketsMap.setVisibility(View.GONE);
        });

        binding.ibMarketsList.setOnClickListener(view12 -> {

            binding.viewPager.setCurrentItem(0);
            isList = true;

            binding.ibMarketsList.setVisibility(View.GONE);
            binding.ibMarketsMap.setVisibility(View.VISIBLE);

        });

        binding.btnBack.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }

    void initViewPager(Bundle bundle, int page) {
        marketsFragmentsAdapter = new MarketsFragmentsAdapter(this, bundle);
        binding.viewPager.setAdapter(marketsFragmentsAdapter);
        binding.viewPager.setUserInputEnabled(false);

        binding.viewPager.setCurrentItem(page);
    }


    private void getMarkets() {
        binding.progressBar.getRoot().setVisibility(View.VISIBLE);


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MarketResponse> call = apiInterface.getMarkets("Bearer " + TokenHelper.getToken(), null);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MarketResponse> call, @NonNull Response<MarketResponse> response) {

                binding.progressBar.getRoot().setVisibility(View.GONE);

                try {

                    assert response.body() != null;
                    markets = response.body().getMarkets();

                    if (markets != null) {

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("MARKETS", markets);

                        if (isList) {
                            initViewPager(bundle, 0);
                        } else {
                            initViewPager(bundle, 1);
                        }

                    }


                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarketResponse> call, @NonNull Throwable t) {
                binding.progressBar.getRoot().setVisibility(View.GONE);
            }
        });
    }

}