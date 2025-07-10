package com.saatco.murshadik.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.saatco.murshadik.BaseActivity;
import com.saatco.murshadik.ChatActivityNew;
import com.saatco.murshadik.Helpers.ChatHelper;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.RatingActivity;
import com.saatco.murshadik.utils.SettingsUtil;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.utils.WebRtcSessionManager;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//import static com.mesibo.api.Mesibo.CALLSTATUS_COMPLETE;

public class CallListenerService extends Service implements QBRTCClientSessionCallbacks {
    //implements MesiboCall.MesiboCallListener , Mesibo.CallListener {

    public Context context = this;

    private QBRTCClient rtcClient;
    private QBUser currentUser;

    private Timer myTimer;
    QBIncomingMessagesManager incomingMessagesManager;


    public CallListenerService(){

    }

    public CallListenerService(Context applicationContext) {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel_back_call",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setAutoCancel(false)
                    .setContentText("").build();


            startForeground(1, notification);
        }

        currentUser = SharedPrefsHelper.getInstance().getQbUser();

        if (!QBChatService.getInstance().isLoggedIn()) {
            if(currentUser != null) {
                loginToChat(currentUser);
            }
        }else{
            startActionsOnSuccessLogin();
        }


    }



    private void loginToChat(QBUser qbUser) {
        QBChatService.getInstance().login(qbUser, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Log.v("cService", "ChatService Login Successful");
                startActionsOnSuccessLogin();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.v("cService", "ChatService Login Error: " + e.getMessage());
                startActionsOnSuccessLogin();
            }
        });
    }


    @Override
    public void onStart(Intent intent, int startid) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(getApplicationContext(),"SERVICE DESTROY",Toast.LENGTH_SHORT).show();

        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);

    }

    private void initQBRTCClient() {
        rtcClient = QBRTCClient.getInstance(getApplicationContext());
        // Add signalling manager
        QBChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    rtcClient.addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });

        // Configure
        QBRTCConfig.setDebugEnabled(true);
        SettingsUtil.configRTCTimers(CallListenerService.this);

        // Add service as callback to RTCClient
        rtcClient.addSessionCallbacksListener(WebRtcSessionManager.getInstance(this));
        rtcClient.prepareToProcessCalls();
    }

    private void startActionsOnSuccessLogin() {
        initQBRTCClient();
        Log.v("cService", "ChatService startActionsOnSuccessLogin");

        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                initCall();
            }

        }, 0, 2000);
    }

    private void initCall(){
        QBRTCClient.getInstance(this).addSessionCallbacksListener(this);
    }


    private void sendMissedCallNotification(){

    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {

    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {


    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
    }

    @Override
    public void onChangeReconnectionState(QBRTCSession qbrtcSession, Integer integer, QBRTCTypes.QBRTCReconnectionState qbrtcReconnectionState) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {

    }

}

