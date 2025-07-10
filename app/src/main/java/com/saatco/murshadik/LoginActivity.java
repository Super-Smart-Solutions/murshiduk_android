package com.saatco.murshadik;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.databinding.ActivityLoginBinding;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.views.EditWithBorderTitle;
import com.ybs.countrypicker.CountryPicker;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout etMobile;
    EditText etPassword;
    TextView tvSelectCountry;
    LinearLayout btnChooseCountry;
    CheckBox switchTerm;
    LinearLayout llProgressBar;
    LinearLayout layoutContact;

    ///firebase

    Dialog dialog;


    int roleID = 5;
    Button loginBtn;

    String countryCode = "sa";

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

        FirebaseAuth.getInstance();

        TextView btnRegister = findViewById(R.id.btnForgot);
        loginBtn = findViewById(R.id.btnLogin);

        //by default farmer
        PrefUtil.writePreferenceValue(getApplicationContext(), "LOGIN_TYPE", "FARMER");
        PrefUtil.writeBooleanValue(getApplicationContext(), "is_one_minute_passed", false);
        PrefUtil.writeIntValue(getApplicationContext(), "otp_timer", 0);


        binding.etMobile.setPrefixText("+966");
        // focus on edit text and show keyboard
        binding.etMobile.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.etMobile, InputMethodManager.SHOW_IMPLICIT);

        btnRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        loginBtn.setOnClickListener(view -> {

            if (switchTerm.isChecked()) {
                if (checkForm()) {
                    if (isPhoneNumberValid(Objects.requireNonNull(etMobile.getPrefixText()) + Objects.requireNonNull(etMobile.getEditText()).getText().toString().trim(), countryCode)) {
                        loginBtn.setEnabled(false);
                        loginUser();
                    } else {
                        YoYo.with(Techniques.Shake).playOn(etMobile);
                    }
                }
            } else {
                Util.showToast(getResources().getString(R.string.agree_terms), LoginActivity.this);
            }
        });

        CountryPicker picker = CountryPicker.newInstance("حدد الدولة");  // dialog title
        picker.setListener((name, code, dialCode, flagDrawableResID) -> {

            etMobile.setPrefixText(dialCode);
            tvSelectCountry.setText(name);
            countryCode = code;
            picker.dismiss();

        });

        ActivityResultLauncher<Intent> resultLauncherSelectCity = initResultActivitySelectCity();

        //********************** open country picker *****************//
        btnChooseCountry.setOnClickListener(view -> runActivitySelectCity(resultLauncherSelectCity));


        switchTerm.setOnCheckedChangeListener((compoundButton, isChecked) -> {
        });

        binding.term.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, TermsActivity.class)));

//        etMobile.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
//
//            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
//                return false;
//            }
//
//            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
//                return false;
//            }
//
//            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
//                return false;
//            }
//
//            public void onDestroyActionMode(ActionMode actionMode) {
//            }
//        });

        layoutContact.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ContactUsActivity.class);
            startActivity(intent);
        });

        etMobile.setLongClickable(false);
//        etMobile.setTextIsSelectable(false);

    }


    private void initViews() {
        etMobile = binding.etMobile;
        etPassword = binding.etPassword;
        tvSelectCountry = binding.tvSelectCountry;
        btnChooseCountry = binding.btnChooseCountry;
        switchTerm = binding.switchTerm;
        llProgressBar = binding.llProgressBar.getRoot();
        layoutContact = binding.layoutContact;
    }


    public boolean isPhoneNumberValid(String phoneNumber, String countryCode) {
        //NOTE: This should probably be a member variable.
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(getApplicationContext());
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            String formatted = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            if (!formatted.replaceAll(" ", "").replaceAll("-", "").equals(phoneNumber))
                return false;
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            Log.d("LoGIN", "NumberParseException was thrown: " + e);
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loginUser() {

        llProgressBar.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.loginUser(Objects.requireNonNull(etMobile.getPrefixText()).toString().replace("+", "") + Objects.requireNonNull(etMobile.getEditText()).getText().toString().trim(), roleID, tvSelectCountry.getText().toString(), 2);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                llProgressBar.setVisibility(View.GONE);
                loginBtn.setEnabled(true);
                try {
                    assert response.body() != null;
                    if (response.body().isStatus()) {
                        Intent intent = new Intent(LoginActivity.this, VerifyCodeActivity.class);
                        TokenHelper.createToken(response.body().getMessage());
                        intent.putExtra("MOBILE", Objects.requireNonNull(etMobile.getPrefixText()).toString().replace("+", "") + Objects.requireNonNull(etMobile.getEditText()).getText().toString());
                        intent.putExtra("COUNTRY", tvSelectCountry.getText().toString());
                        intent.putExtra("ROLE_ID", roleID);
                        startActivity(intent);
                        finish();
                    } else {
                        Util.showErrorToast(response.body().getMessage(), LoginActivity.this);
                    }

                } catch (Exception ignored) {
                }

            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                loginBtn.setEnabled(true);
                llProgressBar.setVisibility(View.GONE);
            }
        });

    }

    private boolean checkForm() {

        if (Objects.requireNonNull(etMobile.getEditText()).getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.mobile_number), Toast.LENGTH_SHORT).show();
            YoYo.with(Techniques.Shake).playOn(etMobile);
            return false;
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }


    void runActivitySelectCity(ActivityResultLauncher<Intent> resultLauncher) {
        Intent intent = new Intent(this, SelectCityActivity.class);
        resultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> initResultActivitySelectCity() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if (data == null) {
                return;
            }
            String country = data.getStringExtra("country");
            String dialCode = data.getStringExtra("country_dial");
            String code = data.getStringExtra("country_code");
            if (country != null) {
                countryCode = code;
                tvSelectCountry.setText(country);
                etMobile.setPrefixText(dialCode);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
