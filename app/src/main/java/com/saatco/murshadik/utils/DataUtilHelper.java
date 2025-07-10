package com.saatco.murshadik.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saatco.murshadik.App;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.model.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataUtilHelper {

    public static ArrayList<Item> getRegions() {
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(), "regions");
        Type type = new TypeToken<ArrayList<Item>>() {
        }.getType();
        return gson.fromJson(positions, type);

    }

    public static Item getRegionById(int id){
        for (Item item : getRegions()){
            if (item.getId() == id)
                return item;
        }
        return new Item();
    }

    public static ArrayList<Item> getSkillsFromLocalStorage(Context context){
        Gson gson = new Gson();
        String json = PrefUtil.getStringPref(context,"CATEGORY_CONSULTANT");
        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
