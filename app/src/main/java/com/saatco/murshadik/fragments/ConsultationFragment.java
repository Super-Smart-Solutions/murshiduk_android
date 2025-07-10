package com.saatco.murshadik.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saatco.murshadik.ChatUserActivity;
import com.saatco.murshadik.ChatbotActivity;
import com.saatco.murshadik.CoffeePlantActivity;
import com.saatco.murshadik.FarmVisitListActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.ItemListActivity2;
import com.saatco.murshadik.LabReportActivitiy;
import com.saatco.murshadik.LoginActivity;
import com.saatco.murshadik.QuestionsActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.FragmentConsultationBinding;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;
import java.util.Objects;



public class ConsultationFragment extends Fragment {


    private FragmentConsultationBinding binding;

    public static ConsultationFragment newInstance() {
        ConsultationFragment fragment = new ConsultationFragment();
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
        binding = FragmentConsultationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.llReport.setOnClickListener(view12 -> {
            if (getActivity() == null) return;

            User user = ProfileHelper.getAccount(getContext());
            if (user != null)
                if (user.getStatus() != null) {
                    if (user.isProfileComplete() && !user.getStatus().isEmpty()) {
                        if ((user.isConsultantUser() && user.isApproved()) || user.isFarmer()) {
                            Intent intent = new Intent(getContext(), LabReportActivitiy.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                        } else {
                            Util.showErrorToast(getResources().getString(R.string.account_pending), getActivity());
                        }
                    } else {
                        Util.showErrorToast(getResources().getString(R.string.complete_profile_msg), getActivity());
                    }
                } else
                    startActivity(new Intent(getContext(), LoginActivity.class));
        });

        binding.llQnA.setOnClickListener(view13 -> {
            startActivity(new Intent(getContext(), QuestionsActivity.class));
            requireActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        binding.llChat.setOnClickListener(view14 -> {
            if (getActivity() == null) return;

            User user = ProfileHelper.getAccount(getContext());
            if (user != null)
                if (user.getStatus() != null) {
                    if (user.isProfileComplete() && !user.getStatus().isEmpty()) {
                        if ((user.isConsultantUser() && user.isApproved()) || user.isFarmer()) {
                            Intent intent = new Intent(getContext(), ChatUserActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            Util.showErrorToast(getResources().getString(R.string.account_pending), getActivity());
                        }

                    } else {
                        Util.showErrorToast(getResources().getString(R.string.complete_profile_msg), getActivity());
                    }
                } else
                    startActivity(new Intent(getContext(), LoginActivity.class));

        });

        binding.llCoffee.setOnClickListener(view15 -> {
            if (getActivity() == null) return;

            Intent intent = new Intent(getContext(), CoffeePlantActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

        });

        binding.llChatBot.setOnClickListener(view16 -> {

            User user = ProfileHelper.getAccount(getContext());
            if (user != null)
                if (user.getStatus() != null) {
                    if (user.isProfileComplete() && !user.getStatus().isEmpty()) {
                        if ((user.isConsultantUser() && user.isApproved()) || user.isFarmer()) {
                            Intent intent = new Intent(getActivity(), ChatbotActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            Util.showErrorToast(getResources().getString(R.string.account_pending), getActivity());
                        }
                    } else {
                        Util.showErrorToast(getResources().getString(R.string.complete_profile_msg), getActivity());
                    }
                }

        });

        binding.llFarmVisit.setOnClickListener((v) -> startActivity(new Intent(getContext(), FarmVisitListActivity.class)));

        binding.llLibrary.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), ItemListActivity2.class);
            intent.putExtra("TITLE", "المكتبة الرقمية");
            intent.putExtra("CAT_ID", 15);
            startActivity(intent);
        });

        binding.llOther.setOnClickListener(v -> {
            if (binding.llOtherCards.getVisibility() == View.GONE) {
                binding.llOtherCards.setVisibility(View.VISIBLE);
                binding.flCircleArrow.setRotation(90);
            } else {
                binding.llOtherCards.setVisibility(View.GONE);
                binding.flCircleArrow.setRotation(270);
            }
        });


//        ArrayList<String> medias = new ArrayList<>();
//        medias.add(getURLForResource(R.drawable.img_1_advice));
//        medias.add(getURLForResource(R.drawable.img_2_qna));
//        medias.add(getURLForResource(R.drawable.img_3_labs));

//        setSlider(medias);

        //adding main screen to services screen
        return view;
    }


//    private void setSlider(ArrayList<String> medias) {

//        if (medias == null) return;

//        ArrayList<Page> pageViewsPack = new ArrayList<>();
//
//        for (String media : medias)
//            pageViewsPack.add(new Page(media));
//
//        IndicatorConfiguration configurationPack = new IndicatorConfiguration.Builder()
//                .imageLoader(new CustomImageLoader())
//                .isStopWhileTouch(true)
//                .position(IndicatorConfiguration.IndicatorPosition.Center_Top)
//                .build();
//        binding.indicatorFarmProblemImages.init(configurationPack);
//        binding.indicatorFarmProblemImages.notifyDataChange(pageViewsPack);

//    }

    public String getURLForResource(int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + Objects.requireNonNull(R.class.getPackage()).getName() + "/" + resourceId).toString();

    }


}