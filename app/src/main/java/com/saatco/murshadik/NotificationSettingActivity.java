package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.databinding.ActivityNotificationSettingBinding;
import com.saatco.murshadik.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSettingActivity extends AppCompatActivity {

    LinearLayout btnAlert;
    SwitchCompat switchMarket;
    SwitchCompat switchWeather;

    User user = null;

    ActivityNotificationSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));

        switchMarket.setChecked(ProfileHelper.getAccount(getApplicationContext()).isMarketNotificationEnabled());
        switchWeather.setChecked(ProfileHelper.getAccount(getApplicationContext()).isWeatherNotificationEnabled());

        user = ProfileHelper.getAccount(getApplicationContext());

        switchMarket.setOnCheckedChangeListener((compoundButton, check) -> {


            PrefUtil.writeBooleanValue(getApplicationContext(), "IS_MARKET_ALERT", check);

            if (check) {
                updateEnableNotification(1, true);
                FirebaseMessaging.getInstance().subscribeToTopic(user.getPhoneNumber().replaceAll("\\s+", "") + "-market").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
            } else {
                updateEnableNotification(1, false);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getPhoneNumber().replaceAll("\\s+", "") + "-market").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
            }
        });

        switchWeather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {

                PrefUtil.writeBooleanValue(getApplicationContext(), "IS_WEATHER_ALERT", check);

                if (check) {
                    updateEnableNotification(2, true);
                    FirebaseMessaging.getInstance().subscribeToTopic(user.getWeatherCode()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                } else {
                    updateEnableNotification(2, false);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getWeatherCode()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }

            }
        });

        btnAlert.setOnClickListener(view -> startActivity(new Intent(NotificationSettingActivity.this, SubscribeProductActivity.class)));

    }

    private void initViews() {
        btnAlert = binding.btnAlert;
        switchMarket = binding.switchMarket;
        switchWeather = binding.switchWeather;

    }

    private void updateEnableNotification(int typeId, boolean isEnable) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.enableNotificationForType("Bearer " + TokenHelper.getToken(), typeId, isEnable);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                try {

                    assert response.body() != null;
                    if (response.body().isStatus()) {

                        User user = ProfileHelper.getAccount(getApplicationContext());

                        if (typeId == 2)
                            user.setWeatherNotificationEnabled(isEnable);
                        else if (typeId == 1)
                            user.setMarketNotificationEnabled(isEnable);

                        ProfileHelper.createOrUpdateAccount(user, getApplicationContext());

                        // Util.showSuccessToast(response.body().getMessage(), NotificationSettingActivity.this);

                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {

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
}