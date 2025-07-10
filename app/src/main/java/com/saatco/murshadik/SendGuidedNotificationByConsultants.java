package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.RegionResponse;
import com.saatco.murshadik.databinding.ActivitySendGiudeNotificationByConsultantsBinding;
import com.saatco.murshadik.model.City;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendGuidedNotificationByConsultants extends AppCompatActivity {

    EditText et_guide_text;
    TextView tv_cities, tv_skills, tv_region;

    LinearLayout ll_cities_picker, ll_skills_picker, ll_region_picker, ll_progress_bar;
    Button btn_submit;
    ImageButton btn_guide_notification_history;
    private ArrayList<Item> regions, skills;
    Item region;

    ActivitySendGiudeNotificationByConsultantsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendGiudeNotificationByConsultantsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ToolbarHelper.setToolBarTrans(this, "", binding.appBar.getRoot());

        initViews();
        initRegions();
        initListeners();

        //*********************** api calls *********************** //
        if (regions == null)
            getRegions();
        getSkills();
        assignSkills();


    }


    private void initListeners() {

        ll_region_picker.setOnClickListener(v -> showRegionDialog());

        ll_cities_picker.setOnClickListener(v -> showCitiesDialog());

        ll_skills_picker.setOnClickListener(v -> showSkillsDialog());

        btn_submit.setOnClickListener(v -> {
            if (isFormFill()){
                AlertDialog.Builder builder = new AlertDialog.Builder(SendGuidedNotificationByConsultants.this);

                builder.setTitle(getString(R.string.confirm));
                builder.setMessage(getString(R.string.are_you_sure));

                builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    // Do nothing but close the dialog
                    postGuide();
                    dialog.dismiss();
                });

                builder.setNegativeButton(getString(R.string.no), (dialog, which) -> {

                    // Do nothing
                    dialog.dismiss();
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });

        btn_guide_notification_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendGuidedNotificationByConsultants.this, GuideNotificationHistoryActivity.class));
            }
        });
    }

    void initViews() {
        et_guide_text = binding.etGuideText;
        tv_cities = binding.tvCities;
        tv_skills = binding.tvSkills;
        tv_region = binding.tvRegion;
        ll_cities_picker = binding.llCitiesPicker;
        ll_skills_picker = binding.llSkillsPicker;
        ll_region_picker = binding.llRegionPicker;
        btn_guide_notification_history = binding.btnGuideNotificationHistory;

        btn_submit = binding.appBar.btnSubmit;
        btn_submit.setText(getString(R.string.send));
        ll_progress_bar = binding.llProgressBar.getRoot();
    }

    void initRegions() {
        ArrayList<Item> regionsList;
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(), "regions");
        Type type = new TypeToken<ArrayList<Item>>() {
        }.getType();
        regionsList = gson.fromJson(positions, type);

        if (regionsList != null) {
            regions = regionsList;
        }
    }

    boolean isFormFill(){

        if (region == null){
            Util.showToast(getString(R.string.select_a_region), this);
            return false;
        }
        if (et_guide_text.getText().toString().trim().equals("")){
            Util.showToast(getString(R.string.plaes_enter_text), this);
            return false;
        }
        if (tv_cities.getText().equals("")){
            Util.showToast(getString(R.string.select_cities), this);
            return false;
        }
        if (tv_skills.getText().equals("")){
            Util.showToast(getString(R.string.select_skill), this);
            return false;
        }


        return true;
    }

    private void assignSkills() {
        skills = new ArrayList<>();

        skills.add(new Item("نخيل",null,false));
        skills.add(new Item("زيتون",null,false));
        skills.add(new Item("أشجار الفاكهة",null,false));
        skills.add(new Item("أشجار الزينة",null,false));
        skills.add(new Item("الخضراوات",null,false));
        skills.add(new Item("محاصيل حقلية",null,false));
        skills.add(new Item("البن",null,false));
        skills.add(new Item("ورد طائفي/زهور الزينة",null,false));


        skills.add(new Item("الثروة النباتية/إنتاج نباتي",null,false));
        skills.add(new Item("نخيل",null,false));
        skills.add(new Item("زيتون",null,false));
        skills.add(new Item("أشجار الفاكهة",null,false));
        skills.add(new Item("أشجار الزينة",null,false));
        skills.add(new Item("الخضراوات",null,false));
        skills.add(new Item("محاصيل حقلية",null,false));
        skills.add(new Item("البن",null,false));
        skills.add(new Item("ورد طائفي/زهور الزينة",null,false));
        skills.add(new Item("المناحل/إنتاج العسل",null,false));
        skills.add(new Item("الزراعة العضوية",null,false));
        skills.add(new Item("الزراعة بدون تربة",null,false));
        skills.add(new Item("بيوت محمية",null,false));
        skills.add(new Item("بذور وتقاوي",null,false));
        skills.add(new Item("تسويق زراعي",null,false));
        skills.add(new Item("الثروة الحيواني",null,false));
        skills.add(new Item("الثروة السمكية",null,false));
        skills.add(new Item("الدعم الفني والتقني",null,false));

    }

    private void showRegionDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_regions);

        ListView listView = dialog.findViewById(R.id.statusListView);

        ArrayList<String> regionList = new ArrayList<>();

        for (Item region : regions)
            regionList.add(region.getNameAr());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, regionList);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener((parent, view, position, id) -> {

            // ListView Clicked item index
            String itemValue = (String) listView.getItemAtPosition(position);
            tv_region.setText(itemValue);
            region = regions.get(position);
            tv_cities.setText("");

            dialog.dismiss();

        });


        dialog.show();

    }

    private void showCitiesDialog() {
        if (region == null) {
            ToastUtils.longToast(getString(R.string.select_a_region));
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ArrayList<String> cities_name = new ArrayList<>();
        ArrayList<String> chosen_cities_name = new ArrayList<>();
        // make a list to hold state of every color
        for (City city : region.getCities()) {
            cities_name.add(city.getNameAr());
        }
        String[] n_c = cities_name.toArray(new String[0]);
        boolean[] c_c = new boolean[cities_name.size()];
        for (int i = 0; i < cities_name.size(); i++) {
            c_c[i] = false;
        }


        // Do something here to pass only arraylist on this both arrays ('colors' & 'checkedColors')
        builder.setMultiChoiceItems(n_c, c_c, (dialog, which, isChecked) -> {
            if (isChecked) {
                c_c[which] = true;
                if (!chosen_cities_name.contains(cities_name.get(which))) {
                    chosen_cities_name.add(cities_name.get(which));
                }
            } else {
                c_c[which] = false;
                chosen_cities_name.remove(cities_name.get(which));
            }
        });

        builder.setCancelable(false);

        builder.setTitle(getString(R.string.select_cities));

        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            String temp_city_names = "";
            for (String city_name : chosen_cities_name) {
                temp_city_names = temp_city_names.concat("," + city_name);
            }
            tv_cities.setText(temp_city_names);
        });
        builder.show();
    }


    private void showSkillsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ArrayList<String> skills_name = new ArrayList<>();
        ArrayList<String> chosen_skills_name = new ArrayList<>();
        // make a list to hold state of every color
        for (Item skill : skills) {
            skills_name.add(skill.getNameAr());
        }
        String[] n_c = skills_name.toArray(new String[0]);
        boolean[] c_c = new boolean[skills_name.size()];
        for (int i = 0; i < skills_name.size(); i++) {
            c_c[i] = false;
        }


        // Do something here to pass only arraylist on this both arrays ('colors' & 'checkedColors')
        builder.setMultiChoiceItems(n_c, c_c, (dialog, which, isChecked) -> {
            if (isChecked) {
                c_c[which] = true;
                if (!chosen_skills_name.contains(skills_name.get(which))) {
                    chosen_skills_name.add(skills_name.get(which));
                }
            } else {
                c_c[which] = false;
                chosen_skills_name.remove(skills_name.get(which));
            }
        });

        builder.setCancelable(false);

        builder.setTitle(getString(R.string.select_skills));

        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            String temp_skill_names = "";
            for (String city_name : chosen_skills_name) {
                temp_skill_names = temp_skill_names.concat("," + city_name);
            }
            tv_skills.setText(temp_skill_names);
        });
        builder.show();
    }

    private void getRegions() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RegionResponse> call = apiInterface.getRegionsWithCity();
        call.enqueue(new Callback<RegionResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegionResponse> call, @NonNull Response<RegionResponse> response) {

                try {

                    assert response.body() != null;
                    regions = response.body().getRegionList();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegionResponse> call, @NonNull Throwable t) {

            }
        });


    }

    private void getSkills() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getSkills();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {

                try {

                    if (response.body() != null) {
                        Gson gson = new Gson();
                        String jsonCat = gson.toJson(response.body());
                        PrefUtil.writePreferenceValue(getApplicationContext(), "CATEGORY_CONSULTANT", jsonCat);


                        assignSkills();
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {

            }
        });
    }

    private void postGuide(){
        ll_progress_bar.setVisibility(View.VISIBLE);
        Util.disableInteraction(this);


        final MultipartBody.Part guide_alert = MultipartBody.Part.createFormData("GuideAlert", et_guide_text.getText().toString());
        final MultipartBody.Part region_id = MultipartBody.Part.createFormData("region_id", region.getId() + "");
        final MultipartBody.Part cities = MultipartBody.Part.createFormData("cities", tv_cities.getText().toString());
        final MultipartBody.Part skills = MultipartBody.Part.createFormData("skills", tv_skills.getText().toString());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.postGuideNotification("Bearer " + TokenHelper.getToken(), guide_alert, region_id, cities, skills);
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {

                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        DialogUtil.showInfoDialogAndFinishActivity(SendGuidedNotificationByConsultants.this, 1, getString(R.string.sent_done), getString(R.string.send_guide_notification));
                        Log.v("respone status msg: ", response.body().getMessage());
                    } else {
                        Log.v("respone error: ", response.body().getMessage());
                    }
                }

                ll_progress_bar.setVisibility(View.GONE);
                Util.enableInteraction(SendGuidedNotificationByConsultants.this);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                Util.showErrorToast(getString(R.string.sent_failed), SendGuidedNotificationByConsultants.this);
                Log.e("Error msg: ", t.getMessage());

                ll_progress_bar.setVisibility(View.GONE);
                Util.enableInteraction(SendGuidedNotificationByConsultants.this);
            }
        });
    }
}