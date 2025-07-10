package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.autofill.HintConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.saatco.murshadik.Helpers.KeyboardHelper;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.OTPResponse;
import com.saatco.murshadik.api.response.RegionResponse;
import com.saatco.murshadik.databinding.ActivityVerifyCodeBinding;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VerifyCodeActivity extends AppCompatActivity {


    EditText etCode;
    TextView tvMobile;
    TextView tvTimer;
    Button verifyBtn;
    Button resendBtn;
    ImageView btnBack;
    ProgressBar progressBar;

    String mobile = "";
    String country = "";
    int roleID = 0;
    boolean isChangeNumber = false;


    CountDownTimer countDownTimer;

    private static final int ONE_MINUTE = 60000;

    private static final int FIVE_MINUTE = 300000;

    private static final int TEN_MINUTE = 600000;


    ActivityVerifyCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        mobile = getIntent().getStringExtra("MOBILE");
        country = getIntent().getStringExtra("COUNTRY");
        roleID = getIntent().getIntExtra("ROLE_ID", 0);
        isChangeNumber = getIntent().getBooleanExtra("is_change_number", false);

        tvMobile.setText("+".concat(mobile));

        // [START declare_auth]

        Log.d("OTP", PrefUtil.getInteger(getApplicationContext(), "otp_timer") + "seconds");

        getRegions();

        etCode.requestFocus();

        etCode.setAutofillHints(HintConstants.AUTOFILL_HINT_SMS_OTP);

        resendBtn.setEnabled(false);


        btnBack.setOnClickListener(view -> finish());

        resendBtn.setOnClickListener(view -> resendCode());

        etCode.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        etCode.setLongClickable(false);
        etCode.setTextIsSelectable(false);

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (etCode.getText().toString().length() > 3) {
                    if (isChangeNumber) updatePhoneNumber(etCode.getText().toString());
                    else sendOpt(etCode.getText().toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initViews() {
        etCode = binding.etCode;
        tvMobile = binding.tvMobile;
        tvTimer = binding.tvTimer;
        verifyBtn = binding.btnDone;
        resendBtn = binding.btnResend;
        btnBack = binding.btnBack;
        progressBar = binding.progressBar;

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (PrefUtil.getInteger(getApplicationContext(), "otp_timer") == 0) {
            startCounter(PrefUtil.getBoolean(getApplicationContext(), "is_one_minute_passed") ? FIVE_MINUTE : ONE_MINUTE);
        } else {
            int time = PrefUtil.getInteger(getApplicationContext(), "otp_timer");
            startCounter(time * 1000);
        }

    }

    private void sendOpt(String otp) {
        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<OTPResponse> call = apiInterface.checkOTP(mobile, otp);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OTPResponse> call, @NonNull Response<OTPResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {

                    assert response.body() != null;
                    if (response.body().isStatus()) {

                        KeyboardHelper.hideSoftKeyboard(getApplicationContext(), etCode);

                        countDownTimer.cancel();
                        PrefUtil.writeIntValue(getApplicationContext(), "otp_timer", 0);
                        PrefUtil.writeBooleanValue(getApplicationContext(), "is_one_minute_passed", false);

                        ProfileHelper.createOrUpdateAccount(response.body().getData().getUser(), getApplicationContext());
                        TokenHelper.createToken(response.body().getMessage());


                        if (response.body().getData().getUser().getPhoneNumber() != null) {
                            FirebaseMessaging.getInstance().subscribeToTopic(response.body().getData().getUser().getPhoneNumber().replaceAll("\\s+", "")).addOnSuccessListener(aVoid -> {
                            });
                        }


                        if (response.body().getData().getUser().isWeatherNotificationEnabled()) {

                            if (response.body().getData().getUser().getWeatherCode() != null) {
                                FirebaseMessaging.getInstance().subscribeToTopic(response.body().getData().getUser().getWeatherCode()).addOnSuccessListener(aVoid -> {
                                });
                            }
                        }

                        if (response.body().getData().getUser().isMarketNotificationEnabled()) {
                            FirebaseMessaging.getInstance().subscribeToTopic(response.body().getData().getUser().getPhoneNumber().replaceAll("\\s+", "") + Consts.MARKET_FCM).addOnSuccessListener(aVoid -> {
                            });

                        }

                        if (response.body().getData().getUser().getRegionId() != 0) {
                            FirebaseMessaging.getInstance().subscribeToTopic(Consts.GROUP_FCM_TOPIC + response.body().getData().getUser().getRegionId()).addOnSuccessListener(aVoid -> {
                            });
                        }

                        if (response.body().getData().getUser().isConsultantUser() && response.body().getData().getUser().getLocation() == null) {

                            Intent intent = new Intent(VerifyCodeActivity.this, RegisterActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Intent intent = new Intent(VerifyCodeActivity.this, NewMainActivityDesign.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                    } else
                        Toast.makeText(getApplicationContext(), "رمز غير صالح", Toast.LENGTH_SHORT).show();


                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<OTPResponse> call, @NonNull Throwable t) {
                Log.v("VERIFY", "", t);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updatePhoneNumber(String opt) {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<OTPResponse> call = apiInterface.updatePhoneNoCheckingOTP("Bearer " + TokenHelper.getToken(), mobile.replace("+", ""), opt);
        call.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(@NonNull Call<OTPResponse> call, @NonNull Response<OTPResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {

                    assert response.body() != null;
                    if (response.body().isStatus()) {

                        KeyboardHelper.hideSoftKeyboard(getApplicationContext(), etCode);
                        countDownTimer.cancel();
                        unsubscribeTopicsFromOldNumber();

                        PrefUtil.writeIntValue(getApplicationContext(), "otp_timer", 0);
                        PrefUtil.writeBooleanValue(getApplicationContext(), "is_one_minute_passed", false);
                        ProfileHelper.createOrUpdateAccount(response.body().getData().getUser(), getApplicationContext());

                        if (response.body().getData().getUser().getPhoneNumber() != null) {
                            FirebaseMessaging.getInstance().subscribeToTopic(response.body().getData().getUser().getPhoneNumber().replaceAll("\\s+", "")).addOnSuccessListener(aVoid -> {
                            });
                        }


                        if (response.body().getData().getUser().isWeatherNotificationEnabled()) {

                            if (response.body().getData().getUser().getWeatherCode() != null) {
                                FirebaseMessaging.getInstance().subscribeToTopic(response.body().getData().getUser().getWeatherCode()).addOnSuccessListener(aVoid -> {
                                });
                            }
                        }

                        if (response.body().getData().getUser().isMarketNotificationEnabled()) {
                            FirebaseMessaging.getInstance().subscribeToTopic(response.body().getData().getUser().getPhoneNumber().replaceAll("\\s+", "") + Consts.MARKET_FCM).addOnSuccessListener(aVoid -> {
                            });

                        }


                        Toast.makeText(getApplicationContext(), "تم تحديث رقم الهاتف", Toast.LENGTH_SHORT).show();
                        finish();


                    } else
                        Toast.makeText(getApplicationContext(), "رمز غير صالح", Toast.LENGTH_SHORT).show();


                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<OTPResponse> call, @NonNull Throwable t) {
                Log.v("VERIFY", "", t);
                progressBar.setVisibility(View.GONE);
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

    private void getRegions() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RegionResponse> call = apiInterface.getRegionsWithCity();
        call.enqueue(new Callback<RegionResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegionResponse> call, @NonNull Response<RegionResponse> response) {

                try {

                    Gson gson = new Gson();
                    assert response.body() != null;
                    String jsonCars = gson.toJson(response.body().getRegionList());
                    PrefUtil.writePreferenceValue(App.getInstance(), "regions", jsonCars);

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegionResponse> call, @NonNull Throwable t) {

            }
        });


    }

    private void resendCode() {

        progressBar.setVisibility(View.VISIBLE);

        if (PrefUtil.getInteger(getApplicationContext(), "otp_timer") == 0 && PrefUtil.getBoolean(getApplicationContext(), "is_one_minute_passed")) {
            startCounter(FIVE_MINUTE);
        } else if (PrefUtil.getInteger(getApplicationContext(), "otp_timer") == 0 && !PrefUtil.getBoolean(getApplicationContext(), "is_one_minute_passed")) {
            startCounter(TEN_MINUTE);
        }

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = null;

        if (isChangeNumber)
            call = apiInterface.changePhoneNumber("Bearer " + TokenHelper.getToken(), mobile);
        else call = apiInterface.loginUser(mobile, roleID, country, 2);

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                progressBar.setVisibility(View.GONE);

                resendBtn.setEnabled(false);
                resendBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disable_resend_color, null)));


            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                resendBtn.setEnabled(false);
                resendBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disable_resend_color, null)));
            }
        });
    }

    private void startCounter(int time) {

        countDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

                tvTimer.setText("".concat(String.format(Locale.ENGLISH, "%d : %d ", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)), TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                PrefUtil.writeIntValue(getApplicationContext(), "otp_timer", (int) (millisUntilFinished / 1000));
                Log.d("OTP", "close" + PrefUtil.getInteger(getApplicationContext(), "otp_timer") + "seconds");
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                PrefUtil.writeIntValue(getApplicationContext(), "otp_timer", 0);

                PrefUtil.writeBooleanValue(getApplicationContext(), "is_one_minute_passed", !PrefUtil.getBoolean(getApplicationContext(), "is_one_minute_passed"));
                tvTimer.setText("00:00");
                countDownTimer.cancel();
                resendBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.resend_color, null)));
                resendBtn.setEnabled(true);
            }

        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();


        countDownTimer.cancel();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        countDownTimer.cancel();
        PrefUtil.writeIntValue(getApplicationContext(), "otp_timer", 0);

    }

    private void unsubscribeTopicsFromOldNumber() {
        User user = ProfileHelper.getAccount(getApplicationContext());

        if (user.getPhoneNumber() != null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getPhoneNumber().replaceAll("\\s+", "")).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }


        if (user.isWeatherNotificationEnabled()) {

            if (user.getWeatherCode() != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getWeatherCode()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
            }
        }

        if (user.isMarketNotificationEnabled()) {
            FirebaseMessaging.getInstance().subscribeToTopic(user.getPhoneNumber().replaceAll("\\s+", "") + Consts.MARKET_FCM).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });

        }
    }
}
