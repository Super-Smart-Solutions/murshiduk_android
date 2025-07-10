package com.saatco.murshadik.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.utils.DialogUtil;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessaging;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.QBPushManager;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.QBUsers;
import com.saatco.murshadik.AboutActivity;
import com.saatco.murshadik.App;
import com.saatco.murshadik.ConsultantClipsActivity;
import com.saatco.murshadik.ContactUsActivity;
import com.saatco.murshadik.EditNameActivity;
import com.saatco.murshadik.FaqsActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.InitialActivity;
import com.saatco.murshadik.NewMainActivityDesign;
import com.saatco.murshadik.NotificationSettingActivity;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.ReportBugActivity;
import com.saatco.murshadik.SendGuidedNotificationByConsultants;
import com.saatco.murshadik.adapters.RatingsAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.ApiMethodsHelper;
import com.saatco.murshadik.api.response.RegionResponse;
import com.saatco.murshadik.databinding.FragmentProfileBinding;
import com.saatco.murshadik.fcm.CallService;
import com.saatco.murshadik.fcm.LoginService;
import com.saatco.murshadik.model.City;
import com.saatco.murshadik.model.ConsultantRatings;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {


    ImageView ivProfilePhoto;
    TextView tvRegion;
    TextView tvCountry;
    TextView tvSkills;
    TextView tvName;
    TextView tvCity;
    TextView tvProfile;
    TextView tvMobile;

    LinearLayout layoutWork;
    LinearLayout contactUs;
    LinearLayout aboutUS;

    LinearLayout layoutLogout;
    LinearLayout layoutAlert;
    LinearLayout reportBug;
    LinearLayout layoutProfileInfo;
    LinearLayout layoutFaqs;
    LinearLayout layoutLogo;
    LinearLayout llDeleteAccount;

    ImageView btnPencil;
    TextView tvUserType;
    TextView tvRatingHeading;
    View layout_my_clips;
    View layout_send_guide_notify;


    SwitchCompat onlineSwitch;

    RecyclerView recyclerViewRatings;


    User user;

    View loader;

    protected SharedPrefsHelper sharedPrefsHelper;

    ArrayList<Item> regions = new ArrayList<>();
    ArrayList<City> cities = new ArrayList<>();

    Item region = null;

    private FragmentProfileBinding binding;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initViews();

        getUserProfile();
        getRatings();



        if (user != null) {
            tvUserType.setText(user.isConsultantUser() ? getResources().getString(R.string.consultant) : getResources().getString(R.string.farmer));
        }

        loader = binding.llProgressBar.getRoot();


        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        user = ProfileHelper.getAccount(getContext());

        getRegions();
        getCities();
        getRatings();

        //created by amin
        //sitting up my clips button
        layout_my_clips.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ConsultantClipsActivity.class);
            startActivity(intent);
        });
        //-------------------
        //sitting up my clips button

        layout_send_guide_notify.setOnClickListener(v -> startActivity(new Intent(getActivity(), SendGuidedNotificationByConsultants.class)));
        //-------------------

        onlineSwitch.setOnClickListener(view1 -> {
            boolean isChecked = onlineSwitch.isChecked();
            if (user != null) {
                ApiMethodsHelper.updateOnlineStatus(isChecked);
                user.setOnline(isChecked);
                ApiMethodsHelper.updateUserOnServer(user);
                ProfileHelper.createOrUpdateAccount(user, requireContext());

//                if (getActivity() != null) {
//                    ((NewMainActivityDesign) getActivity()).setProfileStateConnection(isChecked);
//                }
            }
        });
        onlineSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {

        });

        btnPencil.setOnClickListener(view12 -> {

            if (App.isInternetAvailable()) {
                Intent intent = new Intent(getActivity(), EditNameActivity.class);
                startActivity(intent);
            } else
                Util.showToast(getResources().getString(R.string.no_internet_connection), getActivity());
        });

        layoutLogout.setOnClickListener(view13 -> logout());

        llDeleteAccount.setOnClickListener(view1 -> DialogUtil.yesNoDialogWithEnsure(requireContext(), getString(R.string.are_you_sure),
                getString(R.string.account_deletion_msg), 0, msg -> {
                    if (Objects.equals(msg, "yes")) {
                        deleteAccount();
                    }
                }));

        layoutAlert.setOnClickListener(view14 -> {

            Intent intent = new Intent(getActivity(), NotificationSettingActivity.class);
            startActivity(intent);

        });

        contactUs.setOnClickListener(view15 -> {
            Intent intent = new Intent(getActivity(), ContactUsActivity.class);
            startActivity(intent);
        });

        aboutUS.setOnClickListener(view16 -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        reportBug.setOnClickListener(view17 -> {
            Intent intent = new Intent(getActivity(), ReportBugActivity.class);
            startActivity(intent);
        });

        layoutFaqs.setOnClickListener(view18 -> {
            Intent intent = new Intent(getActivity(), FaqsActivity.class);
            startActivity(intent);
        });

        layoutLogo.setOnClickListener(view19 -> {

            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(Consts.WEB_LINK));
            startActivity(viewIntent);

        });

        return view;
    }


    private void initViews() {
        ivProfilePhoto = binding.ivProfilePhoto;
        tvRegion = binding.tvRegion;
        tvCountry = binding.tvCountry;
        tvSkills = binding.tvSkills;
        tvName = binding.tvName;
        tvCity = binding.tvCity;
        tvProfile = binding.tvProfile;
        tvMobile = binding.tvMobile;

        layoutWork = binding.layoutWork;
        contactUs = binding.layoutContact;
        aboutUS = binding.layoutAboutUs;

        layoutLogout = binding.layoutLogout;
        layoutAlert = binding.layoutAlert;
        reportBug = binding.layoutBug;
        layoutProfileInfo = binding.layoutProfileInfo;
        layoutFaqs = binding.layoutFaqs;
        layoutLogo = binding.layoutAboutUs;
        llDeleteAccount = binding.llDeleteAccount;

        btnPencil = binding.btnPencil;
        tvUserType = binding.tvUserType;
        tvRatingHeading = binding.tvRatingHeading;
        layout_my_clips = binding.layoutMyClips;
        layout_send_guide_notify = binding.layoutSendGuideNotify;


        onlineSwitch = binding.switchOnline;

        recyclerViewRatings = binding.rvRatings;
    }

    private void deleteAccount() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.deleteAccount("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                if (response.body() == null) return;
                if (response.body().getStatus()) {
                    logout();
                    ToastUtils.longToast(R.string.account_deleted);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                if (getActivity() == null)
                    ToastUtils.longToast(R.string.error_happend);
                else
                    Util.showErrorToast(getString(R.string.error_happend), requireActivity());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        getUserProfile();
        getRatings();


    }

    private void logout() {
        if (user != null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getPhoneNumber().replaceAll("\\s+", ""));
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getPhoneNumber().replaceAll("\\s+", "") + "-market");
            if (user.getRegionId() != 0)
                FirebaseMessaging.getInstance().unsubscribeFromTopic("group-" + user.getRegionId());

            if (user.getWeatherCode() != null)
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getWeatherCode());

            ApiMethodsHelper.updateOnlineStatus(false);
            user.setOnline(false);
            ApiMethodsHelper.updateUserOnServer(user);

            if (QBChatService.getInstance().isLoggedIn()) {

                if (sharedPrefsHelper.hasQbUser()) {

                    userLogout();

                    if (QBChatService.getInstance() != null) {
                        QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                QBChatService.getInstance().destroy();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                QBChatService.getInstance().destroy();
                            }
                        });
                    }

                    LoginService.stop(App.getInstance());
                    CallService.stop(App.getInstance());
                }
            }

            sharedPrefsHelper.delete("qb_user_login");
            sharedPrefsHelper.delete("qb_user_password");
            sharedPrefsHelper.clearAllData();
            unsubscribeFromPushes();

            PrefUtil.clearPreference(App.getInstance());

            Intent intent = new Intent(getContext(), InitialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finishAffinity();
        } else {

            if (sharedPrefsHelper != null)
                sharedPrefsHelper.clearAllData();
            PrefUtil.clearPreference(App.getInstance());

            Intent intent = new Intent(getContext(), InitialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finishAffinity();
        }
    }


    private void setBitmap(String url) {

        RequestOptions requestOptions = new RequestOptions().override(200, 200).circleCrop();

        if (getContext() != null)
            Glide.with(getContext())
                    .asBitmap()
                    .load(url)
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

    private void getRegions() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RegionResponse> call = apiInterface.getRegionsWithCity();
        call.enqueue(new Callback<RegionResponse>() {
            @Override
            public void onResponse(Call<RegionResponse> call, Response<RegionResponse> response) {

                try {

                    regions = response.body().getRegionList();
                    if (regions != null) {
                        tvRegion.setText(getRegionName());

                        for (Item regionItem : regions) {
                            if (regionItem.getId() == user.getRegionId())
                                region = regionItem;
                        }
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<RegionResponse> call, Throwable t) {

            }
        });


    }

    private String getRegionName() {

        for (Item region : regions) {
            if (region.getId() == user.getRegionId())
                return region.getNameAr();
        }

        return "";
    }

    private void getCities() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<City>> call = apiInterface.getCities();
        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {

                try {

                    cities = (ArrayList<City>) response.body();

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {

            }
        });
    }

    private void getUserProfile() {


        // loader.show();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<User> call = apiInterface.getProfile("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                //  loader.dismiss();

                try {
                    user = response.body();

                    if (user != null) {

                        tvUserType.setText(user.isConsultantUser() ? getResources().getString(R.string.consultant) : getResources().getString(R.string.farmer));

                        onlineSwitch.setChecked(user.isOnline());

                        ProfileHelper.createOrUpdateAccount(user, getContext());
                        String flName = user.getFirstName() + " " + user.getLastName();
                        if (user.getFirstName() != null) {
                            tvName.setText(flName);
                        }

                        if (user.getProfile() != null) {
                            tvProfile.setText(user.getProfile());
                        }

                        if (user.getStatus() != null)
                            tvSkills.setText(user.getStatus());

                        String phoneNumWithPref = "+" + user.getPhoneNumber();
                        tvMobile.setText(phoneNumWithPref);
                        tvSkills.setText(user.getStatus());

                        if (regions != null)
                            tvRegion.setText(getRegionName());


                        if (user.getName() == null)
                            tvName.setText("");
                        else {
                            tvName.setText(flName);
                        }

                        if (!user.isProfileComplete() || user.getName().contains(user.getPhoneNumber()))
                            tvName.setText("");

                        if (user.getPhotoUrl() != null) {
                            setBitmap(APIClient.imageUrl + user.getPhotoUrl());
                        }

                        if (user.getCountry() != null) {
                            tvCountry.setText(user.getCountry());
                        }

                        if (user.getPrefix() != null)
                            tvCity.setText(user.getPrefix());


                        if (user.isFarmer()) // if farmer
                        {
                            layoutWork.setVisibility(View.GONE);
                            layoutProfileInfo.setVisibility(View.GONE);
                            recyclerViewRatings.setVisibility(View.GONE);
                            tvRatingHeading.setVisibility(View.GONE);

                            //added by amin
                            //consultant clips button
                            layout_my_clips.setVisibility(View.GONE);
                            layout_send_guide_notify.setVisibility(View.GONE);
                        } else {
                            layoutWork.setVisibility(View.VISIBLE);
                            layoutProfileInfo.setVisibility(View.VISIBLE);
                            recyclerViewRatings.setVisibility(View.VISIBLE);
                            tvRatingHeading.setVisibility(View.VISIBLE);

                            //added by amin
                            //consultant clips button
                            layout_my_clips.setVisibility(View.VISIBLE);
                            layout_send_guide_notify.setVisibility(View.VISIBLE);
                        }

                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // loader.dismiss();
            }
        });
    }

    // Takes an arraylist as a parameter and returns
    // a reversed arraylist
    public ArrayList<ConsultantRatings> reverseArrayList(ArrayList<ConsultantRatings> alist) {
        // Arraylist for storing reversed elements
        ArrayList<ConsultantRatings> revArrayList = new ArrayList<ConsultantRatings>();
        for (int i = alist.size() - 1; i >= 0; i--) {

            // Append the elements in reverse order
            revArrayList.add(alist.get(i));
        }

        // Return the reversed arraylist
        return revArrayList;
    }

    private void getRatings() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<ConsultantRatings>> call = apiInterface.getRatings("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<List<ConsultantRatings>>() {
            @Override
            public void onResponse(Call<List<ConsultantRatings>> call, Response<List<ConsultantRatings>> response) {

                try {

                    ArrayList<ConsultantRatings> consultantRatings = (ArrayList<ConsultantRatings>) response.body();

                    if (consultantRatings == null || consultantRatings.size() < 1) {
                        recyclerViewRatings.setVisibility(View.GONE);
                        //spacerRating.setVisibility(View.GONE);
                    }

                    RatingsAdapter adapter = new RatingsAdapter(reverseArrayList(consultantRatings), getContext());
                    recyclerViewRatings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    recyclerViewRatings.setAdapter(adapter);
                    recyclerViewRatings.setItemAnimator(new DefaultItemAnimator());
                    adapter.notifyDataSetChanged();


                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<List<ConsultantRatings>> call, Throwable t) {

            }
        });
    }

    private void unsubscribeFromPushes() {
        if (QBPushManager.getInstance().isSubscribedToPushes()) {
            QBPushManager.getInstance().addListener(new QBPushManager.QBSubscribeListener() {
                @Override
                public void onSubscriptionCreated() {
                    Log.v("sub been ceated", "sub been ceated");

                }

                @Override
                public void onSubscriptionError(Exception e, int i) {


                }

                @Override
                public void onSubscriptionDeleted(boolean success) {
                    QBPushManager.getInstance().removeListener(this);
                    userLogout();
                }
            });
            SubscribeService.unSubscribeFromPushes(getContext());
        }
    }

    private void userLogout() {

        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                SharedPrefsHelper.getInstance().removeQbUser();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getUserProfile();
        }

    }



}