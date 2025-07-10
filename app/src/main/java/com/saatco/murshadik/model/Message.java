package com.saatco.murshadik.model;

import java.util.ArrayList;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "message")
public class Message {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "message")
    @SerializedName("message")
    private String message;

    @Ignore
    private String mTimestamp;

    private int isMSGbyMe;
    private String user1ID;
    private String user2ID;
    private String text;
    private String objectId;
    private String groupId;
    private String groupName;
    private String fileUrl;
    private String fileName;

    @ColumnInfo(name = "chat_ids")
    private String chatUserIds;
    private String path;

    @SerializedName("message_from")
    @ColumnInfo(name = "user_id")
    private int userId;

    @Ignore
    private ArrayList<String> members;

    private boolean isRead;

    private long createdAt;

    @Ignore
    @SerializedName("created_at")
    private String date;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser1ID() {
        return user1ID;
    }

    public void setUser1ID(String user1ID) {
        this.user1ID = user1ID;
    }

    public String getUser2ID() {
        return user2ID;
    }

    public void setUser2ID(String user2ID) {
        this.user2ID = user2ID;
    }

    public String getmTimestamp() {
        return mTimestamp;
    }

    public void setmTimestamp(String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public int getIsMSGbyMe() {
        return isMSGbyMe;
    }

    public void setIsMSGbyMe(int isMSGbyMe) {
        this.isMSGbyMe = isMSGbyMe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "message_type")
    @SerializedName("message_type")
    private int type;

    private long time;
    private boolean seen;
    private String from;
    private String recipientFullname;
    private String recipientId;
    private long recipientPictureAt;
    private String senderFullname;
    private int senderId;

    @ColumnInfo(name = "status")
    private int status;

    private double latitude;
    private double longitude;

    public String getRecipientFullname() {
        return recipientFullname;
    }

    public void setRecipientFullname(String recipientFullname) {
        this.recipientFullname = recipientFullname;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public long getRecipientPictureAt() {
        return recipientPictureAt;
    }

    public void setRecipientPictureAt(long recipientPictureAt) {
        this.recipientPictureAt = recipientPictureAt;
    }

    public String getSenderFullname() {
        return senderFullname;
    }

    public void setSenderFullname(String senderFullname) {
        this.senderFullname = senderFullname;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // default constructor
    public Message() {
    }

    // constructor
    @Ignore
    public Message(String message, int type, long time, boolean seen, String from) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
    }

    // getter & setter

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChatUserIds() {
        return chatUserIds;
    }

    public void setChatUserIds(String chatUserIds) {
        this.chatUserIds = chatUserIds;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
