package com.saatco.murshadik.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.saatco.murshadik.utils.SharedPrefsHelper;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           context.startForegroundService(new Intent(context, CallListenerService.class));

        } else {
            context.startService(new Intent(context, CallListenerService.class));

        }
    }
}
