package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import me.leolin.shortcutbadger.ShortcutBadger;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.databinding.ActivityChatUserBinding;
import com.saatco.murshadik.fragments.CategoryFragment;
import com.saatco.murshadik.fragments.ConsultansFragment;
import com.saatco.murshadik.fragments.GroupFragment;
import com.saatco.murshadik.utils.Consts;

public class ChatUserActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    SearchView searchView;
    ImageView addUserBtn;
    FrameLayout container;
    LinearLayout categoryTab;
    LinearLayout chatTab;
    LinearLayout groupTab;
    LinearLayout consultantTab;
    TextView tvTab1;
    TextView tvTab2;
    TextView tvTab3;
    TextView tvTab4;

    ISearchChat iSearchChat;
    ISearchChatUsers iSearchChatUsers;
    public ISearchGroups iSearchGroups;
    IChatUserOnline iChatUserOnline;
    IChatCategoryOnline iChatCategoryOnline;
    FloatingActionButton fab_search_all_consultants;

    boolean isNotification = false;

    ActivityChatUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatUserBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();

        ConsultansFragment consultansFragment = new ConsultansFragment();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        fab_search_all_consultants = findViewById(R.id.fab_search_all_consultants);

        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat", true);
        isNotification = getIntent().getBooleanExtra(Consts.IS_START_FROM_NOTIFY, false);

        if (ProfileHelper.getAccount(getApplicationContext()).getRoleId() == 5) {
            groupTab.setVisibility(View.GONE);

            changeButtonBack(new LinearLayout[]{chatTab, groupTab}, categoryTab, new TextView[]{tvTab2, tvTab3}, tvTab1);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (isNotification) {
                changeButtonBack(new LinearLayout[]{categoryTab, groupTab}, chatTab, new TextView[]{tvTab1, tvTab3}, tvTab2);
            }
            fragmentTransaction.replace(R.id.fragment_container, consultansFragment);

            fragmentTransaction.commit();

            consultantTab.setVisibility(View.GONE);


        } else if (ProfileHelper.getAccount(getApplicationContext()).getRoleId() == 6) {

            changeButtonBack(new LinearLayout[]{categoryTab, groupTab, consultantTab}, chatTab, new TextView[]{tvTab1, tvTab3, tvTab4}, tvTab2);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, consultansFragment);
            fragmentTransaction.commit();

            categoryTab.setVisibility(View.GONE);
            consultantTab.setVisibility(View.VISIBLE);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (iSearchChat != null)
                    iSearchChat.onSearchChat(query);
                if (iSearchChatUsers != null)
                    iSearchChatUsers.onSearchChatUsers(query);
                if (iSearchGroups != null)
                    iSearchGroups.onSearchGroups(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (iSearchChat != null)
                    iSearchChat.onSearchChat(newText);
                if (iSearchChatUsers != null)
                    iSearchChatUsers.onSearchChatUsers(newText);
                if (iSearchGroups != null)
                    iSearchGroups.onSearchGroups(newText);


                return false;
            }


        });


        ToolbarHelper.setToolBar(this, "", binding.appBar.getRoot());

        addUserBtn.setOnClickListener(view -> {

            Intent intent = new Intent(ChatUserActivity.this, SelectUserActivity.class);
            // intent.putExtra("consultants",consultantList);
            startActivity(intent);
            fab_search_all_consultants.setVisibility(View.GONE);

        });


        categoryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeButtonBack(new LinearLayout[]{chatTab, groupTab, consultantTab}, categoryTab, new TextView[]{tvTab2, tvTab3, tvTab4}, tvTab1);

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new CategoryFragment());
                fragmentTransaction.commit();
                fab_search_all_consultants.setVisibility(View.VISIBLE);

            }
        });

        chatTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeButtonBack(new LinearLayout[]{categoryTab, groupTab, consultantTab}, chatTab, new TextView[]{tvTab1, tvTab3, tvTab4}, tvTab2);

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, consultansFragment);
                fragmentTransaction.commit();
            }
        });

        groupTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeButtonBack(new LinearLayout[]{categoryTab, chatTab, consultantTab}, groupTab, new TextView[]{tvTab1, tvTab2, tvTab4}, tvTab3);

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new GroupFragment());
                fragmentTransaction.commit();
                fab_search_all_consultants.setVisibility(View.GONE);
            }
        });

        consultantTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeButtonBack(new LinearLayout[]{chatTab, groupTab, categoryTab}, consultantTab, new TextView[]{tvTab2, tvTab3, tvTab1}, tvTab4);

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new CategoryFragment());
                fragmentTransaction.commit();
                fab_search_all_consultants.setVisibility(View.VISIBLE);
            }
        });

        fab_search_all_consultants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatUserActivity.this, SelectUserActivity.class);
                intent.putExtra("is_sorted", true);
                intent.putExtra("skill", "");
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        searchView = binding.searchView;
        addUserBtn = binding.btnAddUser;
        container = binding.fragmentContainer;
        categoryTab = binding.layoutTab1;
        chatTab = binding.layoutTab2;
        groupTab = binding.layoutTab3;
        consultantTab = binding.layoutTab4;
        tvTab1 = binding.tvTab1Title;
        tvTab2 = binding.tvTab2Title;
        tvTab3 = binding.tvTab3Title;
        tvTab4 = binding.tvTab4Title;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setActivityListener(ISearchChat activityListener) {
        this.iSearchChat = activityListener;
    }

    public void setActivityListener(ISearchChatUsers activityListener) {
        this.iSearchChatUsers = activityListener;
    }

    public void setActivityGroupsListener(ISearchGroups activityListener) {
        this.iSearchGroups = activityListener;
    }

    public void setActivityOnlineOfflineListener(IChatUserOnline activityListener) {
        this.iChatUserOnline = activityListener;
    }

    public void setActivityOnlineOfflineCategoryListener(IChatCategoryOnline activityListener) {
        this.iChatCategoryOnline = activityListener;
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

        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat", false);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(10);

        ShortcutBadger.removeCount(getApplicationContext()); //for 1.1.4+
        PrefUtil.writeIntValue(this, Consts.SHORTCUT_BADGER_COUNTER_CHAT_MSG, 0);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }

    public void changeButtonBack(LinearLayout[] layouts, LinearLayout current, TextView[] textViews, TextView currentTv) {

        current.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.set_selected_tab));
        currentTv.setTextColor(getResources().getColor(R.color.white, null));

        for (int i = 0; i < layouts.length; i++) {

            LinearLayout button = layouts[i];
            TextView textView = textViews[i];

            button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unselected_tab));
            textView.setTextColor(getResources().getColor(R.color.black, null));
        }

    }

}
