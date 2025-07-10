package com.saatco.murshadik.api;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.RegionResponse;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.MyCallbackHandler;
import com.saatco.murshadik.utils.SharedPrefsHelper;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created this class to add multi use APIs methods
 * every method that get called more than once and can be handled should be added here
 */
public class ApiMethodsHelper {


    public static void updateOnlineStatus(boolean isOnline) {
        updateOnlineStatus(isOnline, msg -> {
        });
    }

    public static void updateOnlineStatus(boolean isOnline, MyCallbackHandler<String> callbackHandler) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.updateOnlineStatus("Bearer " + TokenHelper.getToken(), isOnline);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                try {
                    callbackHandler.onResponse(response.message());
                    assert response.body() != null;
                    Log.v("isOnline", response.body().getMessage());
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static void updateUserOnServer(User user) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();

        if (sharedPrefsHelper.hasQbUser()) {

            StringifyArrayList<String> tags = new StringifyArrayList<>();
            tags.add(user.isFarmer() ? "Farmer" : "Consultant");

            HashMap<Object, Object> map = new HashMap<>();
            map.put("is_online", user.isOnline());
            map.put("id", user.getId());
            map.put("avatar", user.getPhotoUrl());
            map.put("phone", user.getPhoneNumber());
            map.put("role_id", user.getRoleId());


            if (!user.getName().equals("")) {
                QBUser qbUser = sharedPrefsHelper.getQbUser();
                qbUser.setFullName(user.getFirstName() + " " + user.getLastName());
                qbUser.setPassword(null);
                qbUser.setCustomData(new JSONObject(map).toString());
                qbUser.setPhone(user.getPhoneNumber());
                qbUser.setTags(tags);
                QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                        sharedPrefsHelper.saveQbUser(qbUser);
                        Log.v("QBUser", "Updated ::: " + qbUser);
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }
        }
    }

    /**
     * return regions array list by callback function
     *
     * @param callbackHandler: will response with regions list or null
     */
    public static void getRegions(MyCallbackHandler<ArrayList<Item>> callbackHandler) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RegionResponse> call = apiInterface.getRegionsWithCity();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RegionResponse> call, @NonNull Response<RegionResponse> response) {

                try {

                    assert response.body() != null;
                    callbackHandler.onResponse(response.body().getRegionList());

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegionResponse> call, @NonNull Throwable t) {
                callbackHandler.onResponse(null);
            }
        });


    }

    public static void getSkills(MyCallbackHandler<List<Item>> callbackHandler) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getSkills();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {

                try {

                    if (response.body() != null) {
                        callbackHandler.onResponse(response.body());
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {

            }
        });
    }


    public static void putCallDurationApi(int appointmentId, int duration) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.putAppointmentCallDone(TokenHelper.getBearerToken(), appointmentId, duration);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call,
                                   @NonNull Response<NewAPIsResponse<String>> response) {


            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call,
                                  @NonNull Throwable t) {

            }
        });
    }

    public static void putConsultationCallDurationApi(int appointmentId, int duration) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.cAPutAppointmentCallDone(TokenHelper.getBearerToken(), appointmentId, duration);
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {

            }
        });

    }
}
