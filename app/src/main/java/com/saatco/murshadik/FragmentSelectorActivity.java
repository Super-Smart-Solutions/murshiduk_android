package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.FragmentsNewUiDesignAdapter;
import com.saatco.murshadik.adapters.OtherFragmentsNewUiDesignAdapter;
import com.saatco.murshadik.databinding.ActivityFragmentSelectorBinding;
import com.saatco.murshadik.fragments.MarketMainFragment;
import com.saatco.murshadik.fragments.NotificationFragment;
import com.saatco.murshadik.fragments.ProfileFragment;

public class FragmentSelectorActivity extends AppCompatActivity {

    ActivityFragmentSelectorBinding binding;

    public static String PAGE_NUMBER = "page_number";
    public static String CODE_NUMBER = "code_number";

    public static int MARKET_PAGE = 0;
    public static int NOTIFICATION_PAGE = 1;
    public static int PROFILE_PAGE = 2;

    public static int WEATHER_NOTIFICATION_CODE = 123;



    public static void startActivity(Context context, int page) {
        Intent intent = new Intent(context, FragmentSelectorActivity.class);
        intent.putExtra(FragmentSelectorActivity.PAGE_NUMBER, page);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int page, int code) {
        Intent intent = new Intent(context, FragmentSelectorActivity.class);
        intent.putExtra(FragmentSelectorActivity.PAGE_NUMBER, page);
        intent.putExtra(FragmentSelectorActivity.CODE_NUMBER, code);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentSelectorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        LanguageUtil.changeLanguage(this);

        ToolbarHelper.setToolBarTransWithTitle(this, "", binding.appBar.toolbarTrans);

        int page = getIntent().getIntExtra(PAGE_NUMBER, 0);
        loadFragment(page);
    }


    private void loadFragment(int page) {


        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        switch (page){
            case 0:
//                fragmentTransaction.add(R.id.fragment_container_view, MarketMainFragment.class, null);
                break;
            case 1:
                fragmentTransaction.add(R.id.fragment_container_view, NotificationFragment.class, null);
                break;
            default:
                fragmentTransaction.add(R.id.fragment_container_view, ProfileFragment.class, null);
                break;
        }
        fragmentTransaction.commit(); // save the changes
    }


}