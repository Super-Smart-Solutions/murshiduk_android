package com.saatco.murshadik.utils;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.users.model.QBUser;
import com.saatco.murshadik.App;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PushNotificationSender {

    /**
     * @param voipCode used for know if push for start call or for end it, set 99 to end call notify
     * */
    public static void sendPushMessage(ArrayList<Integer> recipients, String sessionId, String senderName, String voipCode) {
        String outMessage = String.format(String.valueOf(R.string.text_push_notification_message), senderName);

        // Send Push: create QuickBlox Push Notification Event
        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.PRODUCTION);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String eventDate = simpleDateFormat.format(currentTime);

        // Generic push - will be delivered to all platforms (Android, iOS, WP, Blackberry..)

        JSONObject json = new JSONObject();
        try {
            json.put("message", outMessage);
            json.put("ios_voip", "1");
            json.put("VOIPCall", voipCode);
            json.put("callerID", senderName);
            json.put("chat_id", recipients.get(0));
            json.put("sender_id", SharedPrefsHelper.getInstance().getQbUser().getId());
            json.put("timestamp", eventDate);
            json.put("sessionID", sessionId);
            if (ProfileHelper.getAccount(App.getInstance()).getChatId() != null)
                json.put("name", Integer.parseInt(ProfileHelper.getAccount(App.getInstance()).getChatId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        qbEvent.setMessage(json.toString());

        StringifyArrayList<Integer> userIds = new StringifyArrayList<>(recipients);
        qbEvent.setUserIds(userIds);

        QBPushNotifications.createEvents(qbEvent).performAsync(new QBEntityCallback<List<QBEvent>>() {
            @Override
            public void onSuccess(List<QBEvent> qbEvents, Bundle bundle) {
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("CALLID", e.getLocalizedMessage());

            }
        });
    }

    public static void sendMessageNotification(ArrayList<Integer> recipients, String senderName, String body, int type) {
//        String outMessage = String.format(String.valueOf(R.string.text_push_notification_message), senderName);

        User user_sender = ProfileHelper.getAccount(App.getInstance());
        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.PRODUCTION);

        String messageBody = body;

        if (type == 2)
            messageBody = "[رسالة صوت]";
        else if (type == 3)
            messageBody = "[رسالة فيديو]";
        else if (type == 4)
            messageBody = "[رسالة صور]";
        else if (type == 5)
            messageBody = "[رسالة ملف]";
        else if (type == 6)
            messageBody = "[موقع]";

        QBUser qbUser = SharedPrefsHelper.getInstance().getQbUser();

        JSONObject json = new JSONObject();
        try {
            json.put("body", messageBody);
            json.put("message", "رسالة من " + senderName);
            json.put("ios_sound", "app_sound.wav");
            json.put("ios_badge", "1");
            json.put("sender_id", String.valueOf(qbUser.getId()));
            json.put("sender_name", senderName);
            json.put(Consts.PUSHES_PUSH_TYPE, Consts.PUSHES_PUSH_TYPE_MESSAGE);
            json.put("type", type);
            if (user_sender != null) {
                json.put(Consts.PUSHES_CHAT_ID, user_sender.getChatId());
                json.put("role_id", user_sender.getRoleId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        qbEvent.setMessage(json.toString());

        StringifyArrayList<Integer> userIds = new StringifyArrayList<>(recipients);
        qbEvent.setUserIds(userIds);

        QBPushNotifications.createEvents(qbEvent).performAsync(new QBEntityCallback<List<QBEvent>>() {
            @Override
            public void onSuccess(List<QBEvent> qbEvents, Bundle bundle) {
                Log.d("CALLID", qbEvents.toString());


            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("CALLID", e.getLocalizedMessage());
            }
        });
    }
}