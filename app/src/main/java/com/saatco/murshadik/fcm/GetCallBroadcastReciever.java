package com.saatco.murshadik.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.SharedPrefsHelper;

public class GetCallBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "RECEIVED!!!!!!!!!",Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(context, CallActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra(Consts.EXTRA_IS_INCOMING_CALL, true);
        SharedPrefsHelper.getInstance().save(Consts.EXTRA_IS_INCOMING_CALL, true);
        context.startActivity(intent1);


    }

}
