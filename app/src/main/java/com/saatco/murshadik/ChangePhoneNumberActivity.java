package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.databinding.ActivityChangePhoneNumberBinding;
import com.saatco.murshadik.utils.Util;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePhoneNumberActivity extends AppCompatActivity {

    EditText etMobile;
    TextView tvSelectCountry;
    TextView tvPhoneCode;
    LinearLayout btnChooseCountry;
    LinearLayout llProgressBar;


    public static final int REQUEST_SELECT_CITY = 200;
    public static final int RESULT_SELECT_CITY_OK = 201;

    Button loginBtn;

    String countryCode = "sa";

    ActivityChangePhoneNumberBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePhoneNumberBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();

        ToolbarHelper.setToolBar(this,"", findViewById(R.id.toolbarTrans));

        loginBtn = findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(view -> {

                if (checkForm()) {
                    if (isPhoneNumberValid(tvPhoneCode.getText().toString() + "" + etMobile.getText().toString().trim(), countryCode)) {
                        loginBtn.setEnabled(false);
                        changeNumber();
                    } else {
                        YoYo.with(Techniques.Shake)
                                .playOn(etMobile);
                    }
                }
        });

        btnChooseCountry.setOnClickListener(view -> {

            Intent intent = new Intent(ChangePhoneNumberActivity.this,SelectCityActivity.class);
            startActivityForResult(intent,REQUEST_SELECT_CITY);

        });
    }

    private void initViews() {
        etMobile = binding.etMobile;
        tvSelectCountry = binding.tvSelectCountry;
        tvPhoneCode = binding.tvPhoneCode;
        btnChooseCountry = binding.btnChooseCountry;
        llProgressBar = binding.llProgressBar.getRoot();

    }

    private void changeNumber() {

        llProgressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.changePhoneNumber("Bearer "+TokenHelper.getToken(),tvPhoneCode.getText().toString().replace("+","")+""+etMobile.getText().toString().trim());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                llProgressBar.setVisibility(View.GONE);
                loginBtn.setEnabled(true);


                try {
                    assert response.body() != null;
                    if(response.body().isStatus()){
                        Intent intent = new Intent(ChangePhoneNumberActivity.this,VerifyCodeActivity.class);
                        intent.putExtra("MOBILE",tvPhoneCode.getText().toString().replace("+","")+""+etMobile.getText().toString());
                        intent.putExtra("COUNTRY",tvSelectCountry.getText().toString());
                        intent.putExtra("is_change_number",true);
                        startActivity(intent);
                        finish();
                    }else{
                        Util.showErrorToast(response.body().getMessage(),ChangePhoneNumberActivity.this);
                    }

                }catch (Exception ignored){}

            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                loginBtn.setEnabled(true);
                llProgressBar.setVisibility(View.GONE);
            }
        });

    }

    public boolean isPhoneNumberValid(String phoneNumber, String countryCode)
    {
        //NOTE: This should probably be a member variable.
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(getApplicationContext());
        try
        {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            String formatted = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            if(!formatted.replace(" ","").equals(phoneNumber))
                return false;
            return phoneUtil.isValidNumber(numberProto);
        }
        catch (NumberParseException e)
        {
            Log.d("LoGIN","NumberParseException was thrown: " + e);
        }

        return false;
    }

    private boolean checkForm() {

        if (etMobile.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.mobile_number), Toast.LENGTH_SHORT).show();
            YoYo.with(Techniques.Shake)
                    .playOn(etMobile);
            return false;
        }
        return true;
    }

    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_CITY) {
            if (resultCode == RESULT_SELECT_CITY_OK) {

                String country = data.getStringExtra("country");
                String dialCode = data.getStringExtra("country_dial");
                String code = data.getStringExtra("country_code");
                if (country != null) {
                    countryCode = code;
                    tvSelectCountry.setText(country);
                    tvPhoneCode.setText(dialCode);
                }
            }
        }

    }


}