package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.WorkerExperienceAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityRegisterWorkerExperianceBinding;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.workersService.Experience;
import com.saatco.murshadik.model.workersService.Worker;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterWorkerExperienceActivity extends BaseActivity {

    public static final String EXTRA_WORKER_KEY = "worker";
    ActivityRegisterWorkerExperianceBinding binding;

    WorkerExperienceAdapter adapter;
    Calendar[] dates;

    Worker currentWorker;

    /**
     * You should run this activity throw this method
     * worker data is needed or activity will not start
     */
    public static void startActivity(Worker worker, Context context) {
        if (worker == null) return;

        Intent intent = new Intent(context, RegisterWorkerExperienceActivity.class);
        intent.putExtra(EXTRA_WORKER_KEY, worker);
        intent.putExtra(EXTRA_WORKER_KEY, worker);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterWorkerExperianceBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        dates = new Calendar[2];
        boolean is_edition = getIntent().getBooleanExtra(Consts.EXTRA_IS_MY_WORKER_PROFILE, false);
        if (is_edition) {
            binding.appBar.btnSubmit.setText(getString(R.string.done));
        }

        binding.appBar.toolbarTitle.setText(R.string.experiences);

        binding.appBar.btnBack.setVisibility(View.GONE);
        binding.appBar.btnSubmit.setText("for test text");
        binding.appBar.btnSubmit.setOnClickListener(v -> {
            if (is_edition) {
                finish();
            }
            else {
                startActivity(new Intent(this, WorkerIntroActivity.class));
                finish();
            }
        });

        currentWorker = (Worker) getIntent().getSerializableExtra(EXTRA_WORKER_KEY);

        if (currentWorker.getExperiences() != null && currentWorker.getExperiences().size() > 0)
            initRecyclerView(currentWorker.getExperiences());
        else
            initRecyclerView(new ArrayList<>());


        binding.btnDateFrom.setOnClickListener(v -> openDatePicker(true));
        binding.btnDateTo.setOnClickListener(v -> openDatePicker(false));

        binding.btnAdd.setOnClickListener(v -> {
            if (checkData()) {
                addExperienceRequest();
            }
        });


    }

    private boolean checkData() {
        if (binding.etExperience.getText().toString().trim().equals("")) {
            binding.etExperience.setErrorInput(getString(R.string.field_required));
            return false;
        }
        if (dates[0] == null || dates[1] == null) {
            ToastUtils.longToast(getString(R.string.worker_experience_date_required));
            return false;
        }
        if (dates[0].getTimeInMillis() >= dates[1].getTimeInMillis()) {
            ToastUtils.longToast(getString(R.string.worker_experience_date_error_input));
            return false;
        }
        return true;
    }

    private void openDatePicker(boolean isDateFrom) {
        DatePickerDialog pickerDialog = new DatePickerDialog(this);
        pickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        pickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            String fullDate = year + "-" + month + "-" + dayOfMonth;
            if (isDateFrom) {
                binding.btnDateFrom.setText(fullDate);
                dates[0] = new GregorianCalendar(year, month, dayOfMonth);
            } else {
                binding.btnDateTo.setText(fullDate);
                dates[1] = new GregorianCalendar(year, month, dayOfMonth);
            }
        });

        pickerDialog.show();
    }

    void initRecyclerView(ArrayList<Experience> experiences) {
        adapter = new WorkerExperienceAdapter(this, experiences, (experience, position) -> deleteExperienceRequest(experience));

        binding.rvExperiences.setItemAnimator(new DefaultItemAnimator());
        binding.rvExperiences.setAdapter(adapter);
        binding.rvExperiences.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }


    RequestBody getBodyData() {

        Map<String, Object> jsonBody = new ArrayMap<>();
        jsonBody.put("WorkerId", currentWorker.getId());
        jsonBody.put("Description", binding.etExperience.getText().toString().trim());
        jsonBody.put("FromDate", binding.btnDateFrom.getText().toString());
        jsonBody.put("ToDate", binding.btnDateTo.getText().toString());


        return RequestBody.create((new JSONObject(jsonBody)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
    }

    private void addExperienceRequest() {

        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.addWorkerExperience("Bearer " + TokenHelper.getToken(), getBodyData()).enqueue(new Callback<NewAPIsResponse<Experience>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Experience>> call, @NonNull Response<NewAPIsResponse<Experience>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                if (response.body() == null || !response.body().getStatus()) {
                    ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng) + response.message());
                    return;
                }

                adapter.addItemToFirst(response.body().getData());
                binding.rvExperiences.smoothScrollToPosition(0);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Experience>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                Log.d("deleteExperienceRequest", "onFailure: " + t.getLocalizedMessage());
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng) + t.getLocalizedMessage());

            }
        });
    }

    private void deleteExperienceRequest(Experience exp) {

        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.deleteWorkerExperience("Bearer " + TokenHelper.getToken(), exp.getId()).enqueue(new Callback<NewAPIsResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Object>> call, @NonNull Response<NewAPIsResponse<Object>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                if (response.body() == null || !response.body().getStatus()) {
                    ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng) + response.message());
                    return;
                }

                adapter.removeItem(exp);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Object>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                Log.d("deleteExperienceRequest", "onFailure: " + t.getLocalizedMessage());
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng) + t.getLocalizedMessage());

            }
        });
    }
}