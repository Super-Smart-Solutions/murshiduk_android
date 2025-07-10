package com.saatco.murshadik;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.databinding.ActivityRatingBinding;

public class RatingActivity extends BaseActivity  {

    private ActivityRatingBinding binding;
    int consultantId;
    View loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PrefUtil.writePreferenceValue(getApplicationContext(),"consultant_time","");

        loader = binding.llProgressBar.getRoot();

        consultantId = getIntent().getIntExtra("consultant_id",0);

        if(getIntent().getStringExtra("consultant_name") != null){
            binding.consultantName.setVisibility(View.VISIBLE);
            binding.consultantName.setText(getIntent().getStringExtra("consultant_name"));
        }

        binding.btnDone.setOnClickListener(view -> {
            if(binding.rating.getRating() < 1.0) {
                Toast.makeText(getApplicationContext(),"فضلاً قيم المرشد",Toast.LENGTH_SHORT).show();
            }else{
               ratingConsultant();
            }
        });
    }

    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void ratingConsultant(){

        loader.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.updateUserRating("Bearer " + TokenHelper.getToken(),consultantId, String.valueOf(binding.rating.getRating()), binding.etComment.getText().toString());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                loader.setVisibility(View.GONE);

                try {
                    finish();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                loader.setVisibility(View.GONE);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }

}
