package com.saatco.murshadik.Helpers;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.saatco.murshadik.model.GroupChat;
import com.saatco.murshadik.model.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseObjectHelper {

    public static HashMap<String,Object> getJsonObject(String type,String message,double latitude,double longitude, String chatId, String message_push_id, String messageSenderId,String messageReceiverID, User receiver,User sender){

        HashMap<String, Object> message_text_body = new HashMap<>();
        message_text_body.put("audioDuration",0);
        message_text_body.put("createdAt", ServerValue.TIMESTAMP);
        message_text_body.put("groupId","");
        message_text_body.put("chatId",chatId);
        message_text_body.put("objectId",message_push_id);
        message_text_body.put("groupName","");
        message_text_body.put("isDeleted",false);
        message_text_body.put("latitude",latitude);
        message_text_body.put("longitude",longitude);
        message_text_body.put("photoHeight",100);
        message_text_body.put("photoWidth",100);
        message_text_body.put("recipientFullname",receiver.getName());
        message_text_body.put("recipientId",messageReceiverID);
        message_text_body.put("recipientInitials",receiver.getName().replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2").toUpperCase());
        message_text_body.put("recipientPictureAt",ServerValue.TIMESTAMP);
        message_text_body.put("senderFullname",sender.getName());
        message_text_body.put("senderId",messageSenderId);
        message_text_body.put("senderInitials",sender.getName().replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2").toUpperCase());
        message_text_body.put("senderPictureAt",ServerValue.TIMESTAMP);
        message_text_body.put("status","Sent");
        message_text_body.put("type",type);
        message_text_body.put("updatedAt",ServerValue.TIMESTAMP);
        message_text_body.put("videoDuration",0);
        message_text_body.put("members", Arrays.asList(messageSenderId,messageReceiverID));

        if(type.equals("text"))
            message_text_body.put("text",message);
        else if(type.equals("photo"))
            message_text_body.put("text","[رسالة صور]");
        else if(type.equals("video"))
            message_text_body.put("text","[رسالة فيديو]");
        else if(type.equals("audio"))
            message_text_body.put("text","[رسالة صوت]");
        else if(type.equals("file"))
            message_text_body.put("text","[رسالة ملف]");
        else
            message_text_body.put("text","[موقع]");


        return message_text_body;
    }

    public static HashMap<String,Object> getJsonFileObject(String type,String fileName,double latitude,double longitude, String chatId, String message_push_id, String messageSenderId,String messageReceiverID, User receiver,User sender,String fileUrl){

        HashMap<String, Object> message_text_body = new HashMap<>();
        message_text_body.put("audioDuration",0);
        message_text_body.put("createdAt", ServerValue.TIMESTAMP);
        message_text_body.put("groupId","");
        message_text_body.put("chatId",chatId);
        message_text_body.put("objectId",message_push_id);
        message_text_body.put("groupName","");
        message_text_body.put("isDeleted",false);
        message_text_body.put("latitude",latitude);
        message_text_body.put("longitude",longitude);
        message_text_body.put("photoHeight",100);
        message_text_body.put("photoWidth",100);
        message_text_body.put("recipientFullname",receiver.getName());
        message_text_body.put("recipientId",messageReceiverID);
        message_text_body.put("recipientInitials",receiver.getName().replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2").toUpperCase());
        message_text_body.put("recipientPictureAt",ServerValue.TIMESTAMP);
        message_text_body.put("senderFullname",sender.getName());
        message_text_body.put("senderId",messageSenderId);
        message_text_body.put("senderInitials",sender.getName().replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2").toUpperCase());
        message_text_body.put("senderPictureAt",ServerValue.TIMESTAMP);
        message_text_body.put("status","Sent");
        message_text_body.put("type",type);
        message_text_body.put("updatedAt",ServerValue.TIMESTAMP);
        message_text_body.put("videoDuration",0);
        message_text_body.put("members", Arrays.asList(messageSenderId,messageReceiverID));
        message_text_body.put("text","[File Attachment]");
        message_text_body.put("fileUrl",fileUrl);

        return message_text_body;
    }

    public static HashMap<String,Object> getGroupJsonObject(String type, GroupChat group, String message, double latitude, double longitude, String chatId, String message_push_id, String messageSenderId, User sender){

        HashMap<String, Object> message_text_body = new HashMap<>();

        message_text_body.put("audioDuration",0);
        message_text_body.put("createdAt", ServerValue.TIMESTAMP);
        message_text_body.put("chatId",chatId);
        message_text_body.put("objectId",message_push_id);
        message_text_body.put("groupName",group.getName());
        message_text_body.put("isDeleted",false);
        message_text_body.put("latitude",latitude);
        message_text_body.put("longitude",longitude);
        message_text_body.put("photoHeight",100);
        message_text_body.put("photoWidth",100);
        message_text_body.put("recipientFullname","");
        message_text_body.put("recipientId","");
        message_text_body.put("recipientInitials","");
        message_text_body.put("recipientPictureAt",ServerValue.TIMESTAMP);
        message_text_body.put("senderFullname",sender.getName());
        message_text_body.put("senderId",messageSenderId);
        message_text_body.put("senderInitials",sender.getName().replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2").toUpperCase());
        message_text_body.put("senderPictureAt",ServerValue.TIMESTAMP);
        message_text_body.put("status","Sent");
        message_text_body.put("type",type);
        message_text_body.put("updatedAt",ServerValue.TIMESTAMP);
        message_text_body.put("videoDuration",0);

        if(type.equals("text"))
            message_text_body.put("text",message);
        else if(type.equals("photo"))
            message_text_body.put("text","[Photo attachment]");
        else if(type.equals("video"))
            message_text_body.put("text","[Video attachment]");
        else if(type.equals("audio"))
            message_text_body.put("text","[Audio attachment]");
        else
            message_text_body.put("text","[Location]");


        return message_text_body;
    }

    public static HashMap<String,Object> createChatLog(String type,String message, String chatId, String messageSenderId,String messageReceiverID, User receiver,User sender){

        HashMap<String, Object> archives = new HashMap<>();
        archives.put(messageSenderId,false);
        archives.put(receiver.getObjectId(),false);

        HashMap<String, Object> deleteds = new HashMap<>();
        deleteds.put(messageSenderId,false);
        deleteds.put(receiver.getObjectId(),false);

        HashMap<String, Object> lastReads = new HashMap<>();
        lastReads.put(messageSenderId,ServerValue.TIMESTAMP);
       // lastReads.put(receiver.getObjectId(),ServerValue.TIMESTAMP);

        HashMap<String, Object> linkeds = new HashMap<>();
        linkeds.put(messageSenderId,true);
        linkeds.put(receiver.getObjectId(),true);

        HashMap<String, Object> typings = new HashMap<>();
        typings.put(messageSenderId,false);
        typings.put(receiver.getObjectId(),false);

        HashMap<String, Object> message_text_body = new HashMap<>();
        message_text_body.put("archiveds",archives);
        message_text_body.put("deleteds",deleteds);
        message_text_body.put("createdAt", ServerValue.TIMESTAMP);
        message_text_body.put("updatedAt", ServerValue.TIMESTAMP);
        message_text_body.put("groupId","");
        message_text_body.put("chatId",chatId);
        message_text_body.put("objectId",chatId);
        message_text_body.put("groupName","");
        message_text_body.put("lastMessageDate",ServerValue.TIMESTAMP);
        message_text_body.put("lastReads",lastReads);
        message_text_body.put("linkeds",linkeds);
        message_text_body.put("typings",typings);
        message_text_body.put("recipientFullname",receiver.getName());
        message_text_body.put("recipientId",receiver.getObjectId());
        message_text_body.put("recipientInitials",receiver.getName().replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2").toUpperCase());
        message_text_body.put("recipientPictureAt",ServerValue.TIMESTAMP);
        message_text_body.put("senderFullname",sender.getName());
        message_text_body.put("senderId",messageSenderId);
        message_text_body.put("senderInitials",sender.getName().replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2").toUpperCase());
        message_text_body.put("senderAvatarAt",sender.getPhotoUrl() != null ? sender.getPhotoUrl().replace("https://murshadik.saatco.net/storage/","") : "");
        message_text_body.put("recipientAvatarAt",receiver.getPhotoUrl());
        message_text_body.put("members",Arrays.asList(messageSenderId,messageReceiverID));

        if(type.equals("text"))
            message_text_body.put("lastMessageText",message);
        else if(type.equals("photo"))
            message_text_body.put("lastMessageText","[رسالة صور]");
        else if(type.equals("video"))
            message_text_body.put("lastMessageText","[رسالة فيديو]");
        else if(type.equals("audio"))
            message_text_body.put("lastMessageText","[[رسالة صوت]");
        else if(type.equals("file"))
            message_text_body.put("lastMessageText","[رسالة ملف]");
        else
            message_text_body.put("lastMessageText","[موقع]");


        return message_text_body;
    }

    public static void updateLastActive(Context context) {

        if(ProfileHelper.hasAccount(context)) {

        }

    }

    public static void updateLastTerminate(Context context){

        if(ProfileHelper.hasAccount(context)) {

        }

    }

    public static void updateChatId(Context context,int chatId){

        if(ProfileHelper.hasAccount(context)) {

        }
    }

    public static void updateAvatar(Context context,String fileName){

        if(ProfileHelper.hasAccount(context)) {
            if(ProfileHelper.getAccount(context).getUdid() != null) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(ProfileHelper.getAccount(context).getUdid());

                Map<String, Object> users = new HashMap<>();
                users.put("updatedAt", ServerValue.TIMESTAMP);
                users.put("avatarAt","avatars/"+fileName);
                usersRef.updateChildren(users);
            }
        }

    }
}
