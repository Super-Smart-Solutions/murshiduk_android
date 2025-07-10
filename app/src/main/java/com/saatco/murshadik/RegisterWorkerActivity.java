package com.saatco.murshadik;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.WorkerHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityRegisterWorkerBinding;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.workersService.Nationality;
import com.saatco.murshadik.model.workersService.Worker;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterWorkerActivity extends BaseActivity {

    ArrayList<Nationality> nationalities;
    ActivityRegisterWorkerBinding binding;
    DialogUtil.DialogListOfObjects<Nationality> nationalityDialog;
    Nationality selectedNationality;

    Worker currentWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterWorkerBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        boolean is_edition = getIntent().getBooleanExtra(Consts.EXTRA_IS_MY_WORKER_PROFILE, false);
        if (is_edition) {
            binding.btnNext.setText(getString(R.string.done));
        }

        binding.appBar.btnBack.setOnClickListener(v -> onBackPressed());
        binding.appBar.toolbarTitle.setText(R.string.register_as_worker);


        binding.etIdNumber.innerViews.etContent.setInputType(InputType.TYPE_CLASS_NUMBER);
        binding.etExpectedSalary.innerViews.etContent.setInputType(InputType.TYPE_CLASS_NUMBER);

        binding.etAge.setOnClickListener(v -> openDateDialogForDOB());

        binding.btnNext.setOnClickListener(v -> {
            if (!checkData()) return;
            if (is_edition) {
                updateWorker();
            } else {
                addWorker();
            }
        });

        getNationalities();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentWorkerRequest();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void bindDataToViews(Worker worker) {
        binding.etAge.setText(worker.getDateOfBirth().split("T")[0]);
        binding.etNationality.setText(worker.getNationalityAr());
        binding.etExpectedSalary.setText(worker.getExpectedSalary() + "");

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
            selectedNationality = findNationalityByName(worker.getNationality());
        });
    }

    private Nationality findNationalityByName(String name) {
        if (nationalities == null) return null;
        for (Nationality n : nationalities) {
            if (Objects.equals(n.getName(), name))
                return n;
        }
        return null;
    }

    private void getCurrentWorkerRequest() {
        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getCurrentWorker("Bearer " + TokenHelper.getToken()).enqueue(new Callback<NewAPIsResponse<Worker>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Response<NewAPIsResponse<Worker>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                if (response.body() != null && response.body().getStatus()) {
                    currentWorker = response.body().getData();
                    bindDataToViews(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);
                ToastUtils.shortToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
            }
        });
    }

    void getNationalities() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getAllNationalities("Bearer " + TokenHelper.getToken()).enqueue(new Callback<NewAPIsResponse<ArrayList<Nationality>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<Nationality>>> call, @NonNull Response<NewAPIsResponse<ArrayList<Nationality>>> response) {
                if (response.body() == null) return;

                nationalities = response.body().getData();
                setupNationalitiesDialog();

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<Nationality>>> call, @NonNull Throwable t) {
                Log.d("TAG Nationalities: ", "onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    private void setupNationalitiesDialog() {

        nationalityDialog = new DialogUtil.DialogListOfObjects<>(this, getString(R.string.nationality), nationalities, msg -> {
            binding.etNationality.setText(msg.toString());
            binding.etNationality.clearErrorInput();
            selectedNationality = msg;
        });

        binding.etNationality.setOnClickListener(v -> nationalityDialog.show());

    }

    void openDateDialogForDOB() {
        DatePickerDialog dialog = new DatePickerDialog(this);
        dialog.setOnDateSetListener((view, year, month, dayOfMonth) ->
                binding.etAge.setText(year + "-" + month + "-" + dayOfMonth)
        );

        dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis() - 15 * 31536000000L); // one year in milliseconds = 31,536,000,000
        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 50 * 31536000000L); // one year in milliseconds = 31,536,000,000
        dialog.show();
    }

    boolean checkData() {
        boolean check = checkIdNumber();
        if (!checkAge()) check = false;
        if (!checkSalary()) check = false;
        if (!checkNationality()) check = false;
        return check;
    }

    boolean checkAge() {
        String s = binding.etAge.getText().toString().trim();
        if (s.isEmpty()) {
            binding.etAge.setErrorInput(getString(R.string.worker_age_error));
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        int age = calendar.get(Calendar.YEAR) - Integer.parseInt(s.split("-")[0]);

        if (age < 15 || age > 50) {
            binding.etAge.setErrorInput(getString(R.string.worker_age_error));
            return false;
        }
        return true;
    }

    boolean checkIdNumber() {
        if (binding.etIdNumber.getText().length() < 10 ||
                binding.etIdNumber.getText().length() > 10 ||
                !Pattern.matches("[12345]", binding.etIdNumber.getText().subSequence(0, 1))) {
            binding.etIdNumber.setErrorInput(getString(R.string.worker_id_number_error));
            return false;
        }
        return true;
    }

    boolean checkSalary() {
        String s = binding.etExpectedSalary.getText().toString().trim();
        if (s.equals("")) {
            binding.etExpectedSalary.setErrorInput(getString(R.string.worker_salary_error));
            return false;
        }
        int i = Integer.parseInt(s);
        if (i < 500 || i > 10000) {
            binding.etExpectedSalary.setErrorInput(getString(R.string.worker_salary_error));
            return false;
        }
        return true;
    }

    boolean checkNationality() {
        if (selectedNationality == null) {
            binding.etNationality.setErrorInput(getString(R.string.worker_nationality_error));
            return false;
        }
        return true;
    }

    void addWorker() {
        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        User user = ProfileHelper.getAccount(this);
        Map<String, Object> jsonData = new ArrayMap<>();
        jsonData.put("Phone", user.getPhoneNumber());
        jsonData.put("IDNumber", binding.etIdNumber.getText().toString());
        jsonData.put("Address", user.getLocation() + "-" + user.getPrefix());
        jsonData.put("DateOfBirth", binding.etAge.getText().toString());
        jsonData.put("Isbusy", 0);
        jsonData.put("ExpectedSalary", binding.etExpectedSalary.getText().toString());
        jsonData.put("NationaltyId", selectedNationality.getId());


        RequestBody body = RequestBody.create((new JSONObject(jsonData)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.addWorker("Bearer " + TokenHelper.getToken(), body).enqueue(new Callback<NewAPIsResponse<Worker>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Response<NewAPIsResponse<Worker>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                if (response.body() == null) return;
                if (response.body().getStatus()) {
                    WorkerHelper.createOrUpdate(response.body().getData(), RegisterWorkerActivity.this);
                    ToastUtils.longToast(R.string.worker_register_done);
                    startActivity(new Intent(RegisterWorkerActivity.this, RegisterWorkerJobsActivity.class));
                } else ToastUtils.shortToast(response.body().getMessage());
                Log.d("TAG: " + response.body().getMessage(), "onResponse: " + response.body().getMessage());
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                ToastUtils.shortToast(t.getLocalizedMessage());
                Log.d("TAG: " + t.getMessage(), "onResponse: " + t.getMessage());
            }
        });
    }


    void updateWorker() {
        binding.layoutLoader.getRoot().setVisibility(View.VISIBLE);
        User user = ProfileHelper.getAccount(this);
        Map<String, Object> jsonData = new ArrayMap<>();
        jsonData.put("Id", currentWorker.getId());
        jsonData.put("Phone", user.getPhoneNumber());
        jsonData.put("IDNumber", binding.etIdNumber.getText().toString());
        jsonData.put("Address", user.getLocation() + "-" + user.getPrefix());
        jsonData.put("DateOfBirth", binding.etAge.getText().toString());
        jsonData.put("Isbusy", 0);
        jsonData.put("ExpectedSalary", binding.etExpectedSalary.getText().toString());
        jsonData.put("NationaltyId", selectedNationality.getId());


        RequestBody body = RequestBody.create((new JSONObject(jsonData)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.updateWorker("Bearer " + TokenHelper.getToken(), body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Response<NewAPIsResponse<Worker>> response) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                if (response.body() == null) return;
                if (response.body().getStatus()) {
                    finish();
                } else ToastUtils.shortToast(response.body().getMessage());
                Log.d("TAG: " + response.body().getMessage(), "onResponse: " + response.body().getMessage());
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Throwable t) {
                binding.layoutLoader.getRoot().setVisibility(View.GONE);

                ToastUtils.shortToast(t.getLocalizedMessage());
                Log.d("TAG: " + t.getMessage(), "onResponse: " + t.getMessage());
            }
        });
    }

}