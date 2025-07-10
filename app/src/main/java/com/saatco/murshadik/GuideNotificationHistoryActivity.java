package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.GuideNotificationsHistoryAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityGuideNotificationHistoryBinding;
import com.saatco.murshadik.model.GuideNotification;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.DialogUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideNotificationHistoryActivity extends AppCompatActivity implements GuideNotificationsHistoryAdapter.OnClickListener {

    private ActivityGuideNotificationHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideNotificationHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ToolbarHelper.setToolBarTransWithTitle(this, getString(R.string.sended_guide_notification_history), binding.includeViewTransBarLayout.toolbarTrans);

        getGuideNotificationHistory();

    }

    void initNotificationList(ArrayList<GuideNotification> guideNotifications) {
        if (guideNotifications == null || guideNotifications.isEmpty()){
            DialogUtil.showInfoDialogAndFinishActivity(this, 0, getString(R.string.no_data), getString(R.string.error));
            return;
        }
        GuideNotificationsHistoryAdapter adapter = new GuideNotificationsHistoryAdapter(guideNotifications, this, this);
        binding.rvGuideNotification.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGuideNotification.setAdapter(adapter);
        binding.rvGuideNotification.setItemAnimator(new DefaultItemAnimator());
    }

    private void getGuideNotificationHistory() {

        binding.llProgressBar.progressBar.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<GuideNotification>>> call = apiInterface.getGuideNotificationsHistory("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<GuideNotification>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<GuideNotification>>> call, @NonNull Response<NewAPIsResponse<ArrayList<GuideNotification>>> response) {

                try {

                    assert response.body() != null;
                    initNotificationList(response.body().getData());

                } catch (Exception ignored) {
                }

                binding.llProgressBar.progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<GuideNotification>>> call, @NonNull Throwable t) {
                binding.llProgressBar.progressBar.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public void onClick(View view, int position, GuideNotification item) {

    }
}