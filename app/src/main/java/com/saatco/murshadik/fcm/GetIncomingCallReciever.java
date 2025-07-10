package com.saatco.murshadik.fcm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.SharedPrefsHelper;

public class GetIncomingCallReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getExtras() != null) {

            String action = intent.getStringExtra("RC");

            if (action != null) {
                performClickAction(context, action);
            }

        }

    }

    private void performClickAction(Context context, String action) {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Consts.CALL_NOTIFY_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(context.getString(R.string.call_channel));
        }

        if (action.equals("rc")) {


            PrefUtil.writeBooleanValue(context, "is_app_open", true);
            SharedPrefsHelper.getInstance().save(Consts.EXTRA_IS_INCOMING_CALL, true);

            // added by amin to start call activity with call service and comment code above
            CallActivity.start(context, true);


        } else if (action.equals("ac")) {

            CallService.stop(context);

            notificationManager.cancel(Consts.CALL_NOTIFY_ID);

        }
    }

}
