package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.ConsultantVideoClipsListAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.fragments.ModalBottomSheet;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.consultantvideos.CategorySubDataOfConsultantVideos;
import com.saatco.murshadik.model.consultantvideos.VideoDataOfConsultantVideos;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.UiUtils;
import com.saatco.murshadik.views.SearchableToolbar;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultantsVideoListActivity extends BaseActivity implements ConsultantVideoClipsListAdapter.OnSelectItemClickListener {

    RecyclerView rv_consultant_clips;
    NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>> consultantVideos;
    ArrayList<VideoDataOfConsultantVideos> filteredConsultantVideos;
    ArrayList<CategorySubDataOfConsultantVideos> departments;
    LinearLayout ll_progress_bar;
    TextView tv_no_data_received;
    ConsultantVideoClipsListAdapter adapter;
    SearchableToolbar st_searchable_toolbar;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultants_video_list);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        assignViews();
        UiUtils.resizeRecyclerViewHeight(this, rv_consultant_clips, st_searchable_toolbar);

        departments = (ArrayList<CategorySubDataOfConsultantVideos>) getIntent().getSerializableExtra(ClipsIndicativeCategoryActivity.CATEGORY_SUB_DATA);
        setViewsListeners();
        getAllVideosFromAPI(getIntent().getStringExtra(ClipsIndicativeCategoryActivity.CATEGORY_ID));
    }

    private void assignViews() {
        ll_progress_bar = findViewById(R.id.ll_progress_bar);
        tv_no_data_received = findViewById(R.id.tv_no_data_received);
        st_searchable_toolbar = findViewById(R.id.st_searchable_toolbar);
        rv_consultant_clips = findViewById(R.id.rv_consultant_clips);
        rv_consultant_clips.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_consultant_clips.setItemAnimator(new DefaultItemAnimator());
    }

    private void setViewsListeners() {
        st_searchable_toolbar.innerViews.ivBtnBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        st_searchable_toolbar.innerViews.tvTitle.setText(getIntent().getStringExtra(ClipsIndicativeCategoryActivity.CATEGORY));

        st_searchable_toolbar.innerViews.ivBtnSort.setOnClickListener(v -> showSortByCategoryView());

        st_searchable_toolbar.innerViews.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    }



    void setOrUpdateRecyclerViewAdapter(ArrayList<VideoDataOfConsultantVideos> videoData) {
        videoData.sort(Comparator.comparing(VideoDataOfConsultantVideos::getDate).reversed());
        if (adapter == null) {
            adapter = new ConsultantVideoClipsListAdapter(videoData, getApplicationContext(), this);
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

        if (cat_name.contains(Consts.SHOW_ALL)){
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

        if (departments == null) return;

        ArrayList<String> cat_departments_name = new ArrayList<>();
        cat_departments_name.add(Consts.SHOW_ALL);
        cat_departments_name
                .addAll(departments.stream()
                .map(CategorySubDataOfConsultantVideos::getName)
                .collect(Collectors.toCollection(ArrayList::new)));



        ModalBottomSheet<String> modalBottomSheet = new ModalBottomSheet.Builder<String>()
                .setTitle(getResources().getString(R.string.select_option))
                .setItems(cat_departments_name)
                .setListener((position, item) -> {
                    sortByCategory(item);
                })
                .build();

        modalBottomSheet.show(getSupportFragmentManager(), "sort_by_category");
    }

    private void getAllVideosFromAPI(String category_id) {

        ll_progress_bar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>>> call = apiInterface.getAllConsultantVideos("Bearer " + TokenHelper.getToken(), category_id);
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

                if (t.getMessage() != null && t.getMessage().contains("Unable to resolve host"))
                    ToastUtils.longToast(getString(R.string.connection_lost));
                else
                    tv_no_data_received.setVisibility(View.VISIBLE);

                ll_progress_bar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClipCardItemClick(View view, int position, VideoDataOfConsultantVideos item) {
        Intent intent = new Intent(this, ConsultantVideoClipsViewerActivity.class);
        intent.putExtra("data", item);
        startActivity(intent);
    }

}