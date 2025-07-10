package com.saatco.murshadik.api.request;

import android.util.Log;

import com.saatco.murshadik.App;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallRequest {

    public static final int INITIATED = 1;
    public static final int ACCEPTED = 2;
    public static final int REJECTED = 3;
    public static final int BUSY = 4;
    public static final int NO_ANSWER = 5;
    public static final int CANCELLED = 7;
    public static final int COMPLETED = 6;

   public static void updateCallStatus(int status,int type){

       String CALL_ID = PrefUtil.getStringPref(App.getInstance(),"CALL_ID");

       APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
       Call<BaseResponse> call = apiInterface.updateCallStatus("Bearer "+ TokenHelper.getToken(),PrefUtil.getInteger(App.getInstance(),"USER_ID"),CALL_ID,status,type);
       call.enqueue(new Callback<BaseResponse>() {
           @Override
           public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
               try {

                   if(response.body().isStatus())
                       Log.d("CALL_STATUS","SUCCESS id:" + CALL_ID + "USER_ID :" + PrefUtil.getInteger(App.getInstance(),"USER_ID") + " STATUS:" +status);

               }catch (Exception ignored){}
           }

           @Override
           public void onFailure(Call<BaseResponse> call, Throwable t) {

           }
       });
   }
}
