package com.saatco.murshadik.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.quickblox.chat.QBChatService;
import com.quickblox.users.model.QBUser;
import com.saatco.murshadik.App;
import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.utils.SharedPrefsHelper;

public class IncomingCallBroadCastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            // TELEPHONY MANAGER class object to register one listner
            TelephonyManager tmgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            //Create Listner
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();

            // Register listener for LISTEN_CALL_STATE
            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);


        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }


    private class MyPhoneStateListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {

            // state = 1 means when phone is ringing
            if (state == 1) {

                if (SharedPrefsHelper.getInstance().hasQbUser()) {
                    QBUser qbUser2 = SharedPrefsHelper.getInstance().getQbUser();

                    if (QBChatService.getInstance().isLoggedIn()) {
                        LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(new Intent("incoming_call"));
                        CallService.stop(App.getInstance());
                    }

                }
            }
        }
    }
}

