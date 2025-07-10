package com.saatco.murshadik;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityWorkerDetailsBinding;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.workersService.Experience;
import com.saatco.murshadik.model.workersService.Job;
import com.saatco.murshadik.model.workersService.Worker;
import com.saatco.murshadik.utils.Consts;

import java.util.Calendar;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerDetailsActivity extends BaseActivity {

    ActivityWorkerDetailsBinding binding;

    Worker worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        worker = (Worker) getIntent().getSerializableExtra(Consts.EXTRA_WORKER_KEY);

        if (getIntent().getBooleanExtra(Consts.EXTRA_IS_MY_WORKER_PROFILE, false)) {
            enableEdition(worker);
        }

        bindDataToViews(worker);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getWorkerDataRequest(worker.getId());
    }


    private void enableEdition(Worker worker) {
        binding.ivEditWorkerInfo.setVisibility(View.VISIBLE);
        binding.ivEditWorkerJobs.setVisibility(View.VISIBLE);
        binding.ivEditWorkerExp.setVisibility(View.VISIBLE);

        Intent i = new Intent();
        i.putExtra(Consts.EXTRA_IS_MY_WORKER_PROFILE, true);
        i.putExtra(Consts.EXTRA_WORKER_KEY, worker);

        binding.ivEditWorkerInfo
                .setOnClickListener(v -> startActivity(i.setClass(this, RegisterWorkerActivity.class)));

        binding.ivEditWorkerJobs
                .setOnClickListener(v -> startActivity(i.setClass(this, RegisterWorkerJobsActivity.class)));

        binding.ivEditWorkerExp
                .setOnClickListener(v -> startActivity(i.setClass(this, RegisterWorkerExperienceActivity.class)));
    }

    private void bindDataToViews(Worker worker) {
        if (worker == null) return;

        Calendar calendar = Calendar.getInstance();
        String age = (calendar.get(Calendar.YEAR) - Integer.parseInt(worker.getDateOfBirth().split("-")[0])) + "";

        binding.tvAge.setText(age);

        if (worker.getJobs() != null)
            binding.tvCareer.setText(worker.getJobs().stream().map(Job::getName).collect(Collectors.joining(", ")));

        binding.tvNationality.setText(worker.getNationalityAr());
        binding.tvWorkerName.setText(worker.getName());
        binding.tvRegion.setText(worker.getAddress().split("-")[0]);

//        if (worker.getAddress().split("-").length > 1)
//            binding.tvCity.setText(worker.getAddress().split("-")[1]);
//        else binding.tvCity.setText("");

        String strSalary = worker.getExpectedSalary() + "";
        binding.tvPaiedMonthly.setText(strSalary);

        if (worker.getExperiences() != null)
            binding.tvPreExpert.setText(worker.getExperiences().stream().map(Experience::getDescription).collect(Collectors.joining("\n*-")));

        binding.btnOpenAjeer.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.ajeer.com.sa/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        binding.stSearchableToolbar.innerViews.ivBtnBack.setOnClickListener(v -> onBackPressed());

        binding.tvCallWorker.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + worker.getPhone()));
            startActivity(intent);
        });

        Glide.with(getApplicationContext())
                .load(APIClient.baseUrl + worker.getImgUrl())
                .placeholder(AppCompatResources.getDrawable(this, R.drawable.logo))
                .into(binding.imgWorker);
    }

    private void getWorkerDataRequest(int workerId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getWorkerById("Bearer " + TokenHelper.getToken(), workerId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Response<NewAPIsResponse<Worker>> response) {
                if (response.body() != null) {
                    bindDataToViews(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Throwable t) {

            }
        });
    }
}