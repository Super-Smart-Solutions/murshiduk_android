package com.saatco.murshadik;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.saatco.murshadik.Helpers.CountryHelper;
import com.saatco.murshadik.adapters.CountryAdapter;
import com.saatco.murshadik.model.City;

import java.util.ArrayList;
import java.util.List;

public class SelectCityActivity extends BaseActivity implements CountryAdapter.OnSelectItemClickListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private final List<String> citiesEng = new ArrayList<>();
    private final List<String> citiesArb = new ArrayList<>();

    private ImageView backBtn;

    private SearchView search;
    public static final int RESULT_SELECT_CITY_OK = 201;
    ArrayList<City> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        search = findViewById(R.id.search);

        recyclerView = findViewById(R.id.city_recycler_view);
        makeToolbar("بلد");

        cities = CountryHelper.getCountries();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CountryAdapter(cities,getApplicationContext(),this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        search.setOnClickListener(v -> search.setIconified(false));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        ImageView closeBtn = search.findViewById(com.google.maps.android.R.id.search_close_btn);
        closeBtn.setEnabled(false);
        closeBtn.setImageDrawable(null);

    }



    void filter(String text){
        ArrayList<City> temp = new ArrayList();

            for(City d: cities){
                if (d.getNameAr().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }

        ((CountryAdapter)recyclerView.getAdapter()).updateList(temp);
    }



    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    public void makeToolbar(String title){

        backBtn = findViewById(R.id.btn_back);

        if(LanguageUtil.getLanguage(getApplicationContext()).equals("ar"))
            backBtn.setImageResource(R.drawable.arrow_right);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onCountrySelect(View view, int position, City city) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("country",city.getNameAr());
        bundle.putString("country_code",city.getCode());
        bundle.putString("country_dial",city.getDialCode());
        intent.putExtras(bundle);
        setResult(RESULT_SELECT_CITY_OK, intent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
