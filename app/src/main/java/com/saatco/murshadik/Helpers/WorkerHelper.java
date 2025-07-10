package com.saatco.murshadik.Helpers;

import android.content.Context;

import com.google.gson.Gson;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.model.workersService.Worker;

import org.json.JSONObject;

public class WorkerHelper {

    public static final String PREFS_KEY_WORKER = "worker";
    public static final String PREFS_KEY_IS_REGISTERED = "isRegistered";


    public static void createOrUpdate(Worker worker, Context context){
        Gson gson = new Gson();
        String json = gson.toJson(worker);
        PrefUtil.writePreferenceValue(context,PREFS_KEY_WORKER,json);
        PrefUtil.writeBooleanValue(context,PREFS_KEY_IS_REGISTERED,true);
    }

    public static Worker getCurrentWorker(Context context) {
        Worker worker = null;
        Gson gson = new Gson();
        String json = PrefUtil.getStringPref(context,PREFS_KEY_WORKER);

        JSONObject j;

        try {
            j = new JSONObject(json);
            worker = gson.fromJson(j.toString(), Worker.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return worker;
    }

}
