package com.saatco.murshadik;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.Helpers.PermissionsHelper;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.FileResponse;
import com.saatco.murshadik.api.response.RegionResponse;
import com.saatco.murshadik.api.response.UserResponse;
import com.saatco.murshadik.views.EditWithBorderTitle;
import com.saatco.murshadik.databinding.ActivityRegisterBinding;
import com.saatco.murshadik.model.City;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ImageUtils;
import com.saatco.murshadik.utils.StorageUtil;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.views.ScrollableEditText;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {


    EditWithBorderTitle firstName;
    EditWithBorderTitle lastName;
    ScrollableEditText profile;
    EditWithBorderTitle tvSkills;
    EditWithBorderTitle tvRegion;
    EditWithBorderTitle tvCity;
    TextView tvMobile;
    ImageView btnProfilePic;
    ImageView  imageView;
    Button btnRegister;

    ArrayList<Item> regions = new ArrayList<>();

    public static final int REQUEST_SELECT_CITY = 900;
    public static final int RESULT_SELECT_CITY_OK = 901;

    private final static int GALLERY_PICK_CODE = 2;

    ProgressDialog progressDialog;

    String skillIds = null;

    ArrayList<City> cities = new ArrayList<>();

    String weatherCode = "";

    Item region = null;

    ArrayList<Item> selectedSkills = new ArrayList<>();

    int cityId = 1;

    ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();

        ArrayList<Item> regionsList = new ArrayList<>();
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(), "regions");
        Type type = new TypeToken<ArrayList<Item>>() {
        }.getType();
        regionsList = gson.fromJson(positions, type);

        if (regionsList != null) {
            regions = regionsList;
        }

        //*********************** api calls *********************** //
        getRegions();
        getSkills();

        if (ProfileHelper.getAccount(getApplicationContext()) != null) {
            String phone = ProfileHelper.getAccount(getApplicationContext()).getPhoneNumber() + "+";
            tvMobile.setText(phone);
        }

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("ارجوا الانتظار...");
        progressDialog.setCancelable(false);

        tvRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showRegionDialog();
            }
        });

        tvSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegisterActivity.this, SelectMultiCategoryActivity.class);
                intent.putExtra("SELECTEDS", selectedSkills);
                intent.putExtra("IS_REGISTER", true);
                intent.putExtra("IS_FARMER", ProfileHelper.getAccount(RegisterActivity.this).isFarmer());
                startActivityForResult(intent, REQUEST_SELECT_CITY);
            }
        });

        btnRegister.setOnClickListener(view -> {


            if (checkConsultantForm())
                editProfile();

        });

        imageView.setOnClickListener(view -> {

            if (PermissionsHelper.isMediaImagePermissionGranted(this)) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_PICK_CODE);
            }

        });

        btnProfilePic.setOnClickListener(view -> {

            if (PermissionsHelper.isMediaImagePermissionGranted(this)) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_PICK_CODE);
            }
        });

        tvCity.setOnClickListener(view -> {

            if (region != null)
                showCityDialog();
            else
                Util.showToast(getResources().getString(R.string.select_city), RegisterActivity.this);

        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });
    }

    void initViews() {
        firstName = binding.etbFirstName;
        lastName  = binding.etbLastName;
        profile = binding.etbProfileCv;
        tvSkills = binding.etbSkills;
        tvRegion = binding.etbRegion;
        tvCity = binding.etbCity;
        tvMobile = binding.tvMobile;
        imageView = binding.ivProfilePhoto;
        btnRegister = binding.btnDone;
        btnProfilePic = binding.btnProfilePic;

    }

    private void getSkills() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getSkills();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {

                try {

                    if (response.body() != null) {
                        Gson gson = new Gson();
                        String jsonCat = gson.toJson(response.body());
                        PrefUtil.writePreferenceValue(getApplicationContext(), "CATEGORY_CONSULTANT", jsonCat);
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {

            }
        });
    }

    private boolean checkConsultantForm() {

        if (firstName.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.enter_first_name), RegisterActivity.this);
            return false;
        }

        if (lastName.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.enter_last_name), RegisterActivity.this);
            return false;
        }

        if (tvSkills.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.select_skill), RegisterActivity.this);
            return false;
        }

        if (tvRegion.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.select_region), RegisterActivity.this);
            return false;
        }

        if (profile.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.enter_profile), RegisterActivity.this);
            return false;
        }

        if (tvCity.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.select_city), RegisterActivity.this);
            return false;
        }

        return true;
    }

    private void showRegionDialog() {

        final Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.dialog_regions);

        ListView listView = dialog.findViewById(R.id.statusListView);

        ArrayList<String> regionList = new ArrayList<>();

        for (Item region : regions)
            regionList.add(region.getNameAr());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, regionList);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);
                tvRegion.setText(itemValue);
                region = regions.get(position);
                tvCity.setText("");

                dialog.dismiss();

            }

        });


        dialog.show();

    }

    private void showCityDialog() {

        final Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.dialog_regions);

        ListView listView = dialog.findViewById(R.id.statusListView);

        ArrayList<String> cityList = new ArrayList<>();

        for (City city : region.getCities())
            cityList.add(city.getNameAr());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, cityList);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);
                tvCity.setText(region.getCities().get(position).getNameAr());
                weatherCode = region.getCities().get(position).getWeatherIdentifier();
                cityId = region.getCities().get(position).getId();

                dialog.dismiss();

            }

        });

        dialog.show();

    }

    private void getRegions() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RegionResponse> call = apiInterface.getRegionsWithCity();
        call.enqueue(new Callback<RegionResponse>() {
            @Override
            public void onResponse(Call<RegionResponse> call, Response<RegionResponse> response) {

                try {

                    regions = response.body().getRegionList();

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<RegionResponse> call, Throwable t) {

            }
        });


    }

    @Override // for gallery picking
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  For image sending
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            File file = new File(StorageUtil.getRealPathFromURIPath(uri, this));
            File reducedSizeFile = ImageUtils.getScaledImage2(getApplicationContext(), file);
            Bitmap reducedSizeBitmap = null;

            try {
                reducedSizeBitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), Uri.fromFile(reducedSizeFile));

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (reducedSizeBitmap != null) {
                setBitmap(uri.toString());
                uploadUserPhoto(uri, reducedSizeFile);
            }

        }

        if (requestCode == REQUEST_SELECT_CITY) {
            if (resultCode == RESULT_SELECT_CITY_OK) {

                String city = data.getStringExtra("city");
                String ids = data.getStringExtra("ids");

                if (city != null) {
                    tvSkills.setText(city);
                    skillIds = ids;
                    selectedSkills = (ArrayList<Item>) data.getSerializableExtra("SELECTEDS");
                    Log.v("SKILL_IDS", skillIds);
                }
            }
        }

    }

    private void uploadUserPhoto(final Uri fileUri, File file) {

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<FileResponse> call = apiInterface.uploadUserPhoto("Bearer " + TokenHelper.getToken(), body);
        call.enqueue(new Callback<FileResponse>() {
            @Override
            public void onResponse(Call<FileResponse> call, Response<FileResponse> response) {


                try {
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {

            }
        });
    }

    private void setBitmap(String url) {

        RequestOptions requestOptions = new RequestOptions().override(200, 200).circleCrop();

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(url)
                .apply(requestOptions)
                .placeholder(R.drawable.profile_photo)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        final float roundPx = (float) resource.getWidth() * 2f;
                        roundedBitmapDrawable.setCornerRadius(roundPx);

                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    private void editProfile() {

        progressDialog.show();

        User user = ProfileHelper.getAccount(getApplicationContext());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UserResponse> call = apiInterface.updateProfile("Bearer " + TokenHelper.getToken(), firstName.getText().toString(), lastName.getText().toString(), user.getPhoneNumber(), user.getRoleId(), 2, region.getId(), "المملكة العربية السعودية", tvSkills.getText().toString(), profile.getText(), tvCity.getText().toString(), weatherCode.equals("") ? "OERK" : weatherCode, skillIds.length() > 1 ? skillIds.substring(0, skillIds.length() - 1) : null, cityId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                progressDialog.hide();

                try {

                    if (response.body().isStatus()) {

                        User user = response.body().getUser();
                        TokenHelper.createToken(response.body().getMessage());

                        //*********************** register fcm ************************//

                        FirebaseMessaging.getInstance().subscribeToTopic(user.getPhoneNumber().replaceAll("\\s+", "")).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                        FirebaseMessaging.getInstance().subscribeToTopic(user.getWeatherCode()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        if (user.getRegionId() != 0) {
                            FirebaseMessaging.getInstance().subscribeToTopic(Consts.GROUP_FCM_TOPIC + user.getRegionId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                        }


                        ProfileHelper.createOrUpdateAccount(user, getApplicationContext());
                        Intent intent = new Intent(RegisterActivity.this, NewMainActivityDesign.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_register), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_register), Toast.LENGTH_SHORT).show();
                progressDialog.hide();
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
