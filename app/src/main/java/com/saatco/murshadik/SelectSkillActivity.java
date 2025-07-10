package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.SkillAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectSkillActivity extends AppCompatActivity implements SkillAdapter.OnSelectItemClickListener {

    private RecyclerView recyclerView;
    private SkillAdapter adapter;

    private View root;
    private ImageView backBtn;


    ProgressBar progressBar;
    ArrayList<Item> skills = new ArrayList<>();
    ArrayList<Item> filteredSkills = new ArrayList<>();

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_skill);

        root = findViewById(R.id.coordinatorLayout);

        recyclerView = findViewById(R.id.category_recycler_view);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(SelectSkillActivity.this);
        progressDialog.setMessage("الرجاء الانتظار...");
        progressDialog.setCancelable(false);

        SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filteredSkills.clear();
                for (Item skill : skills) {
                    if (skill.getNameAr().toLowerCase().contains(query.toLowerCase())) {
                        filteredSkills.add(skill);
                    }
                }
                adapter.updateList(filteredSkills);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredSkills.clear();
                for (Item skill : skills) {
                    if (skill.getNameAr().toLowerCase().contains(newText.toLowerCase())) {
                        filteredSkills.add(skill);
                    }
                }
                adapter.updateList(filteredSkills);
                return false;
            }
        });

        makeToolbar("");

        getSkills();
    }

    public void makeToolbar(String title){

        backBtn = findViewById(R.id.btn_back);

        if(LanguageUtil.getLanguage(getApplicationContext()).equals("ar"))
            backBtn.setImageResource(R.drawable.arrow_right);

        backBtn.setOnClickListener(view -> finish());
    }

    @Override
    public void onSkillSelect(View view, int position, Item skill) {

        if(skill.isApproved())
            Util.showToast("لا يمكنك تغيير التخصص بمجرد الموافقة عليها من قبل المسؤول",SelectSkillActivity.this);
        else {
            if (getSelectedSize() < 3) {
                if (!skill.isApproved() && !skill.isSelected()) {
                    addSkill(skill.getId());
                }
            } else
                Util.showErrorToast("لا يمكنك اختيار أكثر من ٣ تخصصات", SelectSkillActivity.this);
        }


    }

    private ArrayList<Item> getAllSkillsSub(ArrayList<Item> items){
        ArrayList<Item> allSkills = new ArrayList<>();
        for (Item item : items) {
            if (item.hasChildren()) {
                allSkills.addAll(getAllSkillsSub(item.getChildrens()));
            } else {
                allSkills.add(item);
            }
        }
        return allSkills;
    }


    private int getSelectedSize(){
        int count = 0;

        for(Item skill : skills){
            if(skill.isSelected())
                count++;
        }
        return count;
    }

    private void addSkill(int id){

         progressDialog.show();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.addUserSkill("Bearer "+ TokenHelper.getToken(),id);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                try{

                    getUserSkills();

                }catch (Exception ignored){}
            }


            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    private void getUserSkills(){


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getUserSkills("Bearer "+ TokenHelper.getToken());
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {

                if(progressDialog.isShowing())
                  progressDialog.dismiss();

                try{

                    if(response.body() != null){
                       ArrayList<Item> userSkills = (ArrayList<Item>) response.body();

                        for(Item skill : skills){
                            for(Item userSkill : userSkills){
                                if(userSkill.getSkillId() == skill.getId()){
                                    skill.setApproved(userSkill.isApproved());
                                    skill.setSelected(true);
                                }
                            }
                        }
                        adapter.updateList(skills);
                       // setDetails();

                    }

                }catch (Exception ex){}
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {

            }
        });
    }

    private void getSkills(){

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getSkills();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {

                try{

                    if(response.body() != null)
                    {
                        ArrayList<Item> skillList = (ArrayList<Item>) response.body();
                        skills = getAllSkillsSub(skillList);


                        setDetails();
                        getUserSkills();
                    }

                }catch (Exception ex){}
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {

            }
        });

    }

    private void setDetails(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SkillAdapter(skills,getApplicationContext(),this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }
}