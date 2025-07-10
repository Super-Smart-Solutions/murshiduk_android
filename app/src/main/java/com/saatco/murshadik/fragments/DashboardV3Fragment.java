package com.saatco.murshadik.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.CalendarDetailActivity;
import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.LabReportActivitiy;
import com.saatco.murshadik.NewMainActivityDesign;
import com.saatco.murshadik.QuestionsActivity;
import com.saatco.murshadik.VirtualClinicActivity;
import com.saatco.murshadik.WeatherDetailActivity;
import com.saatco.murshadik.adapters.DesignV3.DashboardNewsAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.DashboardResponse;
import com.saatco.murshadik.api.response.WeatherResponse;
import com.saatco.murshadik.databinding.FragmentDashboardV3Binding;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.model.Section;
import com.saatco.murshadik.model.Stats;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardV3Fragment extends Fragment {

    private FragmentDashboardV3Binding binding;

    public DashboardV3Fragment() {
        // Required empty public constructor
    }

    public static DashboardV3Fragment newInstance() {
        return new DashboardV3Fragment();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardV3Binding.inflate(inflater, container, false);



        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initHeader();
        binding.btnRequestConsultation.setOnClickListener(v -> {
            // open consultation appointments
            Intent intent = new Intent(getContext(), ConsultationAppointmentsActivity.class);
            startActivity(intent);
        });

        binding.scClinics.setOnClickListener(v -> {
            // open clinics
            Intent intent = new Intent(getContext(), VirtualClinicActivity.class);
            startActivity(intent);
        });

        binding.scLabs.setOnClickListener(v -> {
            // open labs
            Intent intent = new Intent(getContext(), LabReportActivitiy.class);
            startActivity(intent);
        });

        binding.scQNA.setOnClickListener(v -> {
            // open QNA
            Intent intent = new Intent(getContext(), QuestionsActivity.class);
            startActivity(intent);
        });

        binding.scWeather.setOnClickListener(v -> {
            // open weather
            Intent intent = new Intent(getContext(), WeatherDetailActivity.class);
            startActivity(intent);
        });

        binding.scUdhiyah.setOnClickListener(v -> {
            // open virtual clinic
            Intent intent = new Intent(getContext(), VirtualClinicActivity.class);
            startActivity(intent);
        });

        binding.tvViewAllServices.setOnClickListener(v -> {
            // open consultation appointments
            if(getActivity() != null){
                NewMainActivityDesign activity = (NewMainActivityDesign) getActivity();
                activity.openServices();
            }
        });
    }


    private void initHeader() {
        // Initialize the header
        User user = ProfileHelper.getAccount(getContext());
        if (user != null) {
            String greeting = "مرحباً " + user.getFirstName();
            binding.layoutHeader.tvUserName.setText(greeting);
            String location = user.getLocation().replace("منطقة", "") + " . " + user.getPrefix();
            binding.layoutHeader.tvUserLocation.setText(location);

            // set date now formated like "Saturday, 20 March"
            Locale locale = new Locale("ar");
            String fullDate = DateFormat.format("EEEE, dd MMMM", System.currentTimeMillis()).toString();
            binding.layoutHeader.tvDate.setText(fullDate);

            if (user.getPhotoUrl() != null) {
                binding.layoutHeader.ivUserAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(this)
                        .load(APIClient.imageUrl + user.getPhotoUrl())
                        .into(binding.layoutHeader.ivUserAvatar);
            }
        }
    }

    //*********************** init news section *********************** //
    private void initNewsSection(Section newsSection) {
        if (newsSection == null) return;
        DashboardNewsAdapter adapter = new DashboardNewsAdapter(getContext(),
                newsSection.getArticleList().stream().sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).collect(Collectors.toList()),
                (position, item) -> {
            // open news details
            Intent intent = new Intent(getContext(), CalendarDetailActivity.class);
            intent.putExtra("CAT_ID", item.getId());
            startActivity(intent);
        });
        binding.rvNews.setAdapter(adapter);
    }


    private void getDashboard() {

        binding.progressBar2.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<DashboardResponse> call = apiInterface.getDashboard();
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {

                binding.progressBar2.setVisibility(View.GONE);

                try {
                    if (response.body() == null) return;

                    //*********************** first section *********************** //
//                    setFisrtSection(response.body().getSectionOne());

                    //*********************** second section *********************** //
                    initNewsSection(response.body().getSectionTwo());
//                    setThirdSection(response.body().getSectionThree());
                    //*********************** calender and article section *********************** //
//                    setFifthSection(response.body().getSectionFour());
                    //*********************** media section *********************** //
//                    setNinthSection(response.body());

                    //*********************** bottom status *********************** //
                    initStateSection(response.body().getStats());
                    //*********************** annoucement section *********************** //
//                    setAnnouncement(response.body().getAdB());

                    //*********************** save youtube link *********************** //
                    //*********************** first section *********************** //
                    if(getActivity() != null){
                        NewMainActivityDesign activity = (NewMainActivityDesign) getActivity();
                        activity.youtubeVideoUrl = response.body().getLiveTransmission().getUrl();
                        activity.agricultureNoteItems = response.body().getSectionOne().getCategoryList();
                    }

                    //*********************** save contact us *********************** //
                    StorageHelper.saveContactUs(response.body().getContactUs());

                } catch (Exception ex) {
                    Log.e("DashboardV3Fragment", "onResponse: ", ex);
                }

            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Log.e("DashboardV3Fragment", "onResponse: ", t);
                binding.progressBar2.setVisibility(View.GONE);
            }
        });
    }

    private void initStateSection(ArrayList<Stats> stats) {
        if (stats != null) {

            if (stats.size() > 2) {
                binding.tvStatisticsFarmer.setText(stats.get(1).getTitle());
                binding.tvStatisticsConsultants.setText(stats.get(0).getTitle());


                RequestOptions requestOptions = new RequestOptions().override(400, 400);
//                Glide.with(getContext()).load(stats.get(1).getIcon()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivMessageStat);
//                Glide.with(getContext()).load(stats.get(0).getIcon()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivFarmerStat);
//                Glide.with(getContext()).load(stats.get(2).getIcon()).apply(requestOptions).placeholder(circularProgressDrawable).into(ivConsultantStat);

//                messageCardView.setCardBackgroundColor(getContext().getColor(R.color.white));
//                farmerCardView.setCardBackgroundColor(getContext().getColor(R.color.white));

                //hide massages number
                //created by amin

            }
        }
    }

    private void getWeather() {

        String weather = "OERK";

        if (ProfileHelper.getAccount(getContext()) != null)
            weather = ProfileHelper.getAccount(getContext()).getWeatherCode();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<WeatherResponse> call = apiInterface.getWeather(weather);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {

                try {

                    if (response.body() == null || getContext() == null) return;

                    String icon = response.body().getCurrentIcon().replaceAll(" .*", "").replace("-", "_");

                    Log.v("Weather", icon);

                    String tempWithC = response.body().getCurrentTemperature() + "°c";
                    @SuppressLint("DiscouragedApi")
                    int resId = getResources().getIdentifier(icon, "drawable", getContext().getPackageName());
                    binding.layoutHeader.tvTemperature.setText(tempWithC);
                    binding.layoutHeader.ivWeatherIcon.setImageResource(resId);
                    binding.layoutHeader.tvWeatherStatus.setText(response.body().getWeather_PhenomenonAr());

                    if (getActivity() != null) {
                        NewMainActivityDesign activity = (NewMainActivityDesign) getActivity();
                        activity.weatherTemperature = tempWithC;
                        activity.weatherResIcon = resId;
                        activity.weatherPhenomenon = response.body().getWeather_PhenomenonAr();
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {

            }
        });
    }

}
