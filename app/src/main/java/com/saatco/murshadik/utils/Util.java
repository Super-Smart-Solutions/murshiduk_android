package com.saatco.murshadik.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.saatco.murshadik.App;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentStatus;
import com.tapadoo.alerter.Alerter;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Devlomi on 01/08/2017.
 */

public class Util {
    public static void showSnackbar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackbar(Activity activity, String message, int length) {
        Snackbar.make(activity.findViewById(android.R.id.content), message, length).show();
    }

    //extract file extension from full path
    public static String getFileExtensionFromPath(String string) {
        int index = string.lastIndexOf(".");
        String fileExtension = string.substring(index + 1);
        return fileExtension;
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static String getBuildDetails() {

        String mString = "";

        mString = mString + App.getInstance().getResources().getString(R.string.version);
        mString = mString + "-" + Build.BRAND + "-";
        mString = mString + "" + Build.DEVICE + "-";
        mString = mString + "" + Build.VERSION.RELEASE;

        return mString;
    }

    //extract file name from full path
    public static String getFileNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    //this will convert milliseconds progress to a human minutes and seconds like : 02:20
    public static String milliSecondsToTimer(long milliseconds) {

        return String.format(Locale.US, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        );

    }

    public static String formatCallTime(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    //this will convert file size as bytes to KB or MB  size
    public static String getFileSizeFromLong(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "currentFontIndex");
        return String.format(Locale.US, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    //this will highlight the text when user searches for message in chat
    public static Spanned highlightText(String fullText) {
        Spannable wordtoSpan = new SpannableString(fullText);
        wordtoSpan.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan;
    }


    //get video length using its file
    public static String getVideoLength(Context context, String path) {
        if (path == null) return "";

        long mediaLengthInMillis = getMediaLengthInMillis(context, path);
        return Util.milliSecondsToTimer(mediaLengthInMillis);


    }

    /**
     * check if the application is opened
     */
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static Date getDateFromString(String dateUser) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            date = format.parse(dateUser);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateHeader(long date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        return df.format(date);
    }

    //get audio length using its file
    public static long getMediaLengthInMillis(Context context, String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(context, Uri.parse(path));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return Long.parseLong(time);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    //check if a string is contains digits
    public static boolean isNumeric(String s) {
        boolean isDigit = false;
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c))
                return true;
            else
                isDigit = false;
        }

        return isDigit;

    }

    public static String getPhotoUrl(User user) {

        ArrayList<User> consultants = StorageHelper.getConsultants() != null ? StorageHelper.getConsultants() : new ArrayList<>();

        for (User consultant : consultants) {
            if (consultant.getObjectId().equals(user.getObjectId())) {
                user.setPhotoUrl(consultant.getPhotoUrl());
                return consultant.getPhotoUrl();
            }
        }

        return "";
    }

    public static void showToast(String text, Activity activity) {
        Alerter.create(activity)
                .setText(text)
                .setBackgroundColorRes(com.potyvideo.library.R.color.yellow)
                .show();
    }

    public static void showSuccessToast(String text, Activity activity) {
        Alerter.create(activity)
                .setText(text)
                .setIcon(R.drawable.ic_check)
                .setBackgroundColorRes(R.color.success_color)
                .show();
    }

    public static void showErrorToast(String text, Activity activity) {
        Alerter.create(activity)
                .setText(text)
                .setBackgroundColorRes(R.color.red)
                .show();
    }

    public static void showChatAlert(final String text, final Activity activity) {
        Alerter.create(activity)
                .setTitle("طلب استشارة")
                .setText(text)
                .setTextAppearance(R.style.AlertTextAppearance)
                .setBackgroundColorRes(R.color.text_color_dark_grey)
                .show();
    }

    //------------------- created by amin ---------------------
    /*
     *
     * */
    public static String createNotificationChannel(Context context, String channel_id, String notification_name) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, notification_name, importance);
            channel.setDescription(notification_name);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        return channel_id;
    }


    public static boolean isOreoOrAbove() {
        return Build.VERSION.SDK_INT >= 26;
    }

    public static HashSet<User> updateChatList(DataSnapshot dataSnapshot, ArrayList<User> consultantList) {

        HashSet<User> users = new HashSet<>();

        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            for (int i = 0; i < consultantList.size(); i++) {
                if (consultantList.get(i).getObjectId().equals(postSnapshot.child("senderId").getValue()) || consultantList.get(i).getObjectId().equals(postSnapshot.child("recipientId").getValue())) {
                    //  consultantList.get(i).setDateTime(new Date((long) postSnapshot.child("lastMessageDate").getValue()));
                    consultantList.get(i).setLastMessage(String.valueOf(postSnapshot.child("lastMessageText").getValue()));
                    if (!consultantList.get(i).getObjectId().equals(ProfileHelper.getAccount(App.getInstance()).getObjectId()))
                        users.add(consultantList.get(i));
                }
            }
        }

        ArrayList<User> storageUsers = StorageHelper.getChatUsers() != null ? StorageHelper.getChatUsers() : new ArrayList<>();
        ArrayList<User> newUsers = new ArrayList<>(users);

        if (storageUsers.size() > 0) {
            for (User newUser : newUsers) {
                Iterator<User> it = storageUsers.iterator();
                while (it.hasNext()) {
                    User msg = it.next();
                    if (msg.getObjectId().equals(newUser.getObjectId())) {
                        it.remove();
                    }
                }
            }
        }

        StorageHelper.saveAllChatUsers(storageUsers.size() > 0 ? storageUsers : newUsers);

        return users;
    }

    public static String randomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static boolean isPhoneSilent(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    public static boolean isPhoneVibrate(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
    }

    public static boolean isPhoneNormal(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * Combine a bold text with normal to set it in the TextView text
     *
     * @param text_to_be_bold   string of text that will be bold in the return
     * @param text_to_be_normal string of text that will be normal in the return
     * @return string that start with the bold text concat with the normal text
     */
    public static Spanned makeBoldAndNormalText(String text_to_be_bold, String text_to_be_normal, boolean is_bold_first) {
        if (is_bold_first)
            return Html.fromHtml("<b>" + text_to_be_bold + ":</b> " + text_to_be_normal.replaceAll("\n", "<br/>"));
        return Html.fromHtml("<b>" + text_to_be_normal.replaceAll("\n", "<br/>") + ":</b> " + text_to_be_bold);
    }


    /**
     * this method return the type of file
     *
     * @param context: should be getAppContext
     * @param uri:     the uri of file
     * @return : return the type name of file
     */
    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }


    /**
     * Disable user interaction while loading some data
     */
    public static void disableInteraction(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Enable user interaction after loading data
     */
    public static void enableInteraction(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    public static int dpToPixels(Context context, int sizeInDp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp * scale + 0.5f);
    }


    public static int pixelsToDp(Context context, int sizeInPixels) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((sizeInPixels - 0.5f) / scale);
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }


    /***
     *
     * get random integer number
     * @param from small number
     * @param to big number
     * @return number between small and big numbers
     */
    public static int getRandomInt(int from, int to) {
        Random random = new Random();
        int different = to - from;
        int result = (random.nextInt() % different) + from;
        if (result < 0) result = 0;
        return result;
    }


    /**
     * method to check if the date and time is in the range
     * date format ("yyyy-MM-dd'T'HH:mm:ss", new Locale("ar", "SA"))
     * @param startDateTime start date and time
     * @param currentDateTime current date and time
     * @return time deference in minutes (start time is in future return negative value)
     * */
    public static long getTimeDef(String startDateTime, String currentDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("ar", "SA"));
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        try {
            Date startDate = sdf.parse(startDateTime);
            Date currentDate = sdf.parse(currentDateTime);
            assert currentDate != null;
            assert startDate != null;
            long diff = currentDate.getTime() - startDate.getTime();
            return diff / (60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Consts.INVALID_RETURN_TIME;
    }

    /**
     * method to
     */

    public static ClinicAppointmentStatus getClinicAppointmentStatus(String appointmentDateTime, String serverDateTime) {
        if (appointmentDateTime == null || serverDateTime == null)
            return ClinicAppointmentStatus.CANCELED;

        long timeDefMinutes = getTimeDef(appointmentDateTime, serverDateTime);
        if (timeDefMinutes >= 0 && timeDefMinutes <= 15)
            return ClinicAppointmentStatus.AVAILABLE;
        else if (timeDefMinutes > 15)
            return ClinicAppointmentStatus.DONE;
        else
            return ClinicAppointmentStatus.PENDING;

    }


}