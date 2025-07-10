package com.saatco.murshadik.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.saatco.murshadik.AvailableWorkerActivity;
import com.saatco.murshadik.CalendarDetailActivity;
import com.saatco.murshadik.CategoryChildActivity;
import com.saatco.murshadik.ChatUserActivity;
import com.saatco.murshadik.ClipsIndicativeCategoryActivity;
import com.saatco.murshadik.CoffeePlantActivity;
import com.saatco.murshadik.FarmVisitListActivity;
import com.saatco.murshadik.FragmentSelectorActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.WorkerHelper;
import com.saatco.murshadik.ItemListActivity2;
import com.saatco.murshadik.LabReportActivitiy;
import com.saatco.murshadik.LoginActivity;
import com.saatco.murshadik.QuestionsActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.ReefComponentsActivity;
import com.saatco.murshadik.SearchActivity;
import com.saatco.murshadik.SelectUserActivity;
import com.saatco.murshadik.VideoViewActivity;
import com.saatco.murshadik.VirtualClinicActivity;
import com.saatco.murshadik.WeatherDetailActivity;
import com.saatco.murshadik.WorkerIntroActivity;
import com.saatco.murshadik.adapters.AgriProductAdapter;
import com.saatco.murshadik.adapters.CategoryAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.ApiMethodsHelper;
import com.saatco.murshadik.api.response.DashboardResponse;
import com.saatco.murshadik.api.response.WeatherResponse;
import com.saatco.murshadik.databinding.FragmentDashboardBinding;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.enums.ManagementType;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Section;
import com.saatco.murshadik.model.SectionBanner;
import com.saatco.murshadik.model.Stats;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.workersService.Worker;
import com.saatco.murshadik.utils.DataUtilHelper;
import com.saatco.murshadik.utils.DialogCallManagementConsultingUtil;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.RealPathUtil;
import com.saatco.murshadik.utils.Util;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment implements CategoryAdapter.OnSelectItemClickListener, AgriProductAdapter.OnSelectItemClickListener {

    RecyclerView recyclerViewSection1;
    RecyclerView recyclerViewSection2;
    RecyclerView recyclerViewSection5;
    TextView tvSection5; //rv5
    TextView tvAllAg; //rv5
    CardView ivBanner2;
    YouTubePlayerView youTubePlayerView;
    ImageView ivFarmerStat;
    ImageView ivMessageStat;
    TextView tvFarmerStat;
    TextView tvMessageStat;
    CardView farmerCardView;
    CardView messageCardView;
    CardView ivCalender;
    ProgressBar progressBar;
    CardView btnQA;
    CardView btnManagementConsulting;
    CardView btnManagementMinistry;
    CardView btnChatBot;
    CardView projectResearchAppBtn;
    LinearLayout btnWeather;
    ImageView ivWeather;
    TextView tvWeather;
    TextView tvWeatherCondition;
    ImageView ivItemYellowOne;
    ImageView ivItemYellowTwo;
    ImageView ivItemGreen;
    TextView tvItemGreen;
    TextView tvItemYellowOne;
    TextView tvItemYellowTwo;
    View btnItemYellowOne;
    View btnItemYellowTwo;
    View btnItemGreen;
    ViewPager viewPager;
    View btnAnnounce;
    CardView search;
    TextView tvLiveTitle;
    TextView announcementTitle;
    TextView announcementType;
    ImageView announcementIcon;
    ImageView iv_live_icon;
    CardView cv_farm_visit;
    CardView cv_guide_clips;

    TextView tv_region, tv_skill, tv_type;
    ArrayList<Item> skills_list;


    private FragmentDashboardBinding binding;

    ArrayList<String> skilles_type;
    private final String LIVE_URL_SEARCH_LIST = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UCO7z6GRmfgabhF-FYDIzfng&type=video&eventType=live&key=AIzaSyATOolSm0OfEYLCL5jqQ3dw7c3iY35D-Go";
    private final String BASE_URL_YOUTUBE = "https://youtu.be/";
    int currentPage = 0;
    int NUM_PAGES = 0;

    Date serverTime;
    final CustomUiController[] customUiController = {null};


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //*********************** api calls *********************** //
        getDashboard();
        getWeather();
        getServerTime();

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //init views vars
        initViews();

        //on views init finished
        initTableViewsHeight();

        skills_list = DataUtilHelper.getSkillsFromLocalStorage(requireContext());
        if (skills_list == null) {
            ApiMethodsHelper.getSkills(msg -> skills_list = (ArrayList<Item>) msg);
        }

        btnWeather.setOnClickListener(view13 -> {


            Intent intent = new Intent(getActivity(), WeatherDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        binding.layoutDashboard.cvWeatherInfo.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), WeatherDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        btnQA.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivity(), QuestionsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        binding.layoutDashboard.virtualClinicCv.setOnClickListener((view1) -> {
            startActivity(new Intent(getActivity(), VirtualClinicActivity.class));
        });


        btnChatBot.setOnClickListener(view14 -> openConsultantsChat());

        btnManagementConsulting.setOnClickListener(view1 -> {
            if (getActivity() == null) return;

            if (!isTimeAvailableForManagementConsulting()) {
                DialogUtil.showInfoDialog(
                        getActivity(),
                        2,
                        getString(R.string.management_branches),
                        getString(R.string.management_consulting_msg),
                        (b) -> {}
                );
                return;
            }
            DialogCallManagementConsultingUtil dCMC = new DialogCallManagementConsultingUtil(requireActivity());
            dCMC.openSkillsDialog(ManagementType.BRANCHES);

        });

        btnManagementMinistry.setOnClickListener(view15 -> {
            if (getActivity() == null) return;

            if (!isTimeAvailableForManagementConsulting()) {
                DialogUtil.showInfoDialog(
                        getActivity(),
                        2,
                        getString(R.string.management_branches),
                        getString(R.string.management_consulting_msg),
                        (b) -> {}
                );
                return;
            }
            DialogCallManagementConsultingUtil dCMC = new DialogCallManagementConsultingUtil(requireActivity());
            dCMC.openSkillsDialog(ManagementType.MINISTRY);

        });


        binding.layoutDashboard.cvMarkets.setOnClickListener(view1 -> {
            FragmentSelectorActivity.startActivity(requireContext(), FragmentSelectorActivity.MARKET_PAGE);
        });


        search.setOnClickListener(view16 -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        cv_farm_visit.setOnClickListener(view1 -> startActivity(new Intent(getContext(), FarmVisitListActivity.class)));

        cv_guide_clips.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), ClipsIndicativeCategoryActivity.class)));

        binding.layoutDashboard.cvReefComponents.setOnClickListener(view1 -> startActivity(new Intent(requireActivity(), ReefComponentsActivity.class)));

        binding.layoutDashboard.llReport.setOnClickListener(view1 -> startLabActivity());

        binding.layoutDashboard.cvCoffeeDisease.setOnClickListener(v -> {
            if (getActivity() == null) return;

            Intent intent = new Intent(getContext(), CoffeePlantActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        binding.layoutDashboard.cvWorkersService.setOnClickListener(v -> {
            openWorkersService();
        });

        //stopping framer check for testing only
//        if (ProfileHelper.hasAccount(getContext())) {
//            if (ProfileHelper.getAccount(requireContext()).isFarmer())
//        showRequestGuideDialog();
//        }

//        binding.layoutDashboard.callConsultant.setOnClickListener(view1 -> {
//            DialogCallConsultantUtil dialogCallConsultantUtil = new DialogCallConsultantUtil(requireActivity());
//            dialogCallConsultantUtil.showSelectSkillAndRegion();
//        });

        ArrayList<String> medias = new ArrayList<>();
        medias.add(getURLForResource(R.drawable.img_1_advice));
        medias.add(getURLForResource(R.drawable.img_2_qna));
        medias.add(getURLForResource(R.drawable.img_3_labs));

        setSlider(medias);

//        AnimationUtilsLocal.upDownInfinite(requireContext(), binding.layoutDashboard.ivArrowOfShowRest);
        binding.layoutDashboard.tvShowRestOfLayout.setOnClickListener(view1 -> {
            if (binding.layoutDashboard.llRow4.getVisibility() == View.VISIBLE) {
                binding.layoutDashboard.tvShowRestOfLayout.setText(R.string.show_all_services);
                binding.layoutDashboard.llRow4.setVisibility(View.GONE);
                binding.layoutDashboard.llRow5.setVisibility(View.GONE);
                binding.layoutDashboard.llRow6.setVisibility(View.GONE);
            } else {
                binding.layoutDashboard.tvShowRestOfLayout.setText(R.string.show_less);
                binding.layoutDashboard.llRow4.setVisibility(View.VISIBLE);
                binding.layoutDashboard.llRow5.setVisibility(View.VISIBLE);
                binding.layoutDashboard.llRow6.setVisibility(View.VISIBLE);
            }

        });

        return view;
    }

    private boolean isTimeAvailableForManagementConsulting() {
        if (serverTime == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(serverTime);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) return false;
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        return hourOfDay >= 8 && hourOfDay <= 14;
    }

    private void openWorkersService() {
        binding.layoutDashboard.progressBar2.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getCurrentWorker("Bearer " + TokenHelper.getToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Response<NewAPIsResponse<Worker>> response) {
                binding.layoutDashboard.progressBar2.setVisibility(View.GONE);
                if (response.body() != null && response.body().getStatus()) {
                    startActivity(new Intent(requireActivity(), AvailableWorkerActivity.class));
                    if (getContext() != null)
                        WorkerHelper.createOrUpdate(response.body().getData(), getContext());
                    return;
                }
                startActivity(new Intent(getContext(), WorkerIntroActivity.class));
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Throwable t) {
                binding.layoutDashboard.progressBar2.setVisibility(View.GONE);
                startActivity(new Intent(getContext(), WorkerIntroActivity.class));
            }
        });
    }

    private void initViews() {
        recyclerViewSection1 = binding.layoutDashboard.rvSection1;
        recyclerViewSection2 = binding.layoutDashboard.rvSection2;
        recyclerViewSection5 = binding.layoutDashboard.rvSection5;
        tvSection5 = binding.layoutDashboard.tvSection5; //rv5
        tvAllAg = binding.layoutDashboard.tvAllAg; //rv5
        ivBanner2 = binding.layoutDashboard.ivBanner2;
        youTubePlayerView = binding.layoutDashboard.youtubePlayerView;
        ivFarmerStat = binding.layoutDashboard.ivFarmerStat;
        ivMessageStat = binding.layoutDashboard.ivMessageStat;
        tvFarmerStat = binding.layoutDashboard.tvFarmerStat;
        tvMessageStat = binding.layoutDashboard.tvMessageStat;
        farmerCardView = binding.layoutDashboard.farmerCardView;
        messageCardView = binding.layoutDashboard.messageCardView;
        ivCalender = binding.layoutDashboard.btnItemGreen;
        progressBar = binding.layoutDashboard.progressBar2;
        btnQA = binding.layoutDashboard.btnShowQA;
        projectResearchAppBtn = binding.layoutDashboard.projectsResearchApp;
        btnManagementMinistry = binding.layoutDashboard.cvManagementMinistry;
        btnChatBot = binding.layoutDashboard.requestConsult;
        btnManagementConsulting = binding.layoutDashboard.cvManagementConsulting;
        btnWeather = binding.layoutDashboard.btnWeather;
        ivWeather = binding.layoutDashboard.ivWeather;
        tvWeather = binding.layoutDashboard.tvWeather;
        tvWeatherCondition = binding.layoutDashboard.tvWeatherCondition;
        ivItemYellowOne = binding.layoutDashboard.ivItemYellowOne;
        ivItemYellowTwo = binding.layoutDashboard.ivItemYellowTwo;
        ivItemGreen = binding.layoutDashboard.ivItemGreen;
        tvItemGreen = binding.layoutDashboard.tvItemGreen;
        tvItemYellowOne = binding.layoutDashboard.tvItemYellowOne;
        tvItemYellowTwo = binding.layoutDashboard.tvItemYellowTwo;
        btnItemYellowOne = binding.layoutDashboard.btnItemYellowOne;
        btnItemYellowTwo = binding.layoutDashboard.btnItemYellowTwo;
        btnItemGreen = binding.layoutDashboard.btnItemGreen;
        viewPager = binding.layoutDashboard.viewPager;
        btnAnnounce = binding.layoutDashboard.announcementIcon;
        search = binding.layoutDashboard.search;
        tvLiveTitle = binding.layoutDashboard.tvLiveTitle;
        announcementTitle = binding.layoutDashboard.announcementTitle;
        announcementType = binding.layoutDashboard.announcementType;
        announcementIcon = binding.layoutDashboard.announcementIcon;
        iv_live_icon = binding.layoutDashboard.ivLiveIcon;
        cv_farm_visit = binding.layoutDashboard.cvFarmVisit;
        cv_guide_clips = binding.layoutDashboard.cvGuideClips;
    }

    private void initTableViewsHeight() {
//        binding.layoutDashboard.llRow1.measure(0,0);
        ViewTreeObserver vto = binding.layoutDashboard.llRow1.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.layoutDashboard.llRow1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = binding.layoutDashboard.llRow1.getMeasuredWidth() / 3;
                binding.layoutDashboard.llRow1.getLayoutParams().height = height;
                binding.layoutDashboard.llRow2.getLayoutParams().height = height;
                binding.layoutDashboard.llRow3.getLayoutParams().height = height;
                binding.layoutDashboard.llRow4.getLayoutParams().height = height;
                binding.layoutDashboard.llRow5.getLayoutParams().height = height;
                binding.layoutDashboard.llRow6.getLayoutParams().height = height;
                binding.layoutDashboard.llRow4.setVisibility(View.GONE);
                binding.layoutDashboard.llRow5.setVisibility(View.GONE);
                binding.layoutDashboard.llRow6.setVisibility(View.GONE);
            }
        });

    }

    private void startLabActivity() {
        if (getActivity() == null) return;

        User user = ProfileHelper.getAccount(getContext());
        if (user != null)
            if (user.getStatus() != null) {
                if (user.isProfileComplete() && !user.getStatus().equals("")) {
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
    }

    private void setSlider(ArrayList<String> medias) {

        if (medias == null) return;

//        ArrayList<Page> pageViewsPack = new ArrayList<>();
//
//        for (String media : medias)
//            pageViewsPack.add(new Page(media));
//
//        IndicatorConfiguration configurationPack = new IndicatorConfiguration.Builder()
//                .imageLoader(new CustomImageLoader())
//                .isStopWhileTouch(true)
//                .position(IndicatorConfiguration.IndicatorPosition.Center_Top)
//                .onPageClickListener(new OnPageClickListener() {
//                    @Override
//                    public void onPageClick(int position, Page page) {
//                        switch (position) {
//                            case 0:
//                                DialogCallConsultantUtil dialogCallConsultantUtil = new DialogCallConsultantUtil(requireActivity());
//                                dialogCallConsultantUtil.showSelectSkillAndRegion();
//                                break;
//                            case 1:
//                                Intent intent = new Intent(getActivity(), QuestionsActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                startActivity(intent);
//                                break;
//                            default:
//                                startLabActivity();
//                        }
//                    }
//                })
//                .build();
//        binding.layoutDashboard.indicatorFarmProblemImages.init(configurationPack);
//        binding.layoutDashboard.indicatorFarmProblemImages.notifyDataChange(pageViewsPack);


    }

    public String getURLForResource(int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + Objects.requireNonNull(R.class.getPackage()).getName() + "/" + resourceId).toString();

    }

    private void showRequestGuideDialog() {
        Dialog request_guide_dialog = new Dialog(requireActivity());//, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        request_guide_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        request_guide_dialog.setContentView(R.layout.dialog_call_consult);
        request_guide_dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.round_border_dialog_call_consult_bg));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            request_guide_dialog.getWindow().setBackgroundBlurRadius(10);
        }
        LinearLayout ll_type_picker = request_guide_dialog.findViewById(R.id.ll_type_picker);
        LinearLayout ll_skills_picker = request_guide_dialog.findViewById(R.id.ll_skills_picker);
        LinearLayout ll_region_picker = request_guide_dialog.findViewById(R.id.ll_region_picker);
        ImageButton btn_close = request_guide_dialog.findViewById(R.id.btn_close);
        ImageButton btn_request_guide = request_guide_dialog.findViewById(R.id.btn_request_guide);
        tv_type = request_guide_dialog.findViewById(R.id.tv_type);
        tv_skill = request_guide_dialog.findViewById(R.id.tv_skill);
        tv_region = request_guide_dialog.findViewById(R.id.tv_region);
        Button btn_search = request_guide_dialog.findViewById(R.id.btn_search);

        btn_search.setOnClickListener(v -> {
            if (tv_skill.getText().toString().isEmpty()) {
                Util.showErrorToast(getString(R.string.all_required), requireActivity());
                return;
            }

            Item item = getSkillItemByName(tv_skill.getText().toString());
            if (item == null) return;

            if (item.getChildrens().size() > 0) {
                Intent intent = new Intent(getActivity(), CategoryChildActivity.class);
                intent.putExtra("CHILDREN", item.getChildrens());
                if (!tv_region.getText().toString().trim().isEmpty() && !tv_region.getText().toString().contains("المناطق"))
                    intent.putExtra("region_name", tv_region.getText().toString());
                intent.putExtra("NAME", item.getNameAr());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), SelectUserActivity.class);
                intent.putExtra("is_sorted", true);
                if (!tv_region.getText().toString().trim().isEmpty() && !tv_region.getText().toString().contains("المناطق"))
                    intent.putExtra("region_name", tv_region.getText().toString());
                intent.putExtra("skill", item.getNameAr());
                startActivity(intent);
            }
            request_guide_dialog.dismiss();
        });

        ll_type_picker.setOnClickListener(v -> {
            skilles_type = new ArrayList<>();
            skilles_type.add("تخصصات ادارية");
            skilles_type.add("تخصصات فنية");

            DialogUtil.DialogListOfItems<String> dialogListOfItems = new DialogUtil.DialogListOfItems<>(requireActivity(), "نوع التخصصات", skilles_type, msg -> tv_type.setText(((TextView) msg).getText().toString()));
            dialogListOfItems.show();
        });

        ll_skills_picker.setOnClickListener(v -> {
            skills_list = DataUtilHelper.getSkillsFromLocalStorage(requireContext());
            if (skills_list == null) {
                ApiMethodsHelper.getSkills(msg -> dialogForRegionsAndSkills((ArrayList<Item>) msg, getString(R.string.select_a_skill), true));
            } else {
                dialogForRegionsAndSkills(skills_list, getString(R.string.select_a_skill), true);
            }

        });

        ll_region_picker.setOnClickListener(v -> {
            ArrayList<Item> regions = DataUtilHelper.getRegions();
            if (regions == null) {
                ApiMethodsHelper.getRegions(msg -> dialogForRegionsAndSkills(msg, getString(R.string.select_a_region), false));
            } else {
                dialogForRegionsAndSkills(regions, getString(R.string.select_a_region), false);
            }
        });


        btn_request_guide.setOnClickListener(v -> {
            Item item = getSkillItemByName("الأضاحي");
            if (item == null) return;

            if (item.getChildrens().size() > 0) {
                Intent intent = new Intent(getActivity(), CategoryChildActivity.class);
                intent.putExtra("CHILDREN", item.getChildrens());
                if (!tv_region.getText().toString().trim().isEmpty() && !tv_region.getText().toString().contains("المناطق"))
                    intent.putExtra("region_name", tv_region.getText().toString());
                intent.putExtra("NAME", item.getNameAr());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), SelectUserActivity.class);
                intent.putExtra("is_sorted", true);
                if (!tv_region.getText().toString().trim().isEmpty() && !tv_region.getText().toString().contains("المناطق"))
                    intent.putExtra("region_name", tv_region.getText().toString());
                intent.putExtra("skill", item.getNameAr());
                startActivity(intent);
            }
            request_guide_dialog.dismiss();
        });
        btn_close.setOnClickListener(v -> request_guide_dialog.dismiss());

        request_guide_dialog.show();
    }

    private Item getSkillItemByName(String skill_name) {
        if (skills_list == null) return null;
        for (Item skill : skills_list) {
            for (Item s : skill.getChildrens()) {
                if (s.getNameAr().equals(skill_name))
                    return s;
            }
            if (skill.getNameAr().equals(skill_name))
                return skill;
        }
        return null;
    }


    private void dialogForRegionsAndSkills(ArrayList<Item> items, String title, boolean isSkills) {
        ArrayList<String> names = new ArrayList<>();
        if (isSkills) {
            for (Item item : items) {
                if (item.getChildrens().size() > 0) {
                    for (Item i : item.getChildrens()) {
                        names.add(i.getNameAr());
                    }
                } else {
                    names.add(item.getNameAr());
                }
            }
        } else
            names.add("جميع المناطق");
        for (Item item : items) {
            names.add(item.getNameAr());
        }

        DialogUtil.DialogListOfItems<String> dialogListOfItems = new DialogUtil.DialogListOfItems<>(requireActivity(), title, names, msg -> {
            if (isSkills)
                tv_skill.setText(((TextView) msg).getText().toString());
            else
                tv_region.setText(((TextView) msg).getText().toString());
        });
        dialogListOfItems.show();
    }

    private void openConsultantsChat() {
        if (getContext() == null) return;
        User user = ProfileHelper.getAccount(getContext());
        if (user != null) {
            if (user.isProfileComplete() && !user.getStatus().equals("")) {
                if ((user.isConsultantUser() && user.isApproved()) || user.isFarmer()) {
                    Intent intent = new Intent(getContext(), ChatUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    Util.showErrorToast(getResources().getString(R.string.account_pending), getActivity());
                }

            } else {
                Util.showErrorToast(getResources().getString(R.string.complete_profile_msg), getActivity());
            }
        } else
            startActivity(new Intent(getContext(), LoginActivity.class));
    }

    private void getDashboard() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<DashboardResponse> call = apiInterface.getDashboard();
        call.enqueue(new Callback<DashboardResponse>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {

                progressBar.setVisibility(View.GONE);

                try {
                    if (response.body() == null) return;

                    //*********************** first section *********************** //
                    setFisrtSection(response.body().getSectionOne());
                    //*********************** second section *********************** //
                    setSecondSection(response.body().getSectionTwo());
                    setThirdSection(response.body().getSectionThree());
                    //*********************** calender and article section *********************** //
                    setFifthSection(response.body().getSectionFour());
                    //*********************** media section *********************** //
                    setNinthSection(response.body());
                    RealPathUtil.readJsonFromUrl(LIVE_URL_SEARCH_LIST, jsonObject -> {
                        if (getContext() == null) return;
                        if (jsonObject == null) {
                            setTenSection(response.body().getLiveTransmission(), null);
                            return;
                        }
                        String live_video_url = null;
                        try {
                            String video_id = "";
                            if (!jsonObject.getJSONArray("items").isNull(0))
                                video_id = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");

                            if (!video_id.equals("")) {
                                iv_live_icon.setBackground(getContext().getDrawable(R.drawable.shape_oval_red));
                                tvLiveTitle.setText(R.string.live_now);
                                live_video_url = BASE_URL_YOUTUBE.concat(video_id);
                            } else {
                                iv_live_icon.setBackground(getContext().getDrawable(R.drawable.shape_oval_gray));
                                tvLiveTitle.setText(R.string.live_off);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        setTenSection(response.body().getLiveTransmission(), live_video_url);

                    });
                    //*********************** bottom status *********************** //
                    setStateSection(response.body().getStats());
                    //*********************** annoucement section *********************** //
                    setAnnouncement(response.body().getAdB());

                    StorageHelper.saveContactUs(response.body().getContactUs());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Log.v("Bassam", t.getLocalizedMessage());
                //  progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getWeather() {

        String weather = "OERK";

        if (ProfileHelper.getAccount(getContext()) != null)
            weather = ProfileHelper.getAccount(getContext()).getWeatherCode();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<WeatherResponse> call = apiInterface.getWeather(weather);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {

                try {

                    if (response.body() == null || getContext() == null) return;

                    String icon = response.body().getCurrentIcon().replaceAll(" .*", "").replace("-", "_");

                    Log.v("Weather", icon);

                    String str = response.body().getCurrentTemperature() + "°c";
                    tvWeather.setText(str);
                    int resId = getResources().getIdentifier(icon, "drawable", getContext().getPackageName());
                    ivWeather.setImageResource(resId);
                    tvWeatherCondition.setText(response.body().getWeather_PhenomenonAr());

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {

            }
        });
    }


    private void getServerTime() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getServerTime(TokenHelper.getBearerToken()).enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                if (response.body() != null && response.body().getStatus()) {
                    if (getContext() == null) return;
                    DateFormat dateFormat = DateFormat.getDateTimeInstance();
                    try {
                        serverTime = dateFormat.parse(response.body().getData());
                    } catch (ParseException e) {
                        serverTime = new Date();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                Log.e("ServerTime", t.getMessage());
            }
        });
    }

    private void setAnnouncement(Section announcement) {

        if (getContext() == null) return;
        if (announcement.getEnabled().equals("on"))
            btnAnnounce.setVisibility(View.VISIBLE);
        else
            btnAnnounce.setVisibility(View.GONE);

        Glide.with(getContext()).load(APIClient.imageUrl + announcement.getBanner()).into(announcementIcon);

        announcementTitle.setText(announcement.getTitle());
        announcementType.setText(announcement.getType());

        btnAnnounce.setOnClickListener(view -> {

            if (announcement.getUrl() != null && (announcement.getUrl().contains("http") || announcement.getUrl().contains("www"))) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(announcement.getUrl()));
                startActivity(browserIntent);
            }
        });
    }



    private void setFisrtSection(Section firstSection) {
        binding.layoutDashboard.cvSection1.setOnClickListener(v -> {
            openSection1Dialog(firstSection);
        });

    }

    private void openSection1Dialog(Section firstSection) {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_border_green);
        dialog.setContentView(R.layout.dialog_list);

        TextView title = dialog.findViewById(R.id.tv_title);
        RecyclerView listView = dialog.findViewById(R.id.list_view);
        title.setText(firstSection.getTitle());
        CategoryAdapter adapter = new CategoryAdapter(firstSection.getCategoryList(), getContext(), this, false);
        listView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        listView.setAdapter(adapter);
        listView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
        dialog.show();
    }

    private void setSecondSection(Section secondSection) {


//        viewPager.setAdapter(new AgriProductAdapter(secondSection.getArticleList(), getContext(), this));
//
//        indicator.setViewPager(viewPager);
//
//        if (secondSection.getArticleList().size() > 1) {
//            viewPager.setCurrentItem(secondSection.getArticleList().size() - 1);
//            currentPage = secondSection.getArticleList().size() - 1;
//        }
//
//        final float density = getResources().getDisplayMetrics().density;
//
//        //Set circle indicator radius
//        indicator.setRadius(5 * density);
//
//        NUM_PAGES = secondSection.getArticleList().size();
//
//        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int position) {
//                currentPage = position;
//            }
//
//            @Override
//            public void onPageScrolled(int pos, float arg1, int arg2) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int pos) {
//
//            }
//        });


    }

    @Override
    public void onCategoryClick(View view, int position, Item item) {

        Intent intent = new Intent(getContext(), ItemListActivity2.class);
        intent.putExtra("TITLE", item.getNameAr());
        intent.putExtra("CAT_ID", item.getId());
        startActivity(intent);

    }

    @Override
    public void onProductClick(View view, int position, Item item) {

        Intent intent = new Intent(getContext(), CalendarDetailActivity.class);
        intent.putExtra("CAT_ID", item.getId());
        startActivity(intent);

    }

    private void setThirdSection(Section thirdSection) {
        if (getContext() == null) return;


        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        if (thirdSection.getArticleList() != null) {
            if (thirdSection.getArticleList().size() > 2) {
                RequestOptions requestOptions = new RequestOptions().override(Build.VERSION.SDK_INT < Build.VERSION_CODES.P ? 200 : 400, Build.VERSION.SDK_INT < Build.VERSION_CODES.P ? 200 : 400);

                Glide.with(getContext()).load(APIClient.imageUrl + thirdSection.getArticleList().get(0).getImage()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivItemGreen);
                Glide.with(getContext()).load(APIClient.imageUrl + thirdSection.getArticleList().get(1).getImage()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivItemYellowOne);
                Glide.with(getContext()).load(APIClient.imageUrl + thirdSection.getArticleList().get(2).getImage()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivItemYellowTwo);

                tvItemGreen.setText(thirdSection.getArticleList().get(0).getTitle_ar());
                tvItemYellowOne.setText(thirdSection.getArticleList().get(1).getTitle_ar());
                tvItemYellowTwo.setText(thirdSection.getArticleList().get(2).getTitle_ar());

//                binding.layoutDashboard.cvCalenderFarming.setOnClickListener(view -> {
//
//                    Intent intent = new Intent(getContext(), CalendarDetailActivity.class);
//                    intent.putExtra("CAT_ID", thirdSection.getArticleList().get(0).getId());
//                    startActivity(intent);
//
//                });

                btnItemYellowOne.setOnClickListener(view -> {


                    Intent intent = new Intent(getContext(), CalendarDetailActivity.class);
                    intent.putExtra("CAT_ID", thirdSection.getArticleList().get(1).getId());
                    startActivity(intent);
                });

                btnItemYellowTwo.setOnClickListener(view -> {

                    Intent intent = new Intent(getContext(), CalendarDetailActivity.class);
                    intent.putExtra("CAT_ID", thirdSection.getArticleList().get(2).getId());
                    startActivity(intent);

                });

            }
        }


    }


    private void setFifthSection(Section fifthSection) {
        binding.layoutDashboard.qAAgricultural.setOnClickListener(view -> openSection5Dialog(fifthSection));

    }

    private void openSection5Dialog(Section fifthSection) {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_border_green);
        dialog.setContentView(R.layout.dialog_list);

        TextView title = dialog.findViewById(R.id.tv_title);
        RecyclerView listView = dialog.findViewById(R.id.list_view);
        title.setText(fifthSection.getTitle());
        CategoryAdapter adapter = new CategoryAdapter(fifthSection.getCategoryList(), getContext(), this, true);
        listView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        listView.setAdapter(adapter);
        listView.setItemAnimator(new DefaultItemAnimator());
        dialog.show();
    }

    //Banner
    private void setNinthSection(DashboardResponse dashboardResponse) {

        ivBanner2.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ItemListActivity2.class);
            intent.putExtra("TITLE", "المكتبة الرقمية");
            intent.putExtra("CAT_ID", dashboardResponse.getMediaLibraryCatID());
            startActivity(intent);
        });

        projectResearchAppBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ItemListActivity2.class);
            intent.putExtra("TITLE", "مشاريع الأبحاث التطبيقية");
            intent.putExtra("CAT_ID", 86);
            startActivity(intent);
        });

    }

    //Banner
    private void setTenSection(SectionBanner seventhSection, String live_video_url) {


        if (live_video_url == null)
            live_video_url = seventhSection.getUrl();

        if (seventhSection == null) {
            youTubePlayerView.setVisibility(View.GONE);
            return;
        }


        tvLiveTitle.setText(seventhSection.getTitle());

//        View customUiView = youTubePlayerView.inflateCustomPlayerUi(R.layout.layout_custom_youtube_ui);

        String finalLive_video_url = live_video_url;
        binding.layoutDashboard.btnLive.setOnClickListener(view -> {

            Log.v("Live Url: ", finalLive_video_url);
            Intent intent = new Intent(getContext(), VideoViewActivity.class);
            intent.putExtra("path", finalLive_video_url);
            intent.putExtra("is_youtube", true);
            startActivity(intent);
        });

//        youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
//            customUiController[0] = new CustomUiController(customUiView, finalLive_video_url);
//            youTubePlayer.play();
//        });
    }

    private void setStateSection(ArrayList<Stats> stats) {
        if (getContext() == null) return;

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        if (stats != null) {

            if (stats.size() > 2) {
                tvMessageStat.setText(stats.get(1).getTitle());
                tvFarmerStat.setText(stats.get(0).getTitle());


                RequestOptions requestOptions = new RequestOptions().override(400, 400);
//                Glide.with(getContext()).load(stats.get(1).getIcon()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivMessageStat);
//                Glide.with(getContext()).load(stats.get(0).getIcon()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivFarmerStat);
//                Glide.with(getContext()).load(stats.get(2).getIcon()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivConsultantStat);

                messageCardView.setCardBackgroundColor(getContext().getColor(R.color.white));
                farmerCardView.setCardBackgroundColor(getContext().getColor(R.color.white));

                //hide massages number
                //created by amin

            }
        }

    }

    class CustomUiController {

        CustomUiController(View customPlayerUI, String path) {
            FrameLayout fl_cover_video = customPlayerUI.findViewById(R.id.fl_cover_video);

            fl_cover_video.setOnClickListener(v -> {

                Intent intent = new Intent(getContext(), VideoViewActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("is_youtube", true);
                startActivity(intent);
            });
        }
    }
}