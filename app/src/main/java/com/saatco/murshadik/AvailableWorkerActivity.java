package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.Helpers.DataHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.WorkerHelper;
import com.saatco.murshadik.adapters.WorkersAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityAvailableWorkerBinding;
import com.saatco.murshadik.databinding.CustomViewSearchableToolbarBinding;
import com.saatco.murshadik.dialogs.FilterDialog;
import com.saatco.murshadik.dialogs.FilterDialogModel;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.workersService.Job;
import com.saatco.murshadik.model.workersService.PageInfoResponse;
import com.saatco.murshadik.model.workersService.Worker;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.UiUtils;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailableWorkerActivity extends BaseActivity {


    private ActivityAvailableWorkerBinding binding;
    final int PAGE_SIZE = 30;
    int trackPageNum = 1;
    private ArrayList<Job> jobs;
    private ArrayList<Worker> workers;
    private PageInfoResponse pageInfoResponse;
    WorkersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvailableWorkerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        UiUtils.resizeRecyclerViewHeight(this, binding.rvWorkers, binding.stSearchableToolbar);
        workers = new ArrayList<>();

        initRequests();

        initWorkerRecyclerView();
        initSearchableToolbar();
    }

    private void initRequests() {
        getWorkersListRequest();
        getAllJobsRequest();
    }


    private void initSearchableToolbar() {
        CustomViewSearchableToolbarBinding searchableToolbarBinding = binding.stSearchableToolbar.innerViews;

        searchableToolbarBinding.ivBtnBack.setOnClickListener(v -> onBackPressed());

        initSearchableToolbarSearchView();
        initSearchableToolbarJobsFilterBtn();
        initSearchableToolbarRegionsFilterBtn();
        initSearchableToolbarWorkerProfileBtn();

    }

    private void initSearchableToolbarSearchView() {
        CustomViewSearchableToolbarBinding searchableToolbarBinding = binding.stSearchableToolbar.innerViews;

        searchableToolbarBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchByWorkerName(newText);
                return false;
            }
        });
    }

    private void initSearchableToolbarJobsFilterBtn() {
        CustomViewSearchableToolbarBinding searchableToolbarBinding = binding.stSearchableToolbar.innerViews;
        searchableToolbarBinding.ivBtnFilter.setOnClickListener(v -> {
            FilterDialog filterDialog = new FilterDialog(this, getJobsFilter(), "فلترة بحسب التخصص:", false);
            filterDialog.setOnFilterSelectItemListener(msg -> {
                if (msg.getId() == -1) {
                    getWorkersListRequest();
                    searchableToolbarBinding.ivBtnFilter.setColorFilter(getColor(R.color.white));
                } else {
                    getWorkersByJobIdRequest(msg.getId());
                    searchableToolbarBinding.ivBtnFilter.setColorFilter(getColor(com.potyvideo.library.R.color.yellow));
                }
                // filter by region will well reset by default
                searchableToolbarBinding.ivBtnSort.setColorFilter(getColor(R.color.white));
            });

            filterDialog.show();
        });

    }

    private void initSearchableToolbarRegionsFilterBtn() {
        CustomViewSearchableToolbarBinding searchableToolbarBinding = binding.stSearchableToolbar.innerViews;

        searchableToolbarBinding.ivBtnSort.setImageResource(R.drawable.location);
        searchableToolbarBinding.ivBtnSort.setColorFilter(getColor(R.color.white));

        searchableToolbarBinding.ivBtnSort.setOnClickListener(v -> {
            FilterDialog filterDialog = new FilterDialog(this, getRegionsFilter(), "فلترة بحسب المنطقة:", false);
            filterDialog.setOnFilterSelectItemListener(msg -> {
                if (msg.getId() == -1) {
                    searchByRegionName("");
                    searchableToolbarBinding.ivBtnSort.setColorFilter(getColor(R.color.white));
                } else {
                    searchByRegionName(msg.getText());
                    searchableToolbarBinding.ivBtnSort.setColorFilter(getColor(com.potyvideo.library.R.color.yellow));
                }
            });

            filterDialog.show();
        });

    }

    private void initSearchableToolbarWorkerProfileBtn() {
        CustomViewSearchableToolbarBinding searchableToolbarBinding = binding.stSearchableToolbar.innerViews;

        Worker worker = WorkerHelper.getCurrentWorker(this);
        if (worker == null) {
            searchableToolbarBinding.btnAddNew.setVisibility(View.GONE);
            binding.cvRegisterAsWorker.setVisibility(View.VISIBLE);
            binding.cvRegisterAsWorker.setOnClickListener(v -> {
                startActivity(new Intent(this, RegisterWorkerActivity.class));
            });
            binding.btnRegisterNow.setOnClickListener(v -> {
                startActivity(new Intent(this, RegisterWorkerActivity.class));
            });
            return;
        }
        binding.cvRegisterAsWorker.setVisibility(View.GONE);


        RequestOptions requestOptions = new RequestOptions()
                .override(Util.dpToPixels(this, 30), Util.dpToPixels(this, 30))
                .transform(new RoundedCorners(15));

        Glide.with(this)
                .load(APIClient.baseUrl + worker.getImgUrl())
                .apply(requestOptions)
                .placeholder(R.drawable.profile_photo)
                .into(searchableToolbarBinding.btnAddNew);


        searchableToolbarBinding.btnAddNew.setOnClickListener(v -> {
            Intent i = new Intent(this, WorkerDetailsActivity.class);
            i.putExtra(Consts.EXTRA_IS_MY_WORKER_PROFILE, true);
            i.putExtra(Consts.EXTRA_WORKER_KEY, worker);
            startActivity(i);
        });

    }


    ArrayList<FilterDialogModel> getRegionsFilter() {
        ArrayList<Item> items = DataHelper.getRegions();
        ArrayList<FilterDialogModel> filterDialogModels = new ArrayList<>();
        filterDialogModels.add(new FilterDialogModel(-1, getString(R.string.all)));
        if (items != null)
            items.forEach(item -> filterDialogModels.add(new FilterDialogModel(item.getId(), item.getNameAr())));
        return filterDialogModels;
    }

    ArrayList<FilterDialogModel> getJobsFilter() {
        ArrayList<FilterDialogModel> filterDialogModels = new ArrayList<>();
        filterDialogModels.add(new FilterDialogModel(-1, getString(R.string.all)));
        if (jobs != null)
            jobs.forEach(jobs -> filterDialogModels.add(new FilterDialogModel(jobs.getId(), jobs.getName())));
        return filterDialogModels;
    }

    private void initWorkerRecyclerView() {
        adapter = new WorkersAdapter(this, workers, worker -> {
            Intent intent = new Intent(this, WorkerDetailsActivity.class);
            intent.putExtra(Consts.EXTRA_WORKER_KEY, worker);
            startActivity(intent);
        });

        binding.rvWorkers.setAdapter(adapter);
        binding.rvWorkers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvWorkers.setAnimation(new AnimationSet(true));
    }

    void searchByRegionName(String s) {
        adapter.updateList(workers.stream().filter(worker -> worker.getAddress().contains(s.trim())).collect(Collectors.toList()));
    }

    void searchByWorkerName(String s) {
        adapter.updateList(workers.stream().filter(worker -> worker.getName().contains(s.trim())).collect(Collectors.toList()));
    }

    public void getWorkersListRequest() {
        if (pageInfoResponse != null && pageInfoResponse.getNextPage())
            trackPageNum++;

        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getAllWorkerByPage("Bearer " + TokenHelper.getToken(), PAGE_SIZE, trackPageNum).enqueue(new Callback<NewAPIsResponse<ArrayList<Worker>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<Worker>>> call, @NonNull Response<NewAPIsResponse<ArrayList<Worker>>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                if (response.body() != null && response.body().getStatus()) {
                    workers.clear();
                    workers.addAll(response.body().getData());
                    pageInfoResponse = PageInfoResponse.getInstance(response.body().getInfo());
                    initWorkerRecyclerView();
                    return;
                }
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<Worker>>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));

            }
        });
    }

    public void getWorkersByJobIdRequest(int jobId) {
        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getAllWorkersByJobId(TokenHelper.getBearerToken(), jobId).enqueue(new Callback<NewAPIsResponse<ArrayList<Worker>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<Worker>>> call, @NonNull Response<NewAPIsResponse<ArrayList<Worker>>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                if (response.body() != null && response.body().getStatus()) {
                    workers.clear();
                    workers.addAll(response.body().getData());
                    initWorkerRecyclerView();
                    return;
                }
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<Worker>>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
            }
        });
    }


    void getAllJobsRequest() {
        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        binding.llNoData.setVisibility(View.GONE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getAllWorkerJobs("Bearer " + TokenHelper.getToken()).enqueue(new Callback<NewAPIsResponse<ArrayList<Job>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<Job>>> call, @NonNull Response<NewAPIsResponse<ArrayList<Job>>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                if (response.body() != null && response.body().getStatus()) {
                    jobs = response.body().getData();
                    initSearchableToolbar();
                    return;
                }
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<Job>>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
            }

        });
    }


}