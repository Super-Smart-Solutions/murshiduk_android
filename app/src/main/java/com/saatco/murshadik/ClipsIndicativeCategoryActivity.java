package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ClipsIndicativeCategoryAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.consultantvideos.CategoryDataOfConsultantVideos;
import com.saatco.murshadik.utils.ToastUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClipsIndicativeCategoryActivity extends BaseActivity implements ClipsIndicativeCategoryAdapter.OnSelectItemClickListener {

    static final String CATEGORY = "category";
    static final String CATEGORY_ID = "category_id";
    static final String CATEGORY_SUB_DATA = "category_sub_data";

    RecyclerView lv_categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clips_indicative_catiegory);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ToolbarHelper.setToolBarTransWithTitle(this,getString(R.string.areas_indicative),findViewById(R.id.toolbarTrans));


        lv_categories = findViewById(R.id.lv_categories);
        lv_categories.setItemAnimator(new DefaultItemAnimator());

        getVideoCategoriesFromAPI();

    }

    void setCategoriesRvAdapter(ArrayList<CategoryDataOfConsultantVideos> categoryDataArrayList){

        ClipsIndicativeCategoryAdapter categoryAdapter = new ClipsIndicativeCategoryAdapter(categoryDataArrayList, this, this) ;
        lv_categories.setAdapter(categoryAdapter);


    }

    private void getVideoCategoriesFromAPI() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call = apiInterface.getVideoCategories("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call, Response<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> response) {
                if (response.body() == null) return;
                setCategoriesRvAdapter(response.body().getData());
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call, @NonNull Throwable t) {
                ToastUtils.longToast(getString(R.string.connection_lost));
            }
        });
    }

    @Override
    public void onCategoryClipsCardItemClick(View view, int position, CategoryDataOfConsultantVideos item) {
        Intent intent = new Intent(ClipsIndicativeCategoryActivity.this, ConsultantsVideoListActivity.class);
        intent.putExtra(CATEGORY, item.getName());
        intent.putExtra(CATEGORY_ID, item.getId());
        intent.putExtra(CATEGORY_SUB_DATA, item.getSubData());
        startActivity(intent);

    }
}