package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityUserInfoBinding;
import com.saatco.murshadik.model.User;


public class userInfoActivity extends BaseActivity {

    TextView tvName;
    TextView tvMobile;
    TextView tvResume;
    TextView tvLocation;
    ImageView ivProfilePhoto;
    TextView tvStatus;
    ImageView btnBack;
    LinearLayout layoutProfile;

    User user;

    ActivityUserInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        user = (User) getIntent().getSerializableExtra("USER");

        btnBack.setOnClickListener(view -> finish());

        getUserById(user.getId());
    }

    private void initViews() {
        tvName = binding.tvName;
        tvMobile = binding.tvMobile;
        tvResume = binding.tvResume;
        tvLocation = binding.tvLocation;
        ivProfilePhoto = binding.ivProfilePhoto;
        tvStatus = binding.tvStatus;
        btnBack = binding.btnBack;
        layoutProfile = binding.layoutProfileInfo;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void getUserById(int userId) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<User> call = apiInterface.getUserBy("Bearer " + TokenHelper.getToken(), String.valueOf(userId));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                try {

                    if (response.body() != null) {
                        User user = response.body();
                        tvName.setText(user.getName());
                        tvStatus.setText(user.getStatus());
                        tvResume.setText(user.getProfile());
                        tvLocation.setText(user.getLocation());
                        tvMobile.setText("+".concat(user.getPhoneNumber()));

                        if (user.getRoleId() == 5) {
                            tvResume.setVisibility(View.GONE);
                            layoutProfile.setVisibility(View.GONE);
                        }


                        RequestOptions requestOptions = new RequestOptions().override(300, 300).circleCrop();

                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(APIClient.imageUrl + user.getPhotoUrl())
                                .apply(requestOptions)
                                .placeholder(R.drawable.profile_photo)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {

                                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                        final float roundPx = (float) resource.getWidth() * 2f;
                                        roundedBitmapDrawable.setCornerRadius(roundPx);

                                        ivProfilePhoto.setImageBitmap(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

            }
        });
    }
}
