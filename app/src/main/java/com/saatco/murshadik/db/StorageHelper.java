package com.saatco.murshadik.db;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saatco.murshadik.App;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.model.ContactUs;
import com.saatco.murshadik.model.Message;
import com.saatco.murshadik.model.Notifications;
import com.saatco.murshadik.model.User;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class StorageHelper {

    public static void saveMessages(Message message) {
        ArrayList<Message> messages = getAllMessages() == null ? new ArrayList<Message>() : getAllMessages();
        messages.add(message);
        Gson gson = new Gson();
        String jsonCars = gson.toJson(messages);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_msg", jsonCars);
    }

    public static ArrayList<Message> getAllMessages(){
        ArrayList<Message> messages;
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(),"json_msg");
        Log.v("bigo","pos is :"+positions);
        Type type = new TypeToken<ArrayList<Message>>(){}.getType();
        messages = gson.fromJson(positions, type);
        return messages;
    }

    public static void saveMediaPath(String objectId,String path) {
        Message message = new Message();
        message.setObjectId(objectId);
        message.setPath(path);
        ArrayList<Message> messages = getAllMessages() == null ? new ArrayList<Message>() : getAllMessages();
        messages.add(message);
        Gson gson = new Gson();
        String jsonCars = gson.toJson(messages);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_msg_path", jsonCars);
    }

    public static String getMediaPath(String messageObjId){
        String path = "";
        ArrayList<Message> messages = new ArrayList<>();
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(),"json_msg_path");
        Type type = new TypeToken<ArrayList<Message>>(){}.getType();
         messages = gson.fromJson(positions, type);

         if(messages != null){
             for(Message message : messages){
                 if(message.getObjectId() != null) {
                     if (message.getObjectId().equals(messageObjId))
                         path = message.getPath();
                 }
             }
         }
         return path;
    }

    public static void saveFarmers(ArrayList<User> farmers) {
        Gson gson = new Gson();
        String jsonCars = gson.toJson(farmers);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_farmers", jsonCars);
    }

    public static ArrayList<User> getAllFarmers(){
        ArrayList<User> messages;
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(),"json_farmers");
        Type type = new TypeToken<ArrayList<User>>(){}.getType();
        messages = gson.fromJson(positions, type);
        return messages;
    }

    public static void saveChatUsers(User consultant) {
        ArrayList<User> messages = getChatUsers() == null ? new ArrayList<User>() : getChatUsers();
        messages.add(consultant);
        Gson gson = new Gson();
        String jsonCars = gson.toJson(messages);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_chat_users", jsonCars);
    }


    public static void updateChatUser(User consultant,String message) {
        ArrayList<User> users = getChatUsers() == null ? new ArrayList<User>() : getChatUsers();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss a", Locale.ENGLISH);
        String strDate = dateFormat.format(new Date().getTime());

        if(users.size() > 0){
            for(User user : users){
                if(user.getChatId() != null) {
                    if (Integer.parseInt(user.getChatId()) == Integer.parseInt(consultant.getChatId())) {
                        user.setLastMessage(message);
                        user.setDateTime(strDate);
                    }
                }
            }
        }

        Gson gson = new Gson();
        String jsonCars = gson.toJson(users);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_chat_users", jsonCars);
    }

    public static void saveAllChatUsers(ArrayList<User> users) {
        Gson gson = new Gson();
        String jsonCars = gson.toJson(users);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_chat_users", jsonCars);
    }

    public static ArrayList<User> getChatUsers(){
        ArrayList<User> messages;
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(),"json_chat_users");
        Type type = new TypeToken<ArrayList<User>>(){}.getType();
        messages = gson.fromJson(positions, type);
        return messages;
    }

    public static boolean isUserExistInChatList(int id,ArrayList<User> list){

        if(list.size() > 0){
            for(User user : list){
                if(user.getChatId() != null) {
                    if (Integer.parseInt(user.getChatId()) == id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isUserExistInChat(String name){
        ArrayList<User> users = getChatUsers() == null ? new ArrayList<User>() : getChatUsers();

        if(users.size() > 0){
            for(User user : users){
                if(user.getName().equals(name)){
                    return true;
                }
            }
        }
        return false;
    }

    public static void updateLastChat(String lastMsg,int id,int type){

        ArrayList<User> users = getChatUsers() == null ? new ArrayList<User>() : getChatUsers();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
        String strDate = dateFormat.format(new Date().getTime());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, -2);

        if(users.size() > 0){
            for(User user : users){
                if(user.getId() == id){
                    user.setLastMessage(lastMsg);
                    user.setDateTime(strDate);
                    user.setMessageType(type);
                }else{
                    user.setDateTime(dateFormat.format(cal.getTime()));
                }
            }

            Gson gson = new Gson();
            String jsonCars = gson.toJson(users);
            PrefUtil.writePreferenceValue(App.getInstance(), "json_chat_users", jsonCars);
        }
    }

    public static void updateUserStatus(int id,boolean status){
        ArrayList<User> users = getChatUsers() == null ? new ArrayList<User>() : getChatUsers();

        if(users.size() > 0){
            for(User user : users){
                if(user.getId() == id){
                    user.setOnline(status);
                    break;
                }
            }

            Gson gson = new Gson();
            String jsonCars = gson.toJson(users);
            PrefUtil.writePreferenceValue(App.getInstance(), "json_chat_users", jsonCars);
        }

    }


    public static void saveConsultants(ArrayList<User> consultants) {
        Gson gson = new Gson();
        String jsonCars = gson.toJson(consultants);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_consult", jsonCars);
    }

    public static ArrayList<User> getConsultants(){
        ArrayList<User> messages = new ArrayList<>();
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(),"json_consult");
        Type type = new TypeToken<ArrayList<User>>(){}.getType();
        messages = gson.fromJson(positions, type);
        return messages == null ? new ArrayList<>() : messages;
    }


    public static int countUsers(int userId){
        int count = 0;
        ArrayList<Message> messages = getAllMessages() == null ? new ArrayList<Message>() : getAllMessages();

        if(messages != null) {
            if (messages.size() > 0) {

                for (Message msg : messages) {
                    if (msg.getUserId() == userId)
                        count++;
                }

            }
        }
        return count;
    }

    public static void removeMessage(int  id){
        ArrayList<Message> messages = getAllMessages();
        if(messages != null) {
            Iterator<Message> it = messages.iterator();

            while (it.hasNext()) {
                Message msg = it.next();
                if (msg.getUserId() == id) {
                    it.remove();
                }
            }

            Gson gson = new Gson();
            String jsonCars = gson.toJson(messages);
            PrefUtil.writePreferenceValue(App.getInstance(), "json_msg", jsonCars);
        }
    }

    public static void saveLastChat(List<Message> messages, int senderId, int recieverId){

        ArrayList<Message> messagesList = getChatMessages(senderId,recieverId) == null ? new ArrayList<Message>() : getChatMessages(senderId,recieverId);
        Iterator<Message> it = messagesList.iterator();

        while (it.hasNext()) {
            Message msg = it.next();
            String s1 = senderId + "," + recieverId;
            String s2 = recieverId + "," + senderId;
            if (msg.getChatUserIds().equals(s1) || msg.getChatUserIds().equals(s2)) {
                it.remove();
            }
        }

        List<Message> myLastPosts = messages;
        if(messages.size() > 20) {
            myLastPosts = messages.subList(messages.size() - 10, messages.size());
        }

        messagesList.addAll(myLastPosts);
        Gson gson = new Gson();
        String jsonCars = gson.toJson(messagesList);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_chat_msg", jsonCars);
    }

    public static void saveSingleChatMessage(Message message, int senderId, int recieverId){

        ArrayList<Message> messagesList = getChatMessages(senderId,recieverId) == null ? new ArrayList<Message>() : getChatMessages(senderId,recieverId);
        messagesList.add(message);
        Gson gson = new Gson();
        String jsonCars = gson.toJson(messagesList);
        PrefUtil.writePreferenceValue(App.getInstance(), "json_chat_msg", jsonCars);

    }
    public static ArrayList<Message> getChatMessages(int senderId,int recieverId){
        ArrayList<Message> messages = new ArrayList<>();
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(),"json_chat_msg");
        Type type = new TypeToken<ArrayList<Message>>(){}.getType();
        ArrayList<Message> messagList = gson.fromJson(positions, type);
        if(messagList != null) {
            for (Message msg : messagList) {

                String s1 = senderId + "," + recieverId;
                String s2 = recieverId + "," + senderId;

                if (msg.getChatUserIds().equals(s1) || msg.getChatUserIds().equals(s2)) {
                    messages.add(msg);
                }
            }
        }
        return messages;
    }

    public static void saveNotificationDateTime(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));
        String strDate = dateFormat.format(new Date().getTime());
        PrefUtil.writePreferenceValue(App.getInstance(),"notification_time",strDate);

    }

    public static String getNotificationDateTime(){
       return PrefUtil.getStringPref(App.getInstance(),"notification_time") == null? "" : PrefUtil.getStringPref(App.getInstance(),"notification_time");
    }

    public static void saveAllNotifications(ArrayList<Notifications> users) {

        Gson gson = new Gson();
        String jsonCars = gson.toJson(users);
        PrefUtil.writePreferenceValue(App.getInstance(), "user_notifications", jsonCars);
    }

    public static ArrayList<Notifications> getAllNotifications(){
        ArrayList<Notifications> messages;
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(),"user_notifications");
        Type type = new TypeToken<ArrayList<Notifications>>(){}.getType();
        messages = gson.fromJson(positions, type);
        return messages;
    }

    public static void saveNotificationsCounter(int count) {
       // Gson gson = new Gson();
       // String jsonCars = gson.toJson(users);
       // PrefUtil.writePreferenceValue(App.getInstance(), "user_notifications_counter", jsonCars);
        PrefUtil.writeIntValue(App.getInstance(),"user_notifications_counter",count);
    }

    public static int getNotificationsCounter(){
       /* ArrayList<Notifications> messages;
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(),"user_notifications_counter");
        Type type = new TypeToken<ArrayList<Notifications>>(){}.getType();
        messages = gson.fromJson(positions, type);
        return messages;*/
        return PrefUtil.getInteger(App.getInstance(),"user_notifications_counter");
    }

    public static void saveLocation(double latitude,double longitude) {
        PrefUtil.writePreferenceValue(App.getInstance(), "user_location",latitude+","+longitude);
    }

    public static String getLocation() {
        return PrefUtil.getStringPref(App.getInstance(),"user_location");
    }

    public static void saveContactUs(ContactUs user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        PrefUtil.writePreferenceValue(App.getInstance(),"contact_us",json);
    }

    public static void saveLastChattingConsultant(String name) {
        PrefUtil.writePreferenceValue(App.getInstance(),"consultant_name",name);
        PrefUtil.writePreferenceValue(App.getInstance(),"consultant_time", String.valueOf(System.currentTimeMillis())); //chat time to show rating after 24 hours
    }

    public static ContactUs getContactUS() {
        ContactUs user = new ContactUs("0596472727");
        Gson gson = new Gson();
        String json = PrefUtil.getStringPref(App.getInstance(),"contact_us");

        JSONObject j;

        try {
            j = new JSONObject(json);
            user = gson.fromJson(j.toString(), ContactUs.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }
}
