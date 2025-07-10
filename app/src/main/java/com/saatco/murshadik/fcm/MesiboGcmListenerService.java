package com.saatco.murshadik.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import androidx.core.app.Person;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.saatco.murshadik.ChatActivityNew;
import com.saatco.murshadik.ChatUserActivity;
import com.saatco.murshadik.NewMainActivityDesign;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.SettingsUtil;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.utils.WebRtcSessionManager;


import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MesiboGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "FcmListenerService";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String NOTIFICATION_CHAT_CHANNEL_ID = "10002";

    // added by Amin to make call notification run
    private static final String CHANNEL_ID = "Quickblox channel";
    private static final String CHANNEL_NAME = "Quickblox background service";

    int type = 0;
    int roleId = 0;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {


            Map<String, String> data = remoteMessage.getData();
//            String senderId = data.get("user_id");
            String badge = data.get("badge");
            int msg_badge_int;
            // **** declare string variable for the message that will appear in the notification
            String msg_text, sender_name;

            //----------------makeing some test on data we got --------------
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                // do something with key and/or tab
                Log.i("key", key);
                Log.i("val", val);
                Log.i("-----", "---------");
            }
            //********************** send notifications general ***************************//
            // prev condition was if(data.get(Consts.MESSAGE_TYPE) != null) but there is no key will
            // be with this name so I make it depend on key name 'message'
            // edited by amin
            if (Objects.equals(data.get(Consts.PUSHES_PUSH_TYPE), Consts.PUSHES_PUSH_TYPE_MESSAGE)) {
                //adding 1 to Shortcut badger preference
                PrefUtil.writeIntValue(getApplicationContext(), Consts.SHORTCUT_BADGER_COUNTER_CHAT_MSG, PrefUtil.getInteger(getApplicationContext(), Consts.SHORTCUT_BADGER_COUNTER_CHAT_MSG) + 1);


                if (data.get(Consts.MESSAGE_LINK_TYPE) != null) {
                    sendChatNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), data.get(Consts.MESSAGE_LINK_TYPE));
                } else if (!Util.isAppOnForeground(this)) {
                    sender_name = data.get("sender_name");
                    msg_text = data.get("body");

                    sendNotification("طلب استشارة", msg_text, sender_name, ChatUserActivity.class, data.get(Consts.PUSHES_CHAT_ID));
                }
                // **** getting integer of badge. badge is the number of unread messages
                int shortcut_badger_counter = PrefUtil.getInteger(getApplicationContext(), Consts.SHORTCUT_BADGER_COUNTER) +
                        PrefUtil.getInteger(getApplicationContext(), Consts.SHORTCUT_BADGER_COUNTER_CHAT_MSG);
                if (shortcut_badger_counter > 0)
                    ShortcutBadger.applyCount(getApplicationContext(), shortcut_badger_counter);
            }

            if (data.get("type") != null)
                type = Integer.parseInt(Objects.requireNonNull(data.get("type")));

            if (data.get("role_id") != null)
                roleId = Integer.parseInt(Objects.requireNonNull(data.get("role_id")));

            //********************** create notification ***************************//

            if (data.get("VOIPCall") != null) {


                //-------------------------------  -------------
//                startService(new Intent(getApplicationContext(), CallListenerService.class));

                int isVOIPCAll = Integer.parseInt(Objects.requireNonNull(data.get("VOIPCall")));

                if (isVOIPCAll != 99) {


                    SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();

                    PrefUtil.writePreferenceValue(this, "CALLER_NAME", data.get("callerID"));
                    if (sharedPrefsHelper.hasQbUser()) {
                        QBUser qbUser2 = sharedPrefsHelper.getQbUser();

                        if (QBChatService.getInstance().isLoggedIn())
                            initQBRTCClient();
                        else {
                            LoginService.start(this, qbUser2);
                            initQBRTCClient();
                        }
                    }


                    PrefUtil.writePreferenceValue(this, "CALLER_NAME", (String) data.get("callerID"));


                }
            }

        }


    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.v("Token", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    /**
     * get active notification list from NotificationManager.
     */
    public Notification getActiveNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] barNotifications = notificationManager.getActiveNotifications();
        for (StatusBarNotification notification : barNotifications) {
            if (notification.getId() == notificationId) {
                return notification.getNotification();
            }
        }
        return null;
    }

    /**
     * I have modify and add extra parameter for the activity that will open when user tap the notification
     *
     * @param cls can be null;
     *            edited by amin
     */
    private void sendNotification(String title, String messageBody, String sender_name, Class<?> cls, String chat_id) {
        if (cls == null) cls = NewMainActivityDesign.class;


        Intent resultIntent = new Intent(this, cls);

        if (chat_id != null) {
            if (chat_id.matches("[0-9]+")){
                cls = ChatActivityNew.class;// open chat activity for user if chat id was exist
                resultIntent = new Intent(this, cls);
                User user = new User();
                user.setChatId(chat_id);
                resultIntent.putExtra("is_noti", true);
                resultIntent.putExtra("USER", user);
            }
        }

        resultIntent.putExtra(Consts.IS_START_FROM_NOTIFY, true);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 101, resultIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.MessagingStyle.Message message;
        Person.Builder p = new Person.Builder();
        p.setName(sender_name);
        Person person = p.build();
        message = new NotificationCompat.MessagingStyle.Message(messageBody, Calendar.getInstance().getTime().getTime(), person);
        //        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(title);

        Notification old_notification = getActiveNotification(10/*this is the id used in notify manager */);
        if (old_notification != null)
            style = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(old_notification);

        if (style != null) {
            style.addMessage(message);
        } else {
            style = new NotificationCompat.MessagingStyle(title)
                    .addMessage(message);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ag_logo)
                .setStyle(style)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();


        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
        notificationChannel.setShowBadge(false);
        //  assert notificationManager != null;
        notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        notificationManager.createNotificationChannel(notificationChannel);

        //notificationManager.notify(5 /* ID of notification */, notificationBuilder.build());
        notificationManager.notify(10, notificationBuilder.build());

        Intent intent = new Intent("alert_msg");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String createNotificationChannel2(String channelID, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
        channel.setLightColor(getColor(R.color.greenColor));
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setDescription("Call Notifications");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
        return channelID;
    }


    private void sendChatNotification(String messageBody, String title, String chatId) {

        User user = new User();
        user.setChatId(chatId);

        Intent resultIntent = new Intent(this, ChatActivityNew.class);
        resultIntent.putExtra("is_noti", true);
        resultIntent.putExtra("USER", user);
        resultIntent.putExtra("is_cb", true);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 101, resultIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ag_logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .setUsage(AudioAttributes.USAGE_ALARM)
//                .build();


        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHAT_CHANNEL_ID, "NOTIFICATION_CHAT", importance);
        notificationChannel.setShowBadge(true);
        //  assert notificationManager != null;
        notificationBuilder.setChannelId(NOTIFICATION_CHAT_CHANNEL_ID);
        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(101, notificationBuilder.build());

        Intent intent = new Intent("alert_msg");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }





    private void initQBRTCClient() {
        if (Util.isAppOnForeground(getApplicationContext())) {
            QBRTCClient rtcClient = QBRTCClient.getInstance(getApplicationContext());
            // Add signalling manager
            if (QBChatService.getInstance() != null) {
                QBChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener((qbSignaling, createdLocally) -> {
                    if (!createdLocally) {
                        rtcClient.addSignaling(qbSignaling);
                    }
                });
            }

            // Configure
            QBRTCConfig.setDebugEnabled(true);
            SettingsUtil.configRTCTimers(this);

            // Add service as callback to RTCClient
            rtcClient.addSessionCallbacksListener(WebRtcSessionManager.getInstance(getApplicationContext()));
            rtcClient.prepareToProcessCalls();
        }
    }
}
