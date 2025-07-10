package com.saatco.murshadik.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.MarketResponse;
import com.saatco.murshadik.databinding.FragmentMarketMainBinding;
import com.saatco.murshadik.model.Market;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MarketMainFragment extends Fragment {

    FrameLayout container;

    ProgressBar progressBar;
    ArrayList<Market> markets = new ArrayList<>();

    private boolean isList = false;

    FragmentMarketMainBinding binding;

    public static MarketMainFragment newInstance() {
        MarketMainFragment fragment = new MarketMainFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        getMarkets();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMarketMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initViews();

        Bundle bundle = new Bundle();
        bundle.putSerializable("MARKETS", markets);

        //*************************** market list selected by default *****************************//
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, MarketsFragment.newInstance(bundle));
        fragmentTransaction.commitAllowingStateLoss();

        isList = true;
        getMarkets();


        binding.ibMarketsMap.setOnClickListener(view1 -> {

            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("MARKETS", markets);

            FragmentTransaction fragmentTransaction1 = getParentFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.fragment_container, MarketMapFragment.newInstance(bundle1));
            fragmentTransaction1.commit();



            isList = false;
        });

        binding.ibMarketsList.setOnClickListener(view12 -> {


            Bundle bundle12 = new Bundle();
            bundle12.putSerializable("MARKETS", markets);

            FragmentTransaction fragmentTransaction12 = getParentFragmentManager().beginTransaction();
            fragmentTransaction12.replace(R.id.fragment_container, MarketsFragment.newInstance(bundle12));
            fragmentTransaction12.commit();



            isList = true;

        });

        return view;
    }

    private void initViews() {
        container = binding.fragmentContainer;
        progressBar = binding.progressBar.getRoot();
    }

    private void getMarkets() {
        progressBar.setVisibility(View.VISIBLE);


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MarketResponse> call = apiInterface.getMarkets("Bearer " + TokenHelper.getToken(), null);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MarketResponse> call, @NonNull Response<MarketResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {

                    assert response.body() != null;
                    markets = response.body().getMarkets();

                    if (markets != null) {

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("MARKETS", markets);

                        if (isList) {
                            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                            fragmentTransaction.add(R.id.fragment_container, MarketsFragment.newInstance(bundle));
                            fragmentTransaction.commit();
                        } else {
                            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, MarketMapFragment.newInstance(bundle));
                            fragmentTransaction.commit();
                        }

                    }


                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarketResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}