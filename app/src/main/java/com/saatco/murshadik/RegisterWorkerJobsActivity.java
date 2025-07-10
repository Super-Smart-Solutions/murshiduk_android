package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.WorkerJobsAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityRegisterWorkerJobsBinding;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.workersService.Job;
import com.saatco.murshadik.model.workersService.JobWithWorker;
import com.saatco.murshadik.model.workersService.Worker;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterWorkerJobsActivity extends BaseActivity {

    ActivityRegisterWorkerJobsBinding binding;
    ArrayList<Job> jobs = null;
    ArrayList<JobWithWorker> jobsWithWorkers;
    WorkerJobsAdapter adapter;

    Worker currentWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterWorkerJobsBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        jobsWithWorkers = new ArrayList<>();


        getCurrentWorkerRequest();

        boolean is_edition = getIntent().getBooleanExtra(Consts.EXTRA_IS_MY_WORKER_PROFILE, false);
        if (is_edition) {
            binding.btnNext.setText(getString(R.string.done));
        }

        binding.llNoData.setOnClickListener(v -> getAllJobsRequest());
        binding.appBar.btnBack.setVisibility(View.GONE);
        binding.appBar.toolbarTitle.setText(R.string.jobs);

        binding.btnNext.setOnClickListener(v -> {
            if (jobsWithWorkers.size() > 0){// check if user have selected any jobs
                if (is_edition) {
                    finish();
                }
                else {
                    RegisterWorkerExperienceActivity.startActivity(currentWorker, this);
                    finish();
                }
            }
            else{
                Util.showErrorToast(getString(R.string.no_worker_jobs_added), this);
            }
        });

    }


    private void initData(){
        if (!initJobsListView()) return;
        updateJobsRelatedWithWorker();
        initJobsAdapter();
    }

    private boolean initJobsListView() {
        if (jobs == null) {
            binding.llNoData.setVisibility(View.VISIBLE);
            return false;
        }
        jobs.forEach(job -> {
            if(currentWorker.getJobs().contains(job))
                job.setAdded(true);
        });

        return true;
    }

    private void initJobsAdapter() {
        adapter = new WorkerJobsAdapter(this, jobs, (job, isAdding) -> {
            if (isAdding)
                addJobRequest(job);
            else {
                removeJobRequest(job);
            }
        });

        binding.rvJobs.setAdapter(adapter);
        binding.rvJobs.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvJobs.setItemAnimator(new DefaultItemAnimator());
    }

    private void updateJobsRelatedWithWorker(){
        currentWorker.getJobs().forEach(job -> jobsWithWorkers.add(new JobWithWorker(job.getId(), job.getName())));
    }


    private void changeJobStateInRecyclerview(Job job, boolean isAdding){
        adapter.removeItem(job);
        job.setAdded(isAdding);
        adapter.addItemToFirst(job);
    }

    private void getCurrentWorkerRequest() {
        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getCurrentWorker("Bearer " + TokenHelper.getToken()).enqueue(new Callback<NewAPIsResponse<Worker>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Response<NewAPIsResponse<Worker>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                if (response.body() != null && response.body().getStatus()){
                    currentWorker = response.body().getData();
                    getAllJobsRequest();
                    return;
                }
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
            }
        });
    }


    void addJobRequest(Job job) {

        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.addWorkerJob("Bearer " + TokenHelper.getToken(), job.getId(), currentWorker.getId()).enqueue(new Callback<NewAPIsResponse<JobWithWorker>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<JobWithWorker>> call, @NonNull Response<NewAPIsResponse<JobWithWorker>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                if (response.body() != null && response.body().getStatus()) {
                    JobWithWorker j = response.body().getData();
                    j.setJobName(job.getName());
                    jobsWithWorkers.add(j);
                    changeJobStateInRecyclerview(job, true);
                }

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<JobWithWorker>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
            }
        });
    }

    void removeJobRequest(Job job) {

        JobWithWorker jobWithWorker = null;
        for (JobWithWorker j : jobsWithWorkers) {
            if (j.getJobName().equals(job.getName())) {
                jobWithWorker = j;
                break;
            }
        }
        if (jobWithWorker == null) {
            jobWithWorker = new JobWithWorker(job.getId());
        }
        JobWithWorker finalJobWithWorker = jobWithWorker;

        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.deleteWorkerJob("Bearer " + TokenHelper.getToken(), jobWithWorker.getId()).enqueue(new Callback<NewAPIsResponse<ArrayList<Object>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<Object>>> call, @NonNull Response<NewAPIsResponse<ArrayList<Object>>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                if (response.body() != null && response.body().getStatus()) {

                    jobsWithWorkers.remove(finalJobWithWorker);
                    changeJobStateInRecyclerview(job, false);

                    Log.d("RemoveJob", "onResponse: " + response.body().getStatus());
                }

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<Object>>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                Log.d("RemoveJob", "onFailure: " + t.getLocalizedMessage());
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
                if (response.body() != null && response.body().getStatus()) {
                    jobs = response.body().getData();
                }
                initData();
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<Job>>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                initData();
            }

        });
    }


}