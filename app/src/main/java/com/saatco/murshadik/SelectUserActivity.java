package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ConsultantAdapterRating;
import com.saatco.murshadik.databinding.ActivitySelectUserBinding;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.viewmodels.UserViewModel;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;



public class SelectUserActivity extends BaseActivity implements ConsultantAdapterRating.OnSelectItemClickListener {

    RecyclerView recyclerView;
    ConsultantAdapterRating adapter;
    ArrayList<User> sortedConsultants = new ArrayList<User>();

    boolean isSorted = false;
    String skill, region_name;

    SearchView searchView;

    ProgressBar progressBar;

    ActivitySelectUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectUserBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_select_user);

        initViews();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });


        recyclerView = findViewById(R.id.rv_consultants);

        isSorted = getIntent().getBooleanExtra("is_sorted", false);
        skill = getIntent().getStringExtra("skill");
        region_name = getIntent().getStringExtra("region_name");

        ToolbarHelper.setToolBar(this, skill, findViewById(R.id.toolbarTrans));

        getChatDialogs();


        progressBar.setVisibility(View.VISIBLE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filter(newText);
                return false;
            }


        });

    }

    private void initViews() {
        searchView = binding.searchView;
        progressBar = binding.progressBar.getRoot();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        UserViewModel userViewModel = viewModelProvider.get(UserViewModel.class);

        if (isSorted) {
            if (region_name != null) {
                userViewModel.getConsultantsByCategoryAndRegion("%" + skill + "%", "%" + region_name + "%").observe(this, new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {

                        sortedConsultants = (ArrayList<User>) users;
                        for (User user : sortedConsultants)
                            user.setRoleId(6);

                        progressBar.setVisibility(View.GONE);

                        sortedConsultants.sort(new OnlineComparator());

                        setDetails();


                    }
                });
            } else {

                userViewModel.getConsultantsByCategory("%" + skill + "%").observe(this, new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {

                        sortedConsultants = (ArrayList<User>) users;
                        for (User user : sortedConsultants)
                            user.setRoleId(6);

                        progressBar.setVisibility(View.GONE);

                        sortedConsultants.sort(new OnlineComparator());

                        setDetails();


                    }
                });
            }
        } else {

            userViewModel.getAllUser().observe(this, users -> {
                sortedConsultants = (ArrayList<User>) users;
                for (User user : sortedConsultants)
                    user.setRoleId(6);

                progressBar.setVisibility(View.GONE);

                sortedConsultants.sort(new OnlineComparator());

                setDetails();
            });
        }
    }

    private final BroadcastReceiver mStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String status = intent.getStringExtra("status");
            int userId = intent.getIntExtra("user_id", 0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        adapter.updateSingle(userId, status);
                    }
                }
            });

        }
    };


    private void setDetails() {

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        sortedConsultants.sort((mall1, mall2) -> {

            boolean b1 = mall1.isOnline();
            boolean b2 = mall2.isOnline();

            return (b1 != b2) ? (b1) ? -1 : 1 : 0;
        });

        adapter = new ConsultantAdapterRating(sortedConsultants, getApplicationContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public static class OnlineComparator implements Comparator<User> {
        @Override
        public int compare(User a, User b) {
            return a.isOnline() == b.isOnline() ? 0 : 1;
        }
    }

    @Override
    public void onConsultantClick(View view, int position, User user) {


        if (user.getChatId() != null) {
            Intent intent = new Intent(SelectUserActivity.this, ChatActivityNew.class);
            intent.putExtra("USER", user);
            startActivity(intent);
        } else {
            DialogUtil.showOfflineAlert(SelectUserActivity.this, "تسجيل غير مكتمل", " المرشد لم يكمل تسجيله في التطبيق" + " " + user.getName());
        }

    }

    @Override
    public void onProfileClick(View view, int position, User user) {

        Intent intent = new Intent(SelectUserActivity.this, userInfoActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mStatus,
                new IntentFilter("user_status"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mStatus);

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }

    void filter(String text) {
        ArrayList<User> temp = new ArrayList<>();

        for (User d : sortedConsultants) {
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        ((ConsultantAdapterRating) Objects.requireNonNull(recyclerView.getAdapter())).updateList(temp);
    }




    private void getChatDialogs() {

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.sortDesc("updated_at");

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> result, Bundle params) {

                for (QBChatDialog qbChatDialog : result) {
                    for (User user : sortedConsultants) {
                        if (user.getChatId() != null) {
                            if (Integer.parseInt(user.getChatId()) == qbChatDialog.getUserId()) {
                                user.chatDialog = qbChatDialog;
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(QBResponseException responseException) {

            }
        });
    }

}
