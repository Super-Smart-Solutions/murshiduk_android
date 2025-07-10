package com.saatco.murshadik.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.quickblox.chat.model.QBChatDialog;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@Entity(tableName ="user_table")
public class User implements Serializable, Comparable<User> {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "id")
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;

    @ColumnInfo(name = "full_name")
    private String fullname;

    @SerializedName("country")
    private String country;

    private int lastActive;

    @SerializedName("region")
    private String location;

    @ColumnInfo(name = "last_message")
    @SerializedName("last_message")
    private String lastMessage;

    private String firstName;

    @ColumnInfo(name = "last_name")
    @SerializedName("last_name")
    private String lastName;

    @ColumnInfo(name = "skills")
    @SerializedName("skills")
    private String status;

    @SerializedName("active")
    private boolean active;

    @Ignore
    private boolean available;

    @ColumnInfo(name = "role_id")
    @SerializedName("role_id")
    private int roleId;

    @ColumnInfo(name = "rating")
    @SerializedName("rating")
    private double userRatings;

    @ColumnInfo(name = "is_online")
    @SerializedName("is_online")
    private boolean isOnline;

    private String city;

    @Ignore
    @SerializedName("governorate")
    private String governorate;

    @ColumnInfo(name = "city_code")
    @SerializedName("city_code")
    private String weatherCode;

    @ColumnInfo(name = "is_profile_completed")
    @SerializedName("is_profile_completed")
    private boolean isProfileComplete;

    @ColumnInfo(name = "is_chat_user")
    private boolean isChatUser;

    @Ignore
    @SerializedName("message_type")
    private int messageType;

    @Ignore
    @SerializedName("can_post")
    private boolean canPost;

    @Ignore
    @SerializedName("is_group_admin")
    private boolean isGroupAdmin;

    @Ignore
    @SerializedName("last_login")
    private String lastLogin;

    public String getLastLogin() {
        return lastLogin;
    }



    public Date getLastLoginDate(){
        // date format "2021-09-28T13:49:11.517"
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date  = fromUser.parse(getLastLogin());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date == null ? new Date() : date;
    }

    public boolean isLastLogin(Date after){
        return getLastLoginDate().after(after);
    }

    public boolean isCanPost() {
        return canPost;
    }

    public void setCanPost(boolean canPost) {
        this.canPost = canPost;
    }

    public boolean isGroupAdmin() {
        return isGroupAdmin;
    }

    public void setGroupAdmin(boolean groupAdmin) {
        isGroupAdmin = groupAdmin;
    }

    public boolean isProfileComplete() {
        return isProfileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        isProfileComplete = profileComplete;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeatherCode() {
        return weatherCode == null? "OERK": weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private String lastMessageDate;

    private long lastMsgTime;

    private int msgCount;

    @Ignore
    private ArrayList<String> members;


    private int userId; //created by

    private boolean isGroup;

    private String onlineStatus;

    private boolean isConsultant;

    @SerializedName("is_approved")
    private boolean isApproved;

    private String profile;

    @Ignore
    public QBChatDialog chatDialog;

    @ColumnInfo(name = "avatar")
    @SerializedName("avatar")
    private String photoUrl;

    @ColumnInfo(name = "chat_id")
    @SerializedName("chatId")
    private String chatId;

    @Ignore
    @SerializedName("is_market_notification_enabled")
    private boolean isMarketNotificationEnabled;

    @Ignore
    @SerializedName("is_weather_notification_enabled")
    private boolean isWeatherNotificationEnabled;

    public boolean isMarketNotificationEnabled() {
        return isMarketNotificationEnabled;
    }

    public void setMarketNotificationEnabled(boolean marketNotificationEnabled) {
        isMarketNotificationEnabled = marketNotificationEnabled;
    }

    public boolean isWeatherNotificationEnabled() {
        return isWeatherNotificationEnabled;
    }

    public void setWeatherNotificationEnabled(boolean weatherNotificationEnabled) {
        isWeatherNotificationEnabled = weatherNotificationEnabled;
    }

    private String prefix;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullname() {
        return firstName + " " + lastName;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMesiboId() {
        return mesiboId;
    }

    public void setMesiboId(String mesiboId) {
        this.mesiboId = mesiboId;
    }

    private String mesiboId;

    @ColumnInfo(name = "phone")
    @SerializedName("phone")
    private String phoneNumber;

    @SerializedName("firebase_token")
    private String firebaseToken;

    @SerializedName("region_id")
    private int regionId;

    @Ignore
    private ArrayList<Skill> skillsList;

    @Ignore
    @SerializedName("groups")
    private ArrayList<Group> groups;

    //firebase
    private String udid;

    private String objectId;

    private String providerID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.firstName = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }


    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public ArrayList<Skill> getSkills() {
        return skillsList;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skillsList = skills;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", fullname='" + fullname + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status='" + status + '\'' +
                ", active=" + active +
                ", available=" + available +
                ", roleId=" + roleId +
                ", isOnline=" + isOnline +
                ", city='" + city + '\'' +
                ", governorate='" + governorate + '\'' +
                ", weatherCode='" + weatherCode + '\'' +
                ", isProfileComplete=" + isProfileComplete +
                ", photoUrl='" + photoUrl + '\'' +
                ", chatId='" + chatId + '\'' +
                ", regionId=" + regionId +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public int getLastActive() {
        return lastActive;
    }

    public void setLastActive(int lastActive) {
        this.lastActive = lastActive;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    private String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String datetime) {
        this.dateTime = datetime;
    }

    @Override
    public int compareTo(User o) {
        return getDate().compareTo(o.getDate());
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public boolean isConsultant() {
        return roleId == 6;
    }

    public void setConsultant(boolean consultant) {
        isConsultant = consultant;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public double getUserRatings() {
        return userRatings;
    }

    public String getUserRatingStr() {
        return String.valueOf(userRatings);
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getLocation() {
        return location == null ? "" : location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setUserRatings(double userRatings) {
        this.userRatings = userRatings;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getGetOnlyName() {
        return name;
    }

    public boolean isChatUser() {
        return isChatUser;
    }

    public void setChatUser(boolean chatUser) {
        isChatUser = chatUser;
    }

    public Date getDate(){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        try {
            date  = fromUser.parse(getDateTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date == null ? new Date() : date;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getGovernorate() {
        return governorate;
    }

    public boolean isFarmer(){
        return roleId == 5;
    }

    public boolean isConsultantUser(){
        return roleId == 6;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }
}
