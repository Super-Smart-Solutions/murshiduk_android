package com.saatco.murshadik.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saatco.murshadik.MarketDetailActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.MarketAdapter;
import com.saatco.murshadik.model.Market;

import java.util.ArrayList;


public class MarketsFragment extends Fragment implements MarketAdapter.OnSelectItemClickListener {


    RecyclerView recyclerView;
    MarketAdapter marketAdapter;
    ArrayList<Market> markets = new ArrayList<>();

    public MarketsFragment() {
        // Required empty public constructor
    }


    public static MarketsFragment newInstance(Bundle bundle) {
        MarketsFragment fragment = new MarketsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_markets, container, false);

        Bundle bundle = getArguments();
        markets = (ArrayList<Market>) bundle.getSerializable("MARKETS");

        recyclerView = root.findViewById(R.id.rv_markets);

        setAdapter();

        return root;
    }

    public void setAdapter(){

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        marketAdapter = new MarketAdapter(markets,getContext(),this);
        recyclerView.setAdapter(marketAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onMarketClick(View view, int position, Market market) {

        Intent intent = new Intent(getContext(), MarketDetailActivity.class);
        intent.putExtra("ID",market.getId());
        intent.putExtra("MARKET_NAME",market.getName());
        startActivity(intent);

    }
}