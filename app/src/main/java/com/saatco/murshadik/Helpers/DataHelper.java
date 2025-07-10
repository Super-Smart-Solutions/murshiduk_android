package com.saatco.murshadik.Helpers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saatco.murshadik.App;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.model.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataHelper {

    public static ArrayList<Item> getRegions() {
        ArrayList<Item> regionsList;
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(), "regions");
        Type type = new TypeToken<ArrayList<Item>>() {
        }.getType();
        regionsList = gson.fromJson(positions, type);

        return regionsList;
    }

    public static String getRegionById(ArrayList<Item> regions, int id) {
        for (Item region : regions) {
            if (region.getId() == id) return region.getNameAr();
        }
        return "";
    }


}
