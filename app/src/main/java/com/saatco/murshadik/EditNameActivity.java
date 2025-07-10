package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.saatco.murshadik.Helpers.PermissionsHelper;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.FileResponse;
import com.saatco.murshadik.api.response.RegionResponse;
import com.saatco.murshadik.api.response.UserResponse;

import com.saatco.murshadik.views.EditWithBorderTitle;
import com.saatco.murshadik.databinding.ActivityEditNameBinding;
import com.saatco.murshadik.fcm.LoginService;
import com.saatco.murshadik.model.City;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ImageUtils;
import com.saatco.murshadik.utils.QBResRequestExecutor;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.StorageUtil;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.views.ScrollableEditText;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNameActivity extends AppCompatActivity {

    EditWithBorderTitle firstName;
    EditWithBorderTitle lastName;
    ScrollableEditText profile;
    EditWithBorderTitle tvSkills;
    EditWithBorderTitle tvRegion;
    EditWithBorderTitle tvCity;
    ImageView btnProfilePic;
    TextView changePhoneNumber;
    ImageView imageView;
    Button btnRegister;

    private final static int GALLERY_PICK_CODE = 2;
    public static final int REQUEST_SELECT_CITY = 900;
    public static final int RESULT_SELECT_CITY_OK = 901;

    User user = null;

    String weatherCode = null;

    Item region = null;
    ArrayList<Item> regions = new ArrayList<>();

    protected SharedPrefsHelper sharedPrefsHelper;


    protected QBResRequestExecutor requestExecutor;

    private QBUser userForSave;

    private int cityId = 0;

    ActivityEditNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));

        user = ProfileHelper.getAccount(getApplicationContext());

        if (requestExecutor == null)
            requestExecutor = new QBResRequestExecutor();

        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        if (sharedPrefsHelper.hasQbUser())
            userForSave = sharedPrefsHelper.getQbUser();

        startSignUpNewUser(createUserWithEnteredData());

        getRegions();
        getSkills();
        setUserDetails();

        tvRegion.setOnClickListener(view -> showRegionDialog());

        tvSkills.setOnClickListener(view -> {

            if (user.getRoleId() == 5) {
                Intent intent = new Intent(EditNameActivity.this, SelectMultiCategoryActivity.class);
                intent.putExtra("IS_FARMER", true);
                intent.putExtra("SKILLS", tvSkills.getText().toString());
                startActivityForResult(intent, REQUEST_SELECT_CITY);
            } else {
                Intent intent = new Intent(EditNameActivity.this, SelectSkillActivity.class);
                startActivity(intent);
            }

        });

        btnRegister.setOnClickListener(view -> {


            if (user.getRoleId() == 5) {
                if (checkFarmerForm())
                    editProfile();
            } else {
                if (checkConsultantForm())
                    editProfile();
            }

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
                Util.showToast("منطقة مطلوب", EditNameActivity.this);

        });

        changePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditNameActivity.this, ChangePhoneNumberActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    void initViews() {
        firstName = binding.etbFirstName;
        lastName = binding.etbLastName;
        profile = binding.etbProfileCv;
        tvSkills = binding.etbSkills;
        tvRegion = binding.etbRegion;
        tvCity = binding.etbCity;
        imageView = binding.ivProfilePhoto;
        btnRegister = binding.btnDone;
        btnProfilePic = binding.btnProfilePic;
        changePhoneNumber = binding.changePhoneNumber;
    }

    private void setUserDetails() {

        if (user != null) {
            tvSkills.setText(user.getStatus());

            if (regions != null)
                tvRegion.setText(user.getLocation());

            if (user.getName().contains(user.getPhoneNumber())) {
                firstName.setText("");
                lastName.setText("");
            } else {
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
            }

            if (user.getPhotoUrl() != null) {
                setBitmap(APIClient.imageUrl + user.getPhotoUrl());
            }

            if (user.getPrefix() != null)
                tvCity.setText(user.getPrefix());

            if (user.isFarmer()) {
                profile.setVisibility(View.GONE);
                tvSkills.setTitle(" أختر النشاط الزراعي");
            }

            if (user.getProfile() != null) {
                profile.setText(user.getProfile());
            }

            if (user.getGovernorate() != null) {
                cityId = Integer.parseInt(user.getGovernorate());
            }
        }
    }

    private boolean checkConsultantForm() {

        if (firstName.getText().toString().isEmpty()) {
            Util.showToast(getResources().getString(R.string.enter_first_name), EditNameActivity.this);
            return false;
        }

        if (lastName.getText().toString().isEmpty()) {
            Util.showToast(getResources().getString(R.string.enter_last_name), EditNameActivity.this);
            return false;
        }

        if (tvRegion.getText().toString().isEmpty()) {
            Util.showToast(getResources().getString(R.string.select_region), EditNameActivity.this);
            return false;
        }

        if (profile.getText().isEmpty()) {
            Util.showToast(getResources().getString(R.string.enter_profile), EditNameActivity.this);
            return false;
        }

        if (tvCity.getText().toString().isEmpty()) {
            Util.showToast(getResources().getString(R.string.select_city), EditNameActivity.this);
            return false;
        }

        return true;
    }

    private boolean checkFarmerForm() {

        if (firstName.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.enter_first_name), EditNameActivity.this);
            return false;
        }

        if (lastName.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.enter_last_name), EditNameActivity.this);
            return false;
        }

        if (tvSkills.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.select_skill), EditNameActivity.this);
            return false;
        }


        if (tvRegion.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.select_region), EditNameActivity.this);
            return false;
        }

        if (tvCity.getText().toString().equals("")) {
            Util.showToast(getResources().getString(R.string.select_city), EditNameActivity.this);
            return false;
        }

        return true;
    }

    private void showRegionDialog() {

        final Dialog dialog = new Dialog(EditNameActivity.this);
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
                user.setLocation(region.getNameAr());
                user.setRegionId(region.getId());
                tvCity.setText("");
                dialog.dismiss();

            }

        });


        dialog.show();

    }

    private void showCityDialog() {

        final Dialog dialog = new Dialog(EditNameActivity.this);
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
                if (user.getWeatherCode() != null)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getWeatherCode());
                user.setWeatherCode(weatherCode == null ? "OERK" : weatherCode);
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

                    for (Item regionItem : regions) {
                        if (regionItem.getId() == user.getRegionId()) {
                            region = regionItem;
                            break;
                        }
                    }

                    if (region != null) {
                        tvRegion.setText(region.getNameAr());
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<RegionResponse> call, Throwable t) {

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

    private void editProfile() {

        binding.progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UserResponse> call = apiInterface.updateProfile("Bearer " + TokenHelper.getToken(), firstName.getText().toString(), lastName.getText().toString(), user.getPhoneNumber(), user.getRoleId(), 2, user.getRegionId(), "المملكة العربية السعودية", user.getStatus(), profile.getText().toString(), tvCity.getText().toString(), user.getWeatherCode(), null, cityId);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {

                binding.progressBar.setVisibility(View.GONE);

                try {

                    if (response.body() != null && response.body().isStatus()) {

                        user = response.body().getUser();
                        ProfileHelper.createOrUpdateAccount(user, getApplicationContext());
                        TokenHelper.createToken(response.body().getMessage());
                        updateUserOnServer();


                        // ************************** register fcm  topics ***********************//
                        if (user.getWeatherCode() != null) {
                            FirebaseMessaging.getInstance().subscribeToTopic(user.getWeatherCode()).addOnSuccessListener(aVoid -> {

                            });
                        }

                        if (user.getRegionId() != 0) {
                            FirebaseMessaging.getInstance().subscribeToTopic(Consts.GROUP_FCM_TOPIC + user.getRegionId()).addOnSuccessListener(aVoid -> {
                            });
                        }

                        finish();

                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {

                binding.progressBar.setVisibility(View.GONE);

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
                uploadUserPhoto(uri, file);

            }

        }

        if (requestCode == REQUEST_SELECT_CITY) {
            if (resultCode == RESULT_SELECT_CITY_OK) {

                String city = data.getStringExtra("city");
                if (city != null) {
                    user.setStatus(city);
                    tvSkills.setText(city);
                }
            }
        }

        if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);

            if (isLoginSuccess) {
                if (!sharedPrefsHelper.hasQbUser())
                    saveUserData(userForSave);
                signInCreatedUser(userForSave);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void uploadUserPhoto(final Uri fileUri, File file) {

        RequestBody requestFile =
                RequestBody.create(
                        file,
                        MediaType.parse(Objects.requireNonNull(getContentResolver().getType(fileUri)))
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
                    Util.showSuccessToast("Profile picture has been updated", EditNameActivity.this);
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {

            }
        });
    }

    private void updateUserOnServer() {

        if (sharedPrefsHelper.hasQbUser()) {

            User user = ProfileHelper.getAccount(getApplicationContext());
            user.setChatId(String.valueOf(sharedPrefsHelper.getQbUser().getId()));
            ProfileHelper.createOrUpdateAccount(user, getApplicationContext());


            StringifyArrayList<String> tags = new StringifyArrayList<>();
            tags.add(user.getRoleId() == 5 ? "Farmer" : "Consultant");

            HashMap<Object, Object> map = new HashMap<>();
            map.put("is_online", user.isOnline());
            map.put("id", user.getId());
            map.put("avatar", user.getPhotoUrl());
            map.put("phone", user.getPhoneNumber());
            map.put("role_id", user.getRoleId());

            QBCustomObject customObject = new QBCustomObject();

            customObject.putString("phone", user.getPhoneNumber());
            customObject.putInteger("id", user.getId());
            customObject.putInteger("role_id", user.getRoleId());
            customObject.putBoolean("is_online", user.isOnline());
            customObject.setClassName("user");

            if (!user.getName().equals("")) {
                QBUser qbUser = sharedPrefsHelper.getQbUser();
                qbUser.setFullName(firstName.getText().toString() + " " + lastName.getText().toString());
                qbUser.setPassword(null);
                qbUser.setCustomData(new JSONObject(map).toString());
                qbUser.setPhone(user.getPhoneNumber());
                qbUser.setTags(tags);
                QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                        sharedPrefsHelper.saveQbUser(qbUser);
                        Log.v("QBUser", "Updated ::: " + qbUser);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.v("QBUser", "Updated Error::: " + e.getLocalizedMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    private QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData(String.valueOf(ProfileHelper.getAccount(getApplicationContext()).getId()), firstName.getText().toString() + " " + lastName.getText().toString());
    }

    private QBUser createQBUserWithCurrentData(String userLogin, String userFullName) {
        QBUser qbUser = null;
        if (!TextUtils.isEmpty(userLogin) && !TextUtils.isEmpty(userFullName)) {
            qbUser = new QBUser();
            qbUser.setLogin(userLogin);
            qbUser.setFullName(userFullName);
            qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        }
        return qbUser;
    }

    private void startSignUpNewUser(final QBUser newUser) {
        Log.d("QBX", "SignUp New User");
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        Log.d("QBX", "SignUp Successful");
                        saveUserData(newUser);
                        loginToChat(result);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser);
                        }
                    }
                }
        );
    }

    private void signInCreatedUser(final QBUser qbUser) {

        requestExecutor.signInUser(qbUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle params) {

                if (userForSave != null)
                    sharedPrefsHelper.saveQbUser(userForSave);
                else {
                    userForSave = user;
                    userForSave.setPassword(App.USER_DEFAULT_PASSWORD);
                    sharedPrefsHelper.saveQbUser(userForSave);
                }

                PrefUtil.writeIntValue(getApplicationContext(), "my_chat_id", user.getId());

                if (!QBChatService.getInstance().isLoggedIn()) {
                    if (sharedPrefsHelper.hasQbUser()) {
                        QBUser qbUser2 = sharedPrefsHelper.getQbUser();
                        LoginService.start(getApplicationContext(), qbUser2);
                        PrefUtil.writeIntValue(getApplicationContext(), "my_chat_id", qbUser2.getId());
                        updateQuickBlox(qbUser2.getId());
                        updateQuickBlox(qbUser.getId());
                    }
                }

                updateUserOnServer();
                updateQuickBlox(qbUser.getId());
            }

            @Override
            public void onError(QBResponseException responseException) {
                Log.d("QBX", "Error SignIn" + responseException.getMessage());
            }
        });
    }

    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        startLoginService(qbUser);
    }

    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, LoginService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        LoginService.start(this, qbUser, pendingIntent);
    }


    private void updateQuickBlox(int chatId) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> userResponseCall = apiInterface.updateChatId("Bearer " + TokenHelper.getToken(), String.valueOf(chatId));
        userResponseCall.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.saveQbUser(qbUser);
        userForSave = sharedPrefsHelper.getQbUser();
    }
}