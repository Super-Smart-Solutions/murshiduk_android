package com.saatco.murshadik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.saatco.murshadik.databinding.ActivityConsultationAppointmentsBinding;
import com.saatco.murshadik.enums.ManagementType;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointments;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsConfirmationFragment;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsReasonFragment;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsSelectCityFragment;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsSelectConsultantFragment;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsSelectDateTimeFragment;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsSelectSkillFragment;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.DialogUtil;

import java.util.Objects;

public class ConsultationAppointmentsActivity extends BaseActivity {

    public static final String EXTRA_IS_SUB_SKILLS = "isSubSkills";
    public static final String EXTRA_MANAGEMENT_TYPE = "managementType";

    ActivityConsultationAppointmentsBinding binding;

    ManagementType managementType;
    String city = "";
    Item skill;
    User consultant;
    String date = "";
    String time = "";
    String reason = "";
    boolean isSubSkills = false;

    static public void start(Context context, boolean isSubSkills, ManagementType managementType) {
        Intent intent = new Intent(context, ConsultationAppointmentsActivity.class);
        intent.putExtra(EXTRA_IS_SUB_SKILLS, isSubSkills);
        intent.putExtra(EXTRA_MANAGEMENT_TYPE, managementType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultationAppointmentsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        isSubSkills = getIntent().getBooleanExtra(EXTRA_IS_SUB_SKILLS, false);
        managementType = (ManagementType) getIntent().getSerializableExtra(EXTRA_MANAGEMENT_TYPE);
        if (managementType == null) {
            managementType = ManagementType.ALL;
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
                    finish();
                }else {
                    getSupportFragmentManager().popBackStack();
                }

            }
        });

        showSelectCityFragment();
    }

    public void showFragment (ConsultationAppointments fragment) {
        switch (fragment) {
            case ConsultationAppointmentsConfirmationFragment:
                showConfirmationFragment();
                break;
            case ConsultationAppointmentsReasonFragment:
                showReasonFragment();
                break;
            case ConsultationAppointmentsSelectCityFragment:
                showSelectCityFragment();
                break;
            case ConsultationAppointmentsSelectConsultantFragment:
                showSelectConsultantFragment();
                break;
            case ConsultationAppointmentsSelectDateTimeFragment:
                showSelectDateFragment();
                break;
            case ConsultationAppointmentsSelectSkillFragment:
                showSelectSkillFragment();
                break;
        }
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setSkill(Item skill) {
        this.skill = skill;
    }

    public void setConsultant(User consultant) {
        this.consultant = consultant;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public void back() {
        getOnBackPressedDispatcher().onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }



    public void close() {
        // confirm closing the activity
        DialogUtil.yesNoDialog(this, getString(R.string.cancel), getString(R.string.are_you_sure), (msg) -> {
            if (Objects.equals(msg, DialogUtil.YES)) {
                finish();
            }
        });
    }

    void showSelectCityFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.fragment_container, ConsultationAppointmentsSelectCityFragment.newInstance(city))
                .setReorderingAllowed(true)
                .addToBackStack("city")
                .commit();
    }

    void showSelectSkillFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.fragment_container, ConsultationAppointmentsSelectSkillFragment.newInstance(skill, isSubSkills, managementType))
                .setReorderingAllowed(true)
                .addToBackStack("skill")
                .commit();
    }

    void showReasonFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.fragment_container, ConsultationAppointmentsReasonFragment.newInstance(reason))
                .setReorderingAllowed(true)
                .addToBackStack("reason")
                .commit();
    }

    void showSelectConsultantFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.fragment_container, ConsultationAppointmentsSelectConsultantFragment.newInstance(skill.getNameAr()))
                .setReorderingAllowed(true)
                .addToBackStack("consultant")
                .commit();
    }

    void showSelectDateFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.fragment_container, ConsultationAppointmentsSelectDateTimeFragment.newInstance(date, time, consultant.getId()))
                .setReorderingAllowed(true)
                .addToBackStack("date")
                .commit();
    }

    void showConfirmationFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.fragment_container, ConsultationAppointmentsConfirmationFragment.newInstance(
                        city,
                        skill,
                        consultant,
                        date,
                        time,
                        reason
                ))
                .setReorderingAllowed(true)
                .addToBackStack("confirmation")
                .commit();
    }




}