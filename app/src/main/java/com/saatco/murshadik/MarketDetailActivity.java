package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.saatco.ItemOffsetDecoration;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ItemListAdapter;
import com.saatco.murshadik.adapters.ProductsAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.LabReportResponse;
import com.saatco.murshadik.databinding.ActivityMarketDetailBinding;
import com.saatco.murshadik.fragments.ModalBottomSheet;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Market;
import com.saatco.murshadik.model.Product;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.Util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketDetailActivity extends AppCompatActivity implements ProductsAdapter.OnSelectItemClickListener {

    private LineChart chart;

    RecyclerView recyclerView;
    TextView tvMarketName;
    TextView tvMarketAddress;
    TextView tvMarketTime;
    TextView tvProductName;
    ProgressBar progressBar;
    MaterialSpinner spinnerProduct;
    MaterialSpinner spinnerMonth;
    ImageButton btnFiler;
    ImageButton btnSort;
    ImageView ivMarket;

    ProductsAdapter productsAdapter;

    int marketId, productId;

    Market market = null;

    ArrayList<Product> products = new ArrayList<>();

    ArrayList<Product> productPrices = new ArrayList<>();


    ArrayList<Item> productCategories = new ArrayList<>();


    CityAdapter adapter;

    int filter = 7;

    ActivityMarketDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMarketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        initViews();


        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));

        marketId = getIntent().getIntExtra("ID", 0);

        getMarketDetails();
        getProductTypes();

        //*************************** chat properties *****************************//
        chart = findViewById(R.id.chart1);

        // background color
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners
        chart.setDrawGridBackground(false);

        chart.animateX(1500);

        Legend l = chart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);

        //*************************** search in products *****************************//
        binding.searchInProducts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (products == null || query.length() < 2) return false;
                if (products.isEmpty()) return false;
                ArrayList<Product> tempProduct = new ArrayList<>();
                for (Product p : products){
                    if (p.getName().contains(query))
                        tempProduct.add(p);
                }
                productsAdapter.updateList(tempProduct);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (products == null || newText.length() < 2) return false;
                if (products.isEmpty()) return false;
                ArrayList<Product> tempProduct = new ArrayList<>();
                for (Product p : products){
                    if (p.getName().contains(newText))
                        tempProduct.add(p);
                }
                productsAdapter.updateList(tempProduct);
                return true;
            }
        });

        //*************************** sort products *****************************//
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (products != null) {
                    if (!products.isEmpty())
                        showFilterByPrice();
                }

            }
        });

        //*************************** filter products *****************************//
        btnFiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (products != null) {
                    if (!products.isEmpty())
                        showFilterByProductCategory();
                }
            }
        });
    }

    void initViews() {
        recyclerView = binding.productsRecycleView;
        tvMarketName = binding.tvMarketName;
        tvMarketAddress = binding.tvMarketAddress;
        tvMarketTime = binding.tvOpenTime;
        tvProductName = binding.tvProductName;
        progressBar = binding.progressBar;
        spinnerProduct = binding.spinnerProduct;
        spinnerMonth = binding.spinnerMonth;
        btnFiler = binding.btnFilter;
        btnSort = binding.btnSort;
        ivMarket = binding.ivMarket;
    }

    private void getMarketDetails() {
        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<LabReportResponse> call = apiInterface.getMarketDetail("Bearer " + TokenHelper.getToken(), marketId);
        call.enqueue(new Callback<LabReportResponse>() {
            @Override
            public void onResponse(@NonNull Call<LabReportResponse> call, @NonNull Response<LabReportResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {

                    assert response.body() != null;
                    market = response.body().getData().getMarket();
                    products = response.body().getData().getProducts();
                    productPrices = response.body().getData().getProductPrices();

                    setProductAdapter();
                    setSpinnerMonth();
                    setSpinnerProduct();
                    setMarketDetails();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<LabReportResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d("CYBER", "onFailure: ", t);
            }
        });

    }

    private void getProductTypes() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getProductTypes("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {

                try {
                    productCategories = (ArrayList<Item>) response.body();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {

            }
        });
    }

    private void setProductAdapter() {
        if (products != null) {
            productId = products.get(0).getId();
            getProductPrices(7, products.get(0).getId());
            productsAdapter = new ProductsAdapter(products, this, this);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            recyclerView.setAdapter(productsAdapter);
            recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);
        }
    }

    private void setSpinnerMonth() {

        ArrayList<String> categories = new ArrayList<>();
        categories.add("أسبوع");
        categories.add("شهر");
        categories.add("عام");

        spinnerMonth.setItems(categories);
        spinnerMonth.setHint("اختر الفئة");
        spinnerMonth.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {

            if (position == 0)
                filter = Consts.WEEK_FILTER;
            if (position == 1)
                filter = Consts.MONTH_FILTER;
            if (position == 2)
                filter = Consts.YEAR_FILTER;

            getProductPrices(filter, productId);
        });
    }

    private void setSpinnerProduct() {
        spinnerProduct.setOnClickListener(view -> showProductName());
    }

    private void setMarketDetails() {

        if (market != null) {
            tvMarketName.setText(market.getName());
            tvMarketAddress.setText(market.getAddress());
            String strMarketTime = market.getOpenAt() + "-" + market.getCloseAt();
            tvMarketTime.setText(strMarketTime);


            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            String image = APIClient.imageUrl + market.getMarketImage();

            RequestOptions requestOptions = new RequestOptions()
                    .transform(new CenterCrop(), new RoundedCorners(10));
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(image)
                    .placeholder(R.drawable.logo)
                    .apply(requestOptions)
                    .into(ivMarket);

            binding.tvIsOpen.setText(market.isOpened() ? getResources().getString(R.string.open) : getResources().getString(R.string.close));
            binding.tvIsOpen.setTextColor(market.isOpened() ? getColor(R.color.green) : getColor(R.color.red));



            if (products != null) {
                tvProductName.setText(products.get(0).getName());
                spinnerProduct.setText(products.get(0).getName());
            }
        }
    }

    //*************************** create chart *****************************//
    private void setData(ArrayList<Product> prices) {

        ArrayList<Entry> yEntries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();


        for (int i = 0; i < prices.size(); i++) {
            yEntries.add(new Entry(i, Float.parseFloat(prices.get(i).getPrice())));
            dates.add(prices.get(i).getFormattedDate());
        }

        LineDataSet set1;
        set1 = new LineDataSet(yEntries, "");
        // line thickness and point size
        set1.setLineWidth(1f);
        // draw points as solid circles
        set1.setDrawCircleHole(false);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // text size of values
        set1.setValueTextSize(9f);
        // set the filled area
        set1.setDrawFilled(true);

        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.GREEN);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        set1.setFillAlpha(65);
        set1.setFillColor(getResources().getColor(R.color.colorPrimary, null));
        set1.setColor(getResources().getColor(R.color.colorPrimary, null)); //line color
        set1.setCircleColor(getResources().getColor(R.color.colorPrimary, null));
        set1.setCircleHoleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleRadius(5f);
        set1.setDrawCircleHole(true);
        set1.setDrawValues(false);

        chart.setData(data);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setTextSize(8f);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));
        chart.getXAxis().setGranularity(1f);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    @Override
    public void onItemClick(View view, int position, Product product) {
        productId = products.get(position).getId();
//        tvProductName.setText(products.get(position).getName());
//        getProductPrices(filter, productId);


    }

    @Override
    public void onAlertClick(View view, int position, Product product) {

        if (ProfileHelper.hasAccount(getApplicationContext())) {
            if (ProfileHelper.getAccount(getApplicationContext()).isProfileComplete()) {
                if (ProfileHelper.getAccount(getApplicationContext()).isMarketNotificationEnabled())
                    showPriceAlert(product.getId());
                else
                    DialogUtil.showMarketAlertSettings(this, "", getResources().getString(R.string.on_market_alerts_from_setting));
            } else {
                Util.showErrorToast(getResources().getString(R.string.complete_profile_msg), this);
            }
        }

    }

    private void showPriceAlert(int productId) {

        final Dialog dialog = new Dialog(MarketDetailActivity.this);
        dialog.setContentView(R.layout.dialog_price_increase);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RadioGroup radioGroup = dialog.findViewById(R.id.priceRadio);
        RadioButton radioButton = dialog.findViewById(R.id.radioPrice);

        EditText etPrice = dialog.findViewById(R.id.etPrice);
        Button btnDone = dialog.findViewById(R.id.btnDone);

        final int[] increase = {0};

        radioGroup.setOnCheckedChangeListener((radioGroup1, checkedId) -> {

            if (checkedId == R.id.radioDecrease) {
                increase[0] = 0;
                etPrice.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioIncrease) {
                increase[0] = 1;
                etPrice.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioPrice) {
                etPrice.setVisibility(View.VISIBLE);
            }
        });

        btnDone.setOnClickListener(view -> {

            if (radioButton.isChecked() && etPrice.getText().toString().isEmpty())
                Util.showToast("أدخل السعر", MarketDetailActivity.this);
            else if (radioButton.isChecked() && !etPrice.getText().toString().isEmpty()) {
                increase[0] = Integer.parseInt(etPrice.getText().toString());
                subscribeProduct(productId, increase[0]);
                dialog.dismiss();
            } else {
                subscribeProduct(productId, increase[0]);
                dialog.dismiss();
            }

        });

        dialog.show();


    }

    private void showFilterByPrice() {

        String[] categories = {getResources().getString(R.string.high_to_low), getResources().getString(R.string.low_to_hight)};


        ModalBottomSheet<String> modalBottomSheet = new ModalBottomSheet.Builder<String>()
                .setTitle(getResources().getString(R.string.select_option))
                .setItems(categories)
                .setListener((position, item) -> {
                    if (position == 1) {
                        Collections.sort(products);
                        for (Product products : products) {
                            Log.v("PRODUCT", products.getPrice());
                        }
                    } else {
                        products.sort(Collections.reverseOrder());
                        for (Product products : products) {
                            Log.v("PRODUCT", products.getPrice());
                        }
                    }

                    productsAdapter.updateList(products);
                })
                .build();

        modalBottomSheet.show(getSupportFragmentManager(), "sort_by_price");

    }

    class CompareByPrice implements Comparator<Product> {
        public int compare(Product one, Product two) {
            return Double.compare(one.getConvertedPrice(), two.getConvertedPrice());
        }
    }

    public class MyComparator implements Comparator<Product> {
        public int compare(Product p1, Product p2) {
            return p1.getPrice().compareTo(p2.getPrice());
        }
    }

    private void showFilterByProductCategory() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dialog_product_category, null);

        BottomDialog builder = new BottomDialog.Builder(MarketDetailActivity.this)
                .setTitle(getResources().getString(R.string.filter_by_category))
                .setCustomView(customView)
                .show();

        Button btnDone = customView.findViewById(R.id.btnDone);
        RecyclerView recyclerView = customView.findViewById(R.id.rv_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new CityAdapter(productCategories, getApplicationContext(), (view, position, city) -> {


            city.setSelected(!city.isSelected());

            adapter.notifyDataSetChanged();
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btnDone.setOnClickListener(view -> {

            String productStr = "";
            for (Item item : productCategories) {
                if (item.isSelected())
                    productStr = productStr + item.getName() + ",";
            }

            ArrayList<Product> newList = new ArrayList<>();
            ArrayList<Product> temp = products;

            for (Product product : temp) {
                String[] categories = productStr.split(",");
                for (String category : categories) {
                    if (product.getCategory().equals(category))
                        newList.add(product);
                }
            }

            if (!newList.isEmpty())
                temp = newList;
            else {
                temp = products;
            }

            if (temp != null)
                productsAdapter.updateList(temp);

            builder.dismiss();
        });

    }

    private void showProductName() {

        ArrayList<Product> productArrayList = products;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dialog_market_category, null);

        BottomDialog builder = new BottomDialog.Builder(MarketDetailActivity.this)
                .setCustomView(customView)
                .setTitle(getResources().getString(R.string.products))
                .show();

        SearchView searchView = customView.findViewById(R.id.searchView);


        ListView listView = customView.findViewById(R.id.nameListView);

        ArrayList<String> titles = new ArrayList<>();

        if (productArrayList != null) {
            for (Product product : productArrayList)
                titles.add(product.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, titles);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener((parent, view, position, id) -> {

            // ListView Clicked item index
            String titleMr = (String) listView.getItemAtPosition(position);

            productId = getProductId(titleMr);
            tvProductName.setText(titleMr);
            getProductPrices(filter, productId);
            spinnerProduct.setText(titleMr);

            builder.dismiss();
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(searchView::performClick, 500);

        searchView.setOnClickListener(view -> searchView.setIconified(false));

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextSize(11);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.isEmpty()) {
                    ArrayList<Product> temp = new ArrayList<>();

                    assert productArrayList != null;
                    for (Product p : productArrayList) {
                        if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                            temp.add(p);
                        }
                    }

                    titles.clear();
                    for (Product product : temp)
                        titles.add(product.getName());

                    adapter.notifyDataSetChanged();
                } else {
                    titles.clear();
                    assert productArrayList != null;
                    for (Product product : productArrayList)
                        titles.add(product.getName());

                    adapter.notifyDataSetChanged();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query.length() > 3) {
                    ArrayList<Product> temp = new ArrayList<>();

                    assert productArrayList != null;
                    for (Product p : productArrayList) {
                        if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                            temp.add(p);
                        }
                    }
                    //update recyclerview

                    titles.clear();
                    for (Product product : temp)
                        titles.add(product.getName());

                    adapter.notifyDataSetChanged();
                } else {
                    titles.clear();
                    assert productArrayList != null;
                    for (Product product : productArrayList)
                        titles.add(product.getName());

                    adapter.notifyDataSetChanged();
                }

                return true;
            }
        });


    }

    private void subscribeProduct(int productId, int increase) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.subscribeProduct("Bearer " + TokenHelper.getToken(), productId, marketId, increase);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                try {

                    Util.showSuccessToast(getResources().getString(R.string.success_product_subscribtion), MarketDetailActivity.this);

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {

            }
        });
    }

    //************************ get product prices **********************//
    private void getProductPrices(int filter, int productId) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Product>> call = apiInterface.getProductPriceFor("Bearer " + TokenHelper.getToken(), productId, marketId, filter);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {

                ArrayList<Product> prices = (ArrayList<Product>) response.body();
                if (prices != null) {
                    setData(prices);
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {

            }
        });
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    private int getProductId(String name) {
        int id = 0;
        for (Product product : products) {
            if (product.getName().equals(name))
                return product.getId();
        }
        return id;
    }
}

