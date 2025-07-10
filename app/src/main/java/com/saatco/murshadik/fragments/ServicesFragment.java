package com.saatco.murshadik.fragments;

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
import com.saatco.murshadik.AgricultureNoteActivity;
import com.saatco.murshadik.AvailableWorkerActivity;
import com.saatco.murshadik.ChatUserActivity;
import com.saatco.murshadik.ClipsIndicativeCategoryActivity;
import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.FragmentSelectorActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.WorkerHelper;
import com.saatco.murshadik.ItemListActivity2;
import com.saatco.murshadik.LabReportActivitiy;
import com.saatco.murshadik.LoginActivity;
import com.saatco.murshadik.MarketsActivity;
import com.saatco.murshadik.NewMainActivityDesign;
import com.saatco.murshadik.QuestionsActivity;
import com.saatco.murshadik.PestIdentificationActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.ReefComponentsActivity;
import com.saatco.murshadik.VideoViewActivity;
import com.saatco.murshadik.VirtualClinicActivity;
import com.saatco.murshadik.WeatherDetailActivity;
import com.saatco.murshadik.WorkerIntroActivity;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentServicesBinding;
import com.saatco.murshadik.enums.ManagementType;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.workersService.Worker;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.MyCallbackHandler;
import com.saatco.murshadik.utils.RealPathUtil;
import com.saatco.murshadik.utils.Util;

