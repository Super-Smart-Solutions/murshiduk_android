package com.saatco.murshadik;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.SubscribePushStrategy;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.saatco.murshadik.Helpers.ChatHelper;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.QBResRequestExecutor;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.Util;

import java.util.Map;


public class BaseActivity extends AppCompatActivity implements ServiceConnection {


    protected QBResRequestExecutor requestExecutor;

    protected SharedPrefsHelper sharedPrefsHelper;

    QBIncomingMessagesManager incomingMessagesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageUtil.changeLanguage(this);


        QBSettings.getInstance().setEnablePushNotification(true);
        QBSettings.getInstance().setSubscribePushStrategy(SubscribePushStrategy.ALWAYS);

        if (requestExecutor == null)
            requestExecutor = new QBResRequestExecutor();

        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        new Thread(this::listenToIncomingMessages).start();


        //------------------ created by Amin ---------------
        createNotificationChannel();

        initQBCallReceive();
    }

    private void listenToIncomingMessages() {
        incomingMessagesManager = ChatHelper.getInstance().getQbChatService().getIncomingMessagesManager();

        if (incomingMessagesManager != null && listener != null)
            incomingMessagesManager.addDialogMessageListener(listener);
        else {

            if (SharedPrefsHelper.getInstance().getQbUser() != null) {
                ChatHelper.getInstance().getQbChatService().login(SharedPrefsHelper.getInstance().getQbUser(), new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                        incomingMessagesManager = ChatHelper.getInstance().getQbChatService().getIncomingMessagesManager();
                        if (incomingMessagesManager != null && listener != null)
                            incomingMessagesManager.addDialogMessageListener(listener);

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }
        }
    }

    private void initQBCallReceive(){
        QBRTCClient.getInstance(this).addSessionCallbacksListener(new QBRTCClientSessionCallbacks() {
            @Override
            public void onReceiveNewSession(QBRTCSession session) {
                Log.d(" #5555 ", "onReceiveNewSession: start");
            }

            @Override
            public void onUserNoActions(QBRTCSession session, Integer userId) {

            }

            @Override
            public void onSessionStartClose(QBRTCSession session) {

            }

            @Override
            public void onUserNotAnswer(QBRTCSession session, Integer userId) {

            }

            @Override
            public void onCallRejectByUser(QBRTCSession session, Integer userID, Map<String, String> userInfo) {

            }

            @Override
            public void onCallAcceptByUser(QBRTCSession session, Integer userID, Map<String, String> userInfo) {

            }

            @Override
            public void onReceiveHangUpFromUser(QBRTCSession session, Integer userID, Map<String, String> userInfo) {

            }

            @Override
            public void onChangeReconnectionState(QBRTCSession qbrtcSession, Integer integer, QBRTCTypes.QBRTCReconnectionState qbrtcReconnectionState) {

            }

            @Override
            public void onSessionClosed(QBRTCSession session) {

            }
        });
    }

    //******************* show live chat alerts when get new message ***********************//
    private final QBChatDialogMessageListener listener = new QBChatDialogMessageListener() {
        @Override
        public void processMessage(String dialogID, QBChatMessage qbChatMessage, Integer senderID) {
            if (!PrefUtil.getBoolean(getApplicationContext(), "is_chat_open")) {
                Util.showChatAlert(qbChatMessage.getBody(), BaseActivity.this);

            }
//            Util.showNotify("طلب استشارة", qbChatMessage.getBody(), BaseActivity.this, ChatActivityNew.class, createNotificationChannel());


        }

        @Override
        public void processError(String dialogID, QBChatException e, QBChatMessage qbChatMessage, Integer senderID) {

        }
    };

    private void createNotificationChannel() {
        String CHANNEL_ID = Consts.MSG_CHANNEL;
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String  name = getString(R.string.msg_channel_name);
            String description = "received msg notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }



    public interface NewMessageReceived {
        default void newMessage(String msg){}
    }
}
