package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saatco.ItemOffsetDecoration;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.CalenderAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.CalenderResponse;
import com.saatco.murshadik.databinding.ActivityCalenderBinding;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.utils.Consts;

import java.util.ArrayList;

public class CalenderActivity extends BaseActivity implements CalenderAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvTitle;
    LinearLayout calOne;
    LinearLayout calTwo;
    private int id;

    ActivityCalenderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalenderBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();

        id = getIntent().getIntExtra("SUB_CAT_ID", 0);

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbar));
        getCalender();

        tvTitle.setText(getIntent().getStringExtra("TITLE"));

        calOne.setOnClickListener(view -> {

            Intent browserIntent = new Intent(CalenderActivity.this, PDFViewerActivity.class);
            browserIntent.putExtra("file_uri", Consts.CALENDER_ONE_URL);
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserIntent);

        });

        calTwo.setOnClickListener(view -> {

            Intent browserIntent = new Intent(CalenderActivity.this, PDFViewerActivity.class);
            browserIntent.putExtra("file_uri", Consts.CALENDER_TWO_URL);
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserIntent);

        });

    }

    private void initViews() {
        recyclerView = binding.rvCalender;
        progressBar = binding.progressBar.getRoot();
        tvTitle = binding.tvTitle;
        calOne = binding.calOne;
        calTwo = binding.calTwo;
    }

    private void getCalender() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<CalenderResponse> call = apiInterface.getSubCategory(id);
        call.enqueue(new Callback<CalenderResponse>() {
            @Override
            public void onResponse(@NonNull Call<CalenderResponse> call, @NonNull Response<CalenderResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {
                    assert response.body() != null;
                    setData(response.body().getItems());
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<CalenderResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setData(ArrayList<Item> response) {

        CalenderAdapter adapter = new CalenderAdapter(response, getApplicationContext(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onCalenderItemClick(View view, int position, Item item) {

        Intent intent = new Intent(this, ItemListActivity.class);
        intent.putExtra("CAT_ID", item.getId());
        intent.putExtra("TITLE", item.getNameAr());
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
}
