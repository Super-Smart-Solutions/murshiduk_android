package com.saatco.murshadik.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsHelper {


    public enum PermissionEnum {
        MEDIA_IMAGE,
        MEDIA_AUDIO,
        MEDIA_VIDEO
    }

    public static boolean isPermissionGranted(String str_permission_name, Activity activity, int request_code) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), str_permission_name) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ActivityCompat.requestPermissions(activity, new String[]{str_permission_name, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, request_code);
            } else
                ActivityCompat.requestPermissions(activity, new String[]{str_permission_name}, request_code);
            return false;
        }
        return true;
    }

    public static boolean isMediaImagePermissionGranted(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES, activity, 555)
                || isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, activity, 555);
    }

    public static boolean isMediaVideoPermissionGranted(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isPermissionGranted(Manifest.permission.READ_MEDIA_VIDEO, activity, 555) ||
                isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, activity, 555);
    }

    public static boolean isMediaAudioPermissionGranted(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isPermissionGranted(Manifest.permission.READ_MEDIA_AUDIO, activity, 555) ||
                isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, activity, 555);
    }


}

