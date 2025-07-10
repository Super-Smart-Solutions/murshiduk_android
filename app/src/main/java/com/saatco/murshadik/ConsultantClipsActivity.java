package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saatco.murshadik.Helpers.PermissionsHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.ConsultantClipVideoAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityConsultantClipsBinding;
import com.saatco.murshadik.fragments.ModalBottomSheet;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.consultantvideos.CategoryDataOfConsultantVideos;
import com.saatco.murshadik.model.consultantvideos.CategorySubDataOfConsultantVideos;
import com.saatco.murshadik.model.consultantvideos.VideoDataOfConsultantVideos;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.UiUtils;
import com.saatco.murshadik.views.SearchableToolbar;


import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultantClipsActivity extends BaseActivity implements ConsultantClipVideoAdapter.OnSelectItemClickListener {

    boolean is_add_layout_started;


    RecyclerView rv_consultant_clips;
    NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>> consultantVideos;
    ArrayList<VideoDataOfConsultantVideos> filteredConsultantVideos;
    NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>> consultantVideosCategory;
    TextView tv_no_data_received;
    LinearLayout ll_progress_bar;
    SearchableToolbar st_searchable_toolbar;
    ConsultantClipVideoAdapter adapter;
    private ActivityConsultantClipsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultantClipsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });


//        ToolbarHelper.setToolBarTransWithTitle(this, getString(R.string.my_clips), findViewById(R.id.toolbarTrans));
        LanguageUtil.changeLanguage(this);


        assignViews();
        UiUtils.resizeRecyclerViewHeight(this, rv_consultant_clips, st_searchable_toolbar);
//        UiUtils.setupNestedScroll(this, nsv_container, rv_consultant_clips);
        getVideosGalleryFromAPI();
        getVideoCategoriesFromAPI();

        st_searchable_toolbar.innerViews.btnAddNew.setVisibility(View.VISIBLE);
        st_searchable_toolbar.innerViews.btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(ConsultantClipsActivity.this, AddVideoByConsultantsActivity.class);
            intent.putExtra("is_edit", false);
            startActivity(intent);
            is_add_layout_started = true;
        });

        st_searchable_toolbar.innerViews.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });

        st_searchable_toolbar.innerViews.ivBtnBack.setOnClickListener(v -> this.onBackPressed());
        st_searchable_toolbar.innerViews.ivBtnSort.setOnClickListener(v -> showSortByCategoryView());


    }

    private void assignViews() {
        rv_consultant_clips = findViewById(R.id.rv_consultant_clips);
        tv_no_data_received = findViewById(R.id.tv_no_data_received);
        ll_progress_bar = findViewById(R.id.ll_progress_bar);
        st_searchable_toolbar = findViewById(R.id.st_searchable_toolbar);
        rv_consultant_clips.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_consultant_clips.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (is_add_layout_started) {
            getVideosGalleryFromAPI();
            is_add_layout_started = false;
        }

    }


    private void getVideosGalleryFromAPI() {

        ll_progress_bar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>>> call = apiInterface.getConsultantVideosGallery("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>>> call, @NonNull Response<NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>>> response) {
                consultantVideos = response.body();
                if (consultantVideos != null) {
                    if (consultantVideos.getData().isEmpty()) {
                        tv_no_data_received.setVisibility(View.VISIBLE);

                    } else {
                        setOrUpdateRecyclerViewAdapter(consultantVideos.getData());
                        tv_no_data_received.setVisibility(View.GONE);
                    }
                } else {
                    tv_no_data_received.setText(getString(com.vanillaplacepicker.R.string.something_went_worng));
                    tv_no_data_received.setVisibility(View.VISIBLE);
                }

                ll_progress_bar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>>> call, @NonNull Throwable t) {
                ToastUtils.longToast(getString(R.string.connection_lost));
                Log.e("error in get consultant videos request: ", t.getMessage());
                ll_progress_bar.setVisibility(View.GONE);
            }
        });
    }


    private void getVideoCategoriesFromAPI() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call = apiInterface.getVideoCategories("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call, @NonNull Response<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> response) {
                consultantVideosCategory = response.body();
                //setting categories array for spinners adapters

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call, @NonNull Throwable t) {
                ToastUtils.longToast(getString(R.string.connection_lost));
            }
        });
    }

    void setOrUpdateRecyclerViewAdapter(ArrayList<VideoDataOfConsultantVideos> videoData) {
        videoData.sort(Comparator.comparing(VideoDataOfConsultantVideos::getDate).reversed());
        if (adapter == null) {
            adapter = new ConsultantClipVideoAdapter(videoData, getApplicationContext(), this);
            rv_consultant_clips.setAdapter(adapter);
            return;
        }
        adapter.filterList(videoData);

    }

    void search(String search_text) {
        if (search_text == null || consultantVideos == null) return;

        ArrayList<VideoDataOfConsultantVideos> video_data = filteredConsultantVideos;
        if (video_data == null || video_data.isEmpty()) video_data = consultantVideos.getData();

        ArrayList<VideoDataOfConsultantVideos> temp_video_data = new ArrayList<>();
        for (VideoDataOfConsultantVideos v_d : video_data) {
            if (v_d.getTitle().contains(search_text) || v_d.getDescription().contains(search_text) || v_d.getConsultantName().contains(search_text)) {
                temp_video_data.add(v_d);
            }
        }
        setOrUpdateRecyclerViewAdapter(temp_video_data);
    }


    void sortByCategory(String cat_name) {
        if (cat_name == null || consultantVideos == null) return;

        if (cat_name.contains(Consts.SHOW_ALL)) {
            setOrUpdateRecyclerViewAdapter(consultantVideos.getData());
            return;
        }

        ArrayList<VideoDataOfConsultantVideos> temp_video_data = new ArrayList<>();
        for (VideoDataOfConsultantVideos v_d : consultantVideos.getData()) {
            if (v_d.getDepartment().contains(cat_name)) {
                temp_video_data.add(v_d);
            }
        }
        filteredConsultantVideos = temp_video_data;
        setOrUpdateRecyclerViewAdapter(temp_video_data);
    }

    private void showSortByCategoryView() {

        if (consultantVideosCategory == null)
            return;

        ArrayList<String> cat_names = new ArrayList<>();
        cat_names.add(Consts.SHOW_ALL);
        for (CategoryDataOfConsultantVideos cat_data :
                consultantVideosCategory.getData()) {
            for (CategorySubDataOfConsultantVideos cat_sub_data :
                    cat_data.getSubData()) {

                cat_names.add(cat_sub_data.getName());
            }
        }

        ModalBottomSheet<String> modalBottomSheet = new ModalBottomSheet.Builder<String>()
                .setTitle(getResources().getString(R.string.select_option))
                .setItems(cat_names)
                .setListener((position, item) -> {
                    sortByCategory(item);
                })
                .build();

        modalBottomSheet.show(getSupportFragmentManager(), "sort_by_category");


    }

    @Override
    public void onClipCardItemClick(View view, int position, VideoDataOfConsultantVideos item) {
        Intent intent = new Intent(this, ConsultantVideoClipsViewerActivity.class);
        intent.putExtra("data", item);
        startActivity(intent);
    }

    @Override
    public void onEditBtnItemClick(View view, int position, VideoDataOfConsultantVideos item) {
        Intent intent = new Intent(ConsultantClipsActivity.this, AddVideoByConsultantsActivity.class);
        intent.putExtra("data", item);
        intent.putExtra("is_edit", true);
        startActivity(intent);
        is_add_layout_started = true;
    }
}