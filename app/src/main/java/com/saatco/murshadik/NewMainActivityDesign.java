package com.saatco.murshadik;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.QBPushManager;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.saatco.murshadik.Helpers.AppRater;
import com.saatco.murshadik.Helpers.DatabaseHelper;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.FragmentsNewUiDesignAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.ApiMethodsHelper;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.WeatherResponse;
import com.saatco.murshadik.databinding.ActivityNewMainDesignBinding;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.enums.MainViewsId;
import com.saatco.murshadik.fcm.LoginService;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Notifications;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewMainActivityDesign extends BaseActivity {

    private ActivityNewMainDesignBinding binding;

    //old main activity public variables
    //=========================

    //Quickblox
    private QBUser userForSave;

    DatabaseHelper databaseHelper;

    User myProfile = null;

    //========================


    ViewPager2 mViewPager2;


    // weather variables
    //=========================
    public String weatherTemperature;
    public int weatherResIcon;
    public String weatherPhenomenon;

    //youtube variables
    //=========================
    public String youtubeVideoUrl;

    //agriculture note variables
    //=========================
    public ArrayList<Item> agricultureNoteItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewMainDesignBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // handle when user press back btn in profile or notification fragments
                if (mViewPager2.getCurrentItem() != 0) {
                    binding.appBarNewMainDesign.navBar.setSelectedItemId(R.id.home);
                } else {
                    finish();
                }
            }
        });

        mViewPager2 = findViewById(R.id.view_pager2);
        mViewPager2.setAdapter(new FragmentsNewUiDesignAdapter(this));
        mViewPager2.setOffscreenPageLimit(5);
        mViewPager2.setUserInputEnabled(false);


        binding.appBarNewMainDesign.navBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home)
                mViewPager2.setCurrentItem(MainViewsId.HOME.id);
            else if (item.getItemId() == R.id.services)
                mViewPager2.setCurrentItem(MainViewsId.SERVICES.id);
            else if (item.getItemId() == R.id.my_appointments)
                mViewPager2.setCurrentItem(MainViewsId.APPOINTMENTS.id);
            else if (item.getItemId() == R.id.notification)
                mViewPager2.setCurrentItem(MainViewsId.NOTIFICATION.id);
            else if (item.getItemId() == R.id.profile)
                mViewPager2.setCurrentItem(MainViewsId.PROFILE.id);

            return true;
        });

        askNotificationPermission();
        oldMainActivityOnCreate();
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    DialogUtil.yesNoDialog(this, getString(R.string.permission_needed), getString(R.string.notification_permission_denide_message_with_rerequest), msg -> {
                        if (msg.equals("yes")) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                DialogUtil.yesNoDialog(this, getString(R.string.permission_needed), getString(R.string.notification_permission_denide_message_with_rerequest), msg -> {
                    if (msg.equals("yes")) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }
                });

            }
        }
    }

    private void showHideNotificationBadge(boolean is_show) {
//        if (!is_show) {
//            BadgeDrawable badgeDrawable = binding.appBarNewMainDesign.navBar.getOrCreateBadge(R.id.notification);
//            badgeDrawable.setNumber(5);
//            badgeDrawable.setVisible(true);
//        } else {
//            BadgeDrawable badgeDrawable = binding.appBarNewMainDesign.navBar.getBadge(R.id.notification);
//            if (badgeDrawable != null) {
//                badgeDrawable.setVisible(false);
//                badgeDrawable.clearNumber();
//            }
//            StorageHelper.saveNotificationsCounter(0);
//        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.new_main_design, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();


        //created by amin
        //making sure that user hase register his data
        myProfile = ProfileHelper.getAccount(getApplicationContext());
        if (myProfile != null) {
            // ********* navigate to register if user is consultant and profile in not completed **********/
            if (!myProfile.isProfileComplete())
                startActivity(new Intent(this, RegisterActivity.class));
        }

        //************ hide Notification bubble **************//

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (getUnreadMessageCount() > 0)
            notificationManager.cancel(5);
        else
            notificationManager.cancelAll();

        ShortcutBadger.removeCount(getApplicationContext()); //for 1.1.4+
        PrefUtil.writeIntValue(this, Consts.SHORTCUT_BADGER_COUNTER, 0);


    }

    private int getUnreadMessageCount() {
        if (StorageHelper.getAllMessages() != null)
            return StorageHelper.getAllMessages().size();
        else return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        //************ unregister local broadcast rcvs **************//

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mNotificationClick);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);

            if (isLoginSuccess) {
                if (!sharedPrefsHelper.hasQbUser())
                    saveUserData(userForSave);
                signInCreatedUser(userForSave);
            }
        }
    }

    private void getWeather() {

        String weather = "OERK";

        if (ProfileHelper.getAccount(this) != null)
            weather = ProfileHelper.getAccount(this).getWeatherCode();


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<WeatherResponse> call = apiInterface.getWeather(weather);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {

                try {

                    if (response.body() == null) return;

                    String icon = response.body().getCurrentIcon().replaceAll(" .*", "").replace("-", "_");

                    Log.v("Weather", icon);

//                    binding.appBarNewMainDesign.tvWeather.setText(response.body().getCurrentTemperature().concat("°c"));
//                    int resId = WeatherIconsMap.getIconByName(icon);
//                    binding.appBarNewMainDesign.ivWeather.setImageResource(resId);
//                    binding.appBarNewMainDesign.tvWeatherCondition.setText(response.body().getWeather_PhenomenonAr());

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void oldMainActivityOnCreate() {
        //app version update
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE);
            }
        });

        //app rate in google play
        // Show a dialogue
        // if meets conditions
        new AppRater(this, getPackageName()).show();


        PrefUtil.writeBooleanValue(getApplicationContext(), "is_first_name", true);

        databaseHelper = new DatabaseHelper(getApplicationContext());


        PrefUtil.writeBooleanValue(getApplicationContext(), "is_first_name", true);
        PrefUtil.writePreferenceValue(getApplicationContext(), "ACTIVE_USER", "");

        //if user logged in
        if (ProfileHelper.hasAccount(this)) {

            myProfile = ProfileHelper.getAccount(getApplicationContext());

            // ********* navigate to register if user is consultant and profile in not completed **********/
            if (!myProfile.isProfileComplete())
                startActivity(new Intent(this, RegisterActivity.class));

            // created by Amin
            //---------- make is_online true if user opened the app from turning of is_online notify
            if (getIntent().getBooleanExtra(Consts.IS_START_FROM_ALARM_IS_ONLINE, false) && myProfile.isConsultantUser() && !myProfile.isOnline()) {
                ApiMethodsHelper.updateOnlineStatus(true, msg -> Util.showSuccessToast(getString(R.string.is_online_btn_on_agine), this));
                myProfile.setOnline(true);
            }

            if (sharedPrefsHelper.hasQbUser())
                userForSave = sharedPrefsHelper.getQbUser();

            // ********* signup into quickblox SDK **********/
            startSignUpNewUser(createUserWithEnteredData());
            getUserProfile();

            // ********* user status topic **********/
            FirebaseMessaging.getInstance().subscribeToTopic("UserStatus").addOnSuccessListener(aVoid -> {
            });

        }


        //request bluetooth access permission for android version 12+
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return;
        }

        //************ check if 24 passed after last chat show ratings screen to rate last consultant **************//
        if (!PrefUtil.getStringPref(getApplicationContext(), "consultant_time").isEmpty()) {
            long savedMillis = Long.parseLong(PrefUtil.getStringPref(getApplicationContext(), "consultant_time"));

            if (System.currentTimeMillis() >= savedMillis + Consts.HOURS_FORMAT_24 && ProfileHelper.getAccount(getApplicationContext()).isFarmer()) {
                Intent intent = new Intent(this, RatingActivity.class);
                intent.putExtra("consultant_id", PrefUtil.getInteger(App.getInstance(), "USER_ID"));
                intent.putExtra("consultant_name", PrefUtil.getStringPref(App.getInstance(), "consultant_name"));
                startActivity(intent);
            }
        }

        //************ api calls **************//
        backgroundApisCall();


    }

    private void backgroundApisCall() {
        new Thread(() -> {

            //topic
            FirebaseMessaging.getInstance().subscribeToTopic("Developers").addOnSuccessListener(aVoid -> {
            });

            QBPushManager.getInstance().addListener(new QBPushManager.QBSubscribeListener() {
                @Override
                public void onSubscriptionCreated() {
                    Log.d("Subscription Created: ", " ================== ");


                }

                @Override
                public void onSubscriptionError(Exception exception, int resultCode) {
                    if (resultCode == -1)
                        if (Objects.requireNonNull(exception.getMessage()).contains("Unable to create a subscription because of exceeded maximum of subscriptions")) {
                            getSubscriptionPushesFromQBAndDeletePushes();
                        }
                    if (resultCode >= 0) {
                        // might be Google play service exception
                        Log.d("SubscriptionError: ", "result code = " + resultCode, exception);
                    }
                }

                @Override
                public void onSubscriptionDeleted(boolean delete) {
                }
            });

            QBSessionManager.getInstance().addListener(new QBSessionManager.QBSessionListener() {
                @Override
                public void onSessionCreated(QBSession session) {
                    // calls when session was created firstly or after it has been expired
                }

                @Override
                public void onSessionUpdated(QBSessionParameters sessionParameters) {
                    // calls when user signed in or signed up
                    // QBSessionParameters stores information about signed in user.
                    sessionParameters.getAccessToken();

                }

                @Override
                public void onSessionDeleted() {
                    // calls when user signed Out or session was deleted
                }

                @Override
                public void onSessionRestored(QBSession session) {
                    // calls when session was restored from local storage
                }

                @Override
                public void onSessionExpired() {
                    // calls when session is expired
                }

                @Override
                public void onProviderSessionExpired(String provider) {
                    // calls when provider's access token is expired or invalid
                }
            });

            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                    new IntentFilter("alert_msg"));

            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mNotificationClick,
                    new IntentFilter("notification_click"));

            getSkills();
            getNotifications();
        }).start();
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getNotifications();
        }
    };

    private final BroadcastReceiver mNotificationClick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //************ switch to notification tab **************//
            mViewPager2.setCurrentItem(MainViewsId.NOTIFICATION.id);
            //mViewPager.setCurrentItem(4);
        }
    };


    //QuickBlox
    private void startSignUpNewUser(final QBUser newUser) {
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        saveUserData(newUser);
                        userForSave = sharedPrefsHelper.getQbUser();
                        loginToChat(result);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            userForSave = sharedPrefsHelper.getQbUser();
                            signInCreatedUser(newUser);
                        }
                    }
                }
        );
    }


    private void signInCreatedUser(final QBUser qbUser) {

        requestExecutor.signInUser(qbUser, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                if (userForSave == null) {
                    userForSave = user;
                    userForSave.setPassword(App.USER_DEFAULT_PASSWORD);
                }
                sharedPrefsHelper.saveQbUser(userForSave);

                PrefUtil.writeIntValue(getApplicationContext(), "my_chat_id", user.getId());

                if (!QBChatService.getInstance().isLoggedIn()) {
                    if (sharedPrefsHelper.hasQbUser()) {
                        QBUser qbUser2 = sharedPrefsHelper.getQbUser();
                        LoginService.start(getApplicationContext(), qbUser2);
                        PrefUtil.writeIntValue(getApplicationContext(), "my_chat_id", qbUser2.getId());
                        updateQuickBlox(qbUser2.getId());
                        updateQBUser();
                        updateQuickBlox(qbUser.getId());
                    }
                }
                updateQuickBlox(qbUser.getId());
                updateQBUser();
                //************ update app verison **************//
                updateAppVersion();

            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("QBX", "Error SignIn" + e.getMessage());
            }
        });
    }


    private void updateQuickBlox(int chatId) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> userResponseCall = apiInterface.updateChatId("Bearer " + TokenHelper.getToken(), String.valueOf(chatId));
        userResponseCall.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {

            }
        });
    }

    //************ send app details on server **************//
    private void updateAppVersion() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> userResponseCall = apiInterface.updateAppVersion("Bearer " + TokenHelper.getToken(), Util.getBuildDetails(), "24.915154,67.037732", 2);
        userResponseCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {

            }
        });
    }

    //************ update profile in QB SDK **************//
    private void updateQBUser() {
        if (ProfileHelper.getAccount(getApplicationContext()) != null) {
            if (sharedPrefsHelper.hasQbUser()) {

                if (ProfileHelper.getAccount(getApplicationContext()).getName() != null) {
                    QBUser qbUser = sharedPrefsHelper.getQbUser();

                    if (ProfileHelper.getAccount(getApplicationContext()).getFirstName() != null) {
                        String firstName = ProfileHelper.getAccount(getApplicationContext()).getFirstName();
                        String lastName = ProfileHelper.getAccount(getApplicationContext()).getLastName();
                        qbUser.setFullName(firstName + " " + lastName);
                    }

                    qbUser.setPassword(null);
                    QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }
            }
        }
    }

    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        startLoginService(qbUser);
    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    private QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData(String.valueOf(ProfileHelper.getAccount(getApplicationContext()).getId()), ProfileHelper.getAccount(getApplicationContext()).getName());
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

    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, LoginService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        LoginService.start(this, qbUser, pendingIntent);
    }


    private void getNotifications() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Notifications>> call = apiInterface.getAllNotification("Bearer " + TokenHelper.getToken(), StorageHelper.getNotificationDateTime());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Notifications>> call, @NonNull Response<List<Notifications>> response) {


                try {
                    if (response.body() != null) {

                        ArrayList<Notifications> notificationsList = (ArrayList<Notifications>) response.body();

                        if (!notificationsList.isEmpty()) {
                            StorageHelper.saveNotificationDateTime();
                            StorageHelper.saveNotificationsCounter(StorageHelper.getNotificationsCounter() + 1);
                            if (StorageHelper.getNotificationDateTime() != null) {
                                if (mViewPager2.getCurrentItem() != 3) {// notification fragment id in view pager is 3
                                    //NotificationHelper.showBadge(getApplicationContext(), bottomNavigation, R.id.notification, "" + StorageHelper.getNotificationsCounter());
                                    showHideNotificationBadge(true);
                                }
                            }
                        }
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notifications>> call, @NonNull Throwable t) {
            }
        });
    }

    private void getUserProfile() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<User> call = apiInterface.getProfile("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                try {
                    User user = response.body();
                    if (user != null) {

                        if (!user.getActive()) {
                            Intent intent = new Intent(NewMainActivityDesign.this, ShowInActiveActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        ProfileHelper.createOrUpdateAccount(user, getApplicationContext());
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

            }
        });
    }


    private void getSkills() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getSkills();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {

                try {

                    if (response.body() != null) {
                        Gson gson = new Gson();
                        String jsonCat = gson.toJson(response.body());
                        //*************** save skill into phone memory **************//
                        PrefUtil.writePreferenceValue(getApplicationContext(), "CATEGORY_CONSULTANT", jsonCat);
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {

            }
        });

    }


    private static void getSubscriptionPushesFromQBAndDeletePushes() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            String qbToken = QBSessionManager.getInstance().getToken();
            if (qbToken == null)
                return;
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.quickblox.com/subscriptions.json")
                    .method("GET", null)
                    .addHeader("Qb-Token", qbToken)
                    .build();
            try (okhttp3.Response response = client.newCall(request).execute()) {
                String s = Objects.requireNonNull(response.body()).string();
                String[] list = s.split("\"id\":");

                //make sure there is an exist push id and keep 3 devices for connect to push notification,
                // by the way first index has only string of word with no id number so I ignore it
                for (int i = 1; i < list.length - 4; i++)
                    deleteSubscriptionByIdFromQB(Integer.valueOf(list[i].split(",")[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                //UI Thread work here
            });
        });
    }

    /**
     * delete subscribe push in quickblox server
     * the use of this method is when user reach the maximum of subscribe pushes
     *
     * @param sub_ids the id of subscription
     */
    private static void deleteSubscriptionByIdFromQB(Integer sub_ids) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            OkHttpClient client = new OkHttpClient();
            String qbToken = QBSessionManager.getInstance().getToken();
            if (qbToken == null)
                return;


            Request request = new Request.Builder()
                    .url(String.format("https://api.quickblox.com/subscriptions/%s.json", sub_ids))
                    .delete(null)
                    .addHeader("Accept", "application/json")
                    .addHeader("QB-Token", qbToken)
                    .build();


            try (okhttp3.Response ignored = client.newCall(request).execute()) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here
            });
        });

    }


    public void openServices() {
        mViewPager2.setCurrentItem(MainViewsId.SERVICES.id);
        binding.appBarNewMainDesign.navBar.setSelectedItemId(R.id.services);
    }
}