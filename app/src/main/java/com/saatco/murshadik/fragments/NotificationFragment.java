package com.saatco.murshadik.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.saatco.murshadik.AnswerActivity;
import com.saatco.murshadik.ChatActivityNew;
import com.saatco.murshadik.ChatUserActivity;
import com.saatco.murshadik.FragmentSelectorActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.LabCompletedActivity;
import com.saatco.murshadik.MarketDetailActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.NotificationAdapter;
import com.saatco.murshadik.adapters.SwipeToDeleteCallback;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.WeatherNotificationResponse;
import com.saatco.murshadik.databinding.FragmentNotificationBinding;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.model.Notifications;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationFragment extends Fragment implements NotificationAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;
    RecyclerView weatherRecyclerView;
    ProgressBar progressBar;
    LinearLayout btnDelete;
    View emptyView;
    TextView tvWeatherHeading;

    ArrayList<Notifications> notifications = new ArrayList<>();
    ArrayList<Notifications> weatherNotifications = new ArrayList<>();

    NotificationAdapter adapter;
    NotificationAdapter weatherAdapter;

    boolean isStartedByWeatherActivity = false;

    FragmentNotificationBinding binding;

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                    new IntentFilter("alert_msg"));
    }

    @Override
    public void onResume() {
        super.onResume();
        //********************* api *******************//
        getNotifications();

        if (ProfileHelper.getAccount(getContext()) != null) {
            //********************* user has governorate load weather notifications *******************//
            if (ProfileHelper.getAccount(getContext()).getGovernorate() != null)
                if (isStartedByWeatherActivity)
                    getWeatherNotifications();
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initViews();

        if (StorageHelper.getAllNotifications() != null) {
            notifications = StorageHelper.getAllNotifications();
            notifications.sort(Collections.reverseOrder());
        }

        setAdapter();
        enableSwipeToDelete();


        if (getActivity() != null)
            isStartedByWeatherActivity = requireActivity().getIntent().getIntExtra(FragmentSelectorActivity.CODE_NUMBER, 0) == FragmentSelectorActivity.WEATHER_NOTIFICATION_CODE;

        btnDelete.setOnClickListener(view1 -> deleteAll());

        return view;
    }

    private void initViews() {
        recyclerView = binding.rvNotifications;
        weatherRecyclerView = binding.rvWeatherNotifications;
        progressBar = binding.progressBar.getRoot();
        btnDelete = binding.btnDelete;
        emptyView = binding.layoutEmpty.getRoot();
        tvWeatherHeading = binding.tvWeatherHeading;
    }

    private void getNotifications() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Notifications>> call = apiInterface.getAllNotification("Bearer " + TokenHelper.getToken(), "");
        call.enqueue(new Callback<List<Notifications>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notifications>> call, @NonNull Response<List<Notifications>> response) {

                progressBar.setVisibility(View.GONE);

                try {
                    if (response.body() != null) {

                        ArrayList<Notifications> notificationsList = (ArrayList<Notifications>) response.body();
                        if (isStartedByWeatherActivity) {
                            notifications = new ArrayList<>();
                            for (Notifications notify : notificationsList) {
                                if (notify.getType() == 9) //consultant public messages
                                    notifications.add(notify);
                            }
                        } else
                            notifications = notificationsList;
                        //********************* save all notifications in memory *******************//
                        StorageHelper.saveAllNotifications(notifications);
                        notifications.sort(Collections.reverseOrder());
                        //********************* notifications are not avilable show empty layout *******************//
                        if (notifications.isEmpty() && weatherNotifications.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                        }

                        //********************* notifications are not avilable hide delete button *******************//
                        if (notifications.isEmpty()) {
                            btnDelete.setVisibility(View.GONE);
                        } else {
                            btnDelete.setVisibility(View.VISIBLE);
                        }

                        adapter.updateList(notifications);
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notifications>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //********************* get weather notifications  *******************//
    private void getWeatherNotifications() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<WeatherNotificationResponse> call = apiInterface.getWeatherNotification("Bearer " + TokenHelper.getToken(), ProfileHelper.getAccount(getContext()).getGovernorate());
        call.enqueue(new Callback<WeatherNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherNotificationResponse> call, @NonNull Response<WeatherNotificationResponse> response) {

                try {
                    if (response.body() != null) {
                        weatherNotifications = response.body().getNotifications();

                        for (Notifications notifications : weatherNotifications) {
                            notifications.setType(1);
                        }

                        if (notifications.size() < 1 && weatherNotifications.size() < 1) {
                            emptyView.setVisibility(View.VISIBLE);
                            btnDelete.setVisibility(View.GONE);
                        } else {
                            btnDelete.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }

                        if (weatherNotifications.size() < 1) {
                            tvWeatherHeading.setVisibility(View.GONE);
                            weatherRecyclerView.setVisibility(View.GONE);
                        } else {
                            tvWeatherHeading.setVisibility(View.VISIBLE);
                            weatherRecyclerView.setVisibility(View.VISIBLE);
                        }

                        setWeatherAdapter();
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherNotificationResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void setAdapter() {

        if (notifications.isEmpty() && weatherNotifications.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        if (notifications.isEmpty()) {
            btnDelete.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.VISIBLE);
        }

        adapter = new NotificationAdapter(notifications, getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setWeatherAdapter() {
        weatherAdapter = new NotificationAdapter(weatherNotifications, getContext(), this);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weatherRecyclerView.setAdapter(weatherAdapter);
        weatherRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(requireContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getBindingAdapterPosition() ;
                final Notifications notifications = adapter.getData().get(position);
                delete(notifications.getId());

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    private void deleteAll() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.deleteAllNotification("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {

                    Util.showSuccessToast(getResources().getString(R.string.remove_success), getActivity());
                    StorageHelper.saveAllNotifications(new ArrayList<>());
                    notifications.clear();
                    setAdapter();
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(new Intent("alert_msg"));
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void delete(int id) {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.deleteNotificationById("Bearer " + TokenHelper.getToken(), id);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {

                    //******************* remove notification and store in memory *******************//

                    notifications.removeIf(notifications -> notifications.getId() == id);

                    StorageHelper.saveAllNotifications(notifications);
                    setAdapter();
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(new Intent("alert_msg"));

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onItemClick(View view, int position, Notifications notification) {

        Intent intent = null;

        if (notification.getType() == Consts.NOTI_TYPE_MARKET) {
            intent = new Intent(getContext(), MarketDetailActivity.class);
            intent.putExtra("ID", notification.getTypeId());
            startActivity(intent);
        } else if (notification.getType() == Consts.NOTI_TYPE_LAB) {
            intent = new Intent(getContext(), LabCompletedActivity.class);
            startActivity(intent);
        } else if (notification.getType() == Consts.NOTI_TYPE_QUESTION || notification.getType() == Consts.NOTI_TYPE_ANSWER) {
            intent = new Intent(getContext(), AnswerActivity.class);
            intent.putExtra("ID", notification.getTypeId());
            startActivity(intent);
        } else if (notification.getType() == Consts.NOTI_TYPE_CHAT || notification.getType() == Consts.NOTI_TYPE_GUIDE) {

            if (notification.getLink() != null && !notification.getLink().equals("")) {
                User user = new User();
                user.setChatId(notification.getLink()); // link is chatId of user

                intent = new Intent(getContext(), ChatActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("USER", user);
                intent.putExtra("is_cb", false);
            } else {
                intent = new Intent(getContext(), ChatUserActivity.class);
                intent.putExtra("is_noti", true);
            }
            startActivity(intent);

        } else //************************ switch to profile **************************//
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(new Intent("notification_click"));


    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getNotifications();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver);

    }
}