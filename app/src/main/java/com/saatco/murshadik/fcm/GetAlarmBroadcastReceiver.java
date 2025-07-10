package com.saatco.murshadik.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.InitialActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.ApiMethodsHelper;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.MyCallbackHandler;

public class GetAlarmBroadcastReceiver extends BroadcastReceiver {


    private static final String CHANNEL_ID = "is_online_notification_channel";
    private static final String CHANNEL_NAME = "is_online_notifications";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getStringExtra(Consts.EXTRA_CLINIC_NOTIFICATION) != null){
            String title = context.getString(R.string.virtual_clinic);
            String message = context.getString(R.string.clinic_reminder_msg);
            dynamicNotification(title, message, Consts.CLINIC_CHANNEL_NOTIFICATION_ID, Consts.CLINIC_NOTIFY_ID, context);
        }
        else if (intent.getStringExtra(Consts.SEND_IS_ONLINE_BTN_NOTIFY) != null &&
                intent.getStringExtra(Consts.SEND_IS_ONLINE_BTN_NOTIFY).equals(Consts.NOTIFY)) {
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String channelID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                    createNotificationChannel(context, CHANNEL_ID, context.getString(R.string.is_online_btn_notification_channel_name))
                    : context.getString(R.string.app_name);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.ag_logo)
                    //example for large icon
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.is_online_btn_msg))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.is_online_btn_msg)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);
            Intent i = new Intent(context, InitialActivity.class);
            i.putExtra(Consts.IS_START_FROM_ALARM_IS_ONLINE, true);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            i,
                            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                    );
            // example for blinking LED
            builder.setContentIntent(pendingIntent);
            manager.notify(Consts.IS_ONLINE_NOTIFY_ID, builder.build());

            ApiMethodsHelper.updateOnlineStatus(false, msg -> {
                User user = ProfileHelper.getAccount(context);
                if (user != null) {
                    user.setOnline(false);
                    ProfileHelper.createOrUpdateAccount(user, context);
                    ApiMethodsHelper.updateUserOnServer(user);
                }
            });
        }



    }

    private void dynamicNotification(String title, String message, String channelId, int notifyId, Context context) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                //example for large icon
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);
        Intent i = new Intent(context, InitialActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        i,
                        PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                );
        // example for blinking LED
        builder.setContentIntent(pendingIntent);
        manager.notify(notifyId, builder.build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(Context context, String channelID, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLightColor(context.getColor(R.color.greenColor));
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setDescription("Call Notifications");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
        return channelID;
    }


}