import org.json.JSONException;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServicesFragment extends Fragment {

    private final String BASE_URL_YOUTUBE = "https://youtu.be/";
    private String live_video_url;
    FragmentServicesBinding binding;

    public ServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServicesFragment newInstance() {
        return new ServicesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentServicesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initHeader();

        binding.scQNA.setOnClickListener(v -> {
            // open QNA
            Intent intent = new Intent(getContext(), QuestionsActivity.class);
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

        binding.scWeather.setOnClickListener(v -> {
            // open weather
            Intent intent = new Intent(getContext(), WeatherDetailActivity.class);
            startActivity(intent);
        });

        binding.scMarket.setOnClickListener(v -> {
            // open market
            if (getContext() != null) {
                MarketsActivity.startActivity(getContext());
            }
        });

        binding.scLibrary.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemListActivity2.class);
            intent.putExtra("TITLE", "المكتبة الرقمية");
            intent.putExtra("CAT_ID", Consts.CAT_ID_DIGITAL_LIBRARY);
            startActivity(intent);
        });

        binding.scMinistryManagements.setOnClickListener(v -> {
            ConsultationAppointmentsActivity.start(getContext(), true, ManagementType.MINISTRY);
        });

        binding.scReef.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ReefComponentsActivity.class));
        });

        binding.scBranchServices.setOnClickListener(v -> {
            ConsultationAppointmentsActivity.start(getContext(), true, ManagementType.BRANCHES);

        });

        binding.scGuideClips.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ClipsIndicativeCategoryActivity.class));
        });

        // init live section
        checkIsLive();

        binding.scAgricultureNote.setOnClickListener(v -> {
            if (getActivity() == null) return;
            NewMainActivityDesign activity = (NewMainActivityDesign) getActivity();

            Intent intent = new Intent(getContext(), AgricultureNoteActivity.class);
            intent.putExtra(AgricultureNoteActivity.EXTRA_TITLE, "المفكرة الزراعية");
            intent.putExtra(AgricultureNoteActivity.EXTRA_ITEMS, activity.agricultureNoteItems);
            startActivity(intent);
        });

        binding.scWorkers.setOnClickListener(v -> {
            openWorkersService();
        });

        binding.scResearchApp.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemListActivity2.class);
            intent.putExtra("TITLE", "مشاريع الأبحاث التطبيقية");
            intent.putExtra("CAT_ID", Consts.CAT_ID_RESEARCH_APPLICATIONS);
            startActivity(intent);
        });

        binding.scFieldSchool.setOnClickListener(v -> {
            Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage("com.alphadeem.alhoqul");
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else {
                DialogUtil.yesNoDialog(getContext(), "تنبيه", "لم يتم تثبيت التطبيق بعد، هل ترغب بتثبيته؟", msg -> {
                    if (msg.equals(DialogUtil.YES)) {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setData(android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.alphadeem.alhoqul"));
                        startActivity(intent1);
                    }
                });
            }
        });

        binding.scChatBot.setOnClickListener(v -> {
            openConsultantsChat();
        });

        binding.scAiService.setOnClickListener(v -> {
            // open pest identification page
            Intent intent = new Intent(getContext(), PestIdentificationActivity.class);
            startActivity(intent);
        });

        binding.scUdhiyah.setOnClickListener(v -> {
            // open virtual clinic
            Intent intent = new Intent(getContext(), VirtualClinicActivity.class);
            startActivity(intent);
        });

    }

    private void openConsultantsChat() {
        if (getContext() == null) return;
        User user = ProfileHelper.getAccount(getContext());
        if (user != null) {
            if (user.isProfileComplete() && !user.getStatus().isEmpty()) {
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

    private void checkIsLive() {
        binding.scLive.binding.vLiveIcon.setVisibility(View.VISIBLE);
        View lvLive = binding.scLive.binding.vLiveIcon;

        String LIVE_URL_SEARCH_LIST = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UCO7z6GRmfgabhF-FYDIzfng&type=video&eventType=live&key=AIzaSyATOolSm0OfEYLCL5jqQ3dw7c3iY35D-Go";
        RealPathUtil.readJsonFromUrl(LIVE_URL_SEARCH_LIST, jsonObject -> {
            if (getActivity() == null) return;
            NewMainActivityDesign activity = (NewMainActivityDesign) getActivity();

            live_video_url = activity.youtubeVideoUrl;

            if (jsonObject == null) {
                setTenSection();
                return;
            }

            try {
                String video_id = "";
                if (!jsonObject.getJSONArray("items").isNull(0))
                    video_id = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");

                if (video_id.isEmpty()) {
                    lvLive.setBackgroundTintList(requireContext().getColorStateList(R.color.gray));
                } else {
                    lvLive.setBackgroundTintList(requireContext().getColorStateList(R.color.red));
                    live_video_url = BASE_URL_YOUTUBE.concat(video_id);
                }

            } catch (JSONException e) {
                Log.e("Error", "Read youtube json error", e);
            }

            setTenSection();

        });
    }

    private void setTenSection() {


        binding.scLive.setOnClickListener(view -> {
            Log.v("Live Url: ", live_video_url);
            Intent intent = new Intent(getContext(), VideoViewActivity.class);
            intent.putExtra("path", live_video_url);
            intent.putExtra("is_youtube", true);
            startActivity(intent);
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
                binding.layoutHeader.ivUserAvatar.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(this)
                        .load(APIClient.imageUrl + user.getPhotoUrl())
                        .into(binding.layoutHeader.ivUserAvatar);
            }
        }
        if (getActivity() != null) {
            NewMainActivityDesign activity = (NewMainActivityDesign) getActivity();

            binding.layoutHeader.tvTemperature.setText(activity.weatherTemperature);
            binding.layoutHeader.ivWeatherIcon.setImageResource(activity.weatherResIcon);
            binding.layoutHeader.tvWeatherStatus.setText(activity.weatherPhenomenon);
        }
    }

    private void openWorkersService() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.getCurrentWorker(TokenHelper.getBearerToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Response<NewAPIsResponse<Worker>> response) {
                if (response.body() != null && response.body().getStatus()) {
                    if (getContext() != null)
                        WorkerHelper.createOrUpdate(response.body().getData(), getContext());
                }
                startActivity(new Intent(getContext(), AvailableWorkerActivity.class));
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Worker>> call, @NonNull Throwable t) {
                startActivity(new Intent(getContext(), AvailableWorkerActivity.class));
            }
        });
    }
}