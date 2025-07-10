package com.saatco.murshadik;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.saatco.murshadik.databinding.ActivityGroupInfoBinding;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;

public class GroupInfoActivity extends BaseActivity {

    TextView tvGroupName;
    TextView membersCount;
    TextView tvCreatedBy;
    ImageView btnBack;

    ArrayList<User> members = new ArrayList<>();
    ListView listViewMembers;

    ActivityGroupInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupInfoBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        initViews();

        members = (ArrayList<User>) getIntent().getSerializableExtra("MEMBERS");
        String name = getIntent().getStringExtra("NAME");

        String strMembersNum = getString(R.string.members_count) + members.size();

        membersCount.setText(strMembersNum);

        tvGroupName.setText(name);

        listViewMembers = findViewById(R.id.rv_participants);

        btnBack.setOnClickListener(view -> finish());

        setGroupMembers();

    }

    private void initViews() {
        tvGroupName = binding.tvGroupName;
        membersCount = binding.tvParticipantsCount;
        tvCreatedBy = binding.tvCreatedBy;
        btnBack = binding.btnBackBtn;
    }

    private void setGroupMembers() {

        ArrayList<String> memberNames = new ArrayList<>();

        for (User member : members) {
            memberNames.add(member.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, memberNames);

        ViewGroup.LayoutParams params = listViewMembers.getLayoutParams();
        params.height = (int) (50 * memberNames.size() * (getResources().getDisplayMetrics().density));

        // Assign adapter to ListView
        listViewMembers.setAdapter(adapter);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
