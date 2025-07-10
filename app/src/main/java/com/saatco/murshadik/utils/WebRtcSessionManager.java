package com.saatco.murshadik.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacksImpl;
import com.saatco.murshadik.App;
import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.fcm.GetIncomingCallReciever;

import java.util.List;


public class WebRtcSessionManager extends QBRTCClientSessionCallbacksImpl {
    private static final String TAG = WebRtcSessionManager.class.getSimpleName();
    public static  final String CALL_CHANNEL_ID = "default_ringtone_channel";
    public static  final String CALL_CHANNEL_ID_SILENCE = "silence_ringtone_channel";
    private static WebRtcSessionManager instance;
    private final Context context;

    private static QBRTCSession currentSession;

    private WebRtcSessionManager(Context context) {
        this.context = context;
    }

    public static WebRtcSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new WebRtcSessionManager(context);
        }

        return instance;
    }

    public QBRTCSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(QBRTCSession qbCurrentSession) {
        currentSession = qbCurrentSession;
    }

    @Override
    public void onReceiveNewSession(QBRTCSession session) {
        Log.d(TAG, "onReceiveNewSession to WebRtcSessionManager");
        Log.v("ELON", "onReceiveNewSession to WebRtcSessionManager");

        if (session != null) {

            setCurrentSession(session);

            if (Util.isAppOnForeground(context))
                CallActivity.start(context, true);
            else
                startActivity();



        }
    }


    @Override
    public void onSessionClosed(QBRTCSession session) {
        Log.d(TAG, "onSessionClosed WebRtcSessionManager");

        if (session.equals(getCurrentSession())) {
            setCurrentSession(null);
        }
    }

    private void startActivity() {

        //------------- edited by Amin-----------
        // the edition is changing the version form Q = 29 to N = 24 so the condition will work on older version of android

        Intent receiveCallAction = new Intent(context, CallActivity.class);
        receiveCallAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        receiveCallAction.putExtra(Consts.EXTRA_IS_INCOMING_CALL, true);
        PrefUtil.writeBooleanValue(context, "is_app_open", true);
        SharedPrefsHelper.getInstance().save(Consts.EXTRA_IS_INCOMING_CALL, true);

        Intent cancelCallAction = new Intent(context, GetIncomingCallReciever.class);
        cancelCallAction.putExtra("RC", "ac");
        cancelCallAction.setAction("CANCEL_CALL");

        PendingIntent receiveCallPendingIntent = PendingIntent.getActivity(context, 1200, receiveCallAction, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Calling");
        bigTextStyle.bigText("android calling");


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CALL_CHANNEL_ID);
        builder.setContentTitle("مرشدك الزراعي");
        builder.setContentText("يتصل بك" + " " + PrefUtil.getStringPref(context, "CALLER_NAME"));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.addAction(R.drawable.call_accept, "عرض المكالمة", receiveCallPendingIntent);
        builder.setCategory(NotificationCompat.CATEGORY_CALL);
        if (Util.isPhoneNormal(App.getInstance()))
            builder.setChannelId(CALL_CHANNEL_ID);
        else builder.setChannelId(CALL_CHANNEL_ID_SILENCE);
//        builder.setFullScreenIntent(receiveCallPendingIntent, true);
        builder.setContentIntent(receiveCallPendingIntent);
        builder.setOngoing(true);

        builder.setPriority(NotificationManager.IMPORTANCE_MAX);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify(Consts.CALL_NOTIFY_ID, builder.build());
        }
    }

    public static void createNotifyChannel(Context context, String channelId, String channelName) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel;
        channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setDescription(channelName);
        channel.setShowBadge(false);
        if (!channelId.equals(CALL_CHANNEL_ID_SILENCE)) {
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_VOICE_CALL)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build());
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        }
        if (manager != null) {
            manager.deleteNotificationChannel("Call Notifications"); // added in app version 2.1.0
            manager.createNotificationChannel(channel);
        }
    }


}