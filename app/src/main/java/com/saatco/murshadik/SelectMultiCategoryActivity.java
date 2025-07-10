package com.saatco.murshadik;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saatco.murshadik.databinding.ActivitySelectMultiCategoryBinding;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.utils.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SelectMultiCategoryActivity extends BaseActivity implements CityAdapter.OnSelectItemClickListener {


    public static final int RESULT_SELECT_CITY_OK = 901;
    ArrayList<Item> items = new ArrayList<>();
    String selectedStr = "";

    ArrayList<Item> selects = new ArrayList<>();

    boolean isFarmer = false;
    boolean isRegister = false;
    String skills;

    CityAdapter adapter;
    ActivitySelectMultiCategoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectMultiCategoryBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        isFarmer = getIntent().getBooleanExtra("IS_FARMER", false);
        isRegister = getIntent().getBooleanExtra("IS_REGISTER", false);
        skills = getIntent().getStringExtra("SKILLS");

        ArrayList<Item> selectedItems = (ArrayList<Item>) getIntent().getSerializableExtra("SELECTEDS");


        makeToolbar(getString(R.string.select_skills));

        Gson gson = new Gson();
        String json = PrefUtil.getStringPref(getApplicationContext(), "CATEGORY_CONSULTANT");
        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        ArrayList<Item> catList = gson.fromJson(json, type);


        setSkills(catList, selectedItems);

        binding.categoryRecyclerView.setHasFixedSize(true);
        binding.categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CityAdapter(items, getApplicationContext(), this);
        binding.categoryRecyclerView.setAdapter(adapter);
        binding.categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        binding.appBar.btnSubmit.setText(getString(R.string.save));
        binding.appBar.btnSubmit.setOnClickListener(view -> {

            String ids = "";

            for (Item item : selects) {
                selectedStr = selectedStr.concat(",").concat(item.getNameAr());
                ids = ids.concat(String.valueOf(item.getId())).concat(",");
            }

            Log.v("SELECTED", selectedStr);

            if (!selectedStr.isEmpty()) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("city", selectedStr);
                bundle.putString("ids", ids);
                bundle.putSerializable("SELECTEDS", selects);
                intent.putExtras(bundle);
                setResult(RESULT_SELECT_CITY_OK, intent);
                finish();
            }
        });

    }

    private void setSkills(ArrayList<Item> catList, ArrayList<Item> selectedItems) {
        if (isFarmer) {
            ArrayList<Item> children1 = new ArrayList<>();
            children1.add(new Item("نخيل", null, isContainsSkill("نخيل")));
            children1.add(new Item("زيتون", null, isContainsSkill("زيتون")));
            children1.add(new Item("أشجار الفاكهة", null, isContainsSkill("أشجار الفاكهة")));
            children1.add(new Item("أشجار الزينة", null, isContainsSkill("أشجار الزينة")));
            children1.add(new Item("الخضراوات", null, isContainsSkill("الخضراوات")));
            children1.add(new Item("محاصيل حقلية", null, isContainsSkill("محاصيل حقلية")));
            children1.add(new Item("البن", null, isContainsSkill("البن")));
            children1.add(new Item("ورد طائفي/زهور الزينة", null, isContainsSkill("ورد طائفي/زهور الزينة")));


            items.add(new Item("الثروة النباتية/إنتاج نباتي", children1, isContainsSkill("الثروة النباتية/إنتاج نباتي")));
            items.add(new Item("نخيل", null, isContainsSkill("نخيل")));
            items.add(new Item("زيتون", null, isContainsSkill("زيتون")));
            items.add(new Item("أشجار الفاكهة", null, isContainsSkill("أشجار الفاكهة")));
            items.add(new Item("أشجار الزينة", null, isContainsSkill("أشجار الزينة")));
            items.add(new Item("الخضراوات", null, isContainsSkill("الخضراوات")));
            items.add(new Item("محاصيل حقلية", null, isContainsSkill("محاصيل حقلية")));
            items.add(new Item("البن", null, isContainsSkill("البن")));
            items.add(new Item("ورد طائفي/زهور الزينة", null, isContainsSkill("ورد طائفي/زهور الزينة")));
            items.add(new Item("المناحل/إنتاج العسل", null, isContainsSkill("المناحل/إنتاج العسل")));
            items.add(new Item("الزراعة العضوية", null, isContainsSkill("الزراعة العضوية")));
            items.add(new Item("الزراعة بدون تربة", null, isContainsSkill("الزراعة بدون تربة")));
            items.add(new Item("بيوت محمية", null, isContainsSkill("بيوت محمية")));
            items.add(new Item("بذور وتقاوي", null, isContainsSkill("بذور وتقاوي")));
            items.add(new Item("تسويق زراعي", null, isContainsSkill("تسويق زراعي")));
            items.add(new Item("الثروة الحيواني", null, isContainsSkill("الثروة الحيواني")));
            items.add(new Item("الثروة السمكية", null, isContainsSkill("الثروة السمكية")));

            if (skills != null) {
                String[] skillList = skills.split(",");
                for (Item item : items) {

                    for (String skill : skillList) {
                        Log.d("CYBER", skill);
                        if (item.getNameAr().trim().equals(skill.trim())) {
                            item.setSelected(true);
                            selects.add(item);
                        }

                    }
                }
            }

        } else {


            if (catList != null) {
               items = getAllSkillsSub(catList);
            }

            if (selectedItems != null) {
                if (!selectedItems.isEmpty()) {
                    for (Item item : items) {
                        for (Item selected : selectedItems) {
                            if (item.getId() == selected.getId()) {
                                item.setSelected(true);
                                selects.add(item);
                            }
                        }
                    }
                }
            }
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

    private boolean isContainsSkill(String find) {
        if (skills == null)
            return false;
        return skills.contains(find);
    }

    public void makeToolbar(String title) {
        if (LanguageUtil.getLanguage(getApplicationContext()).equals("ar"))
            binding.appBar.btnBack.setImageResource(R.drawable.arrow_right);

        binding.appBar.btnBack.setOnClickListener(view -> finish());
        binding.appBar.toolbarTitle.setText(title);
    }

    @Override
    public void onCitySelect(View view, int position, Item city) {


        if (isFarmer) {
            if (city.isSelected()) {
                city.setSelected(false);

                selects.removeIf(item -> item.getNameAr().equals(city.getNameAr()));

            } else {
                city.setSelected(true);
                selects.add(city);
            }
        } else {

            if (city.isSelected()) {
                city.setSelected(false);

                selects.removeIf(item -> item.getId() == city.getId());

            } else {
                if (isRegister) {
                    if (selects.size() < 3) {
                        city.setSelected(true);
                        selects.add(city);
                    } else {
                        Util.showToast("لا يمكنك اختيار أكثر من ٣ تخصصات", SelectMultiCategoryActivity.this);
                    }
                } else {
                    city.setSelected(true);
                    selects.add(city);
                }
            }

        }

        adapter.notifyItemChanged(position);
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
