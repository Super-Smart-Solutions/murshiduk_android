package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.saatco.murshadik.utils.PermissionsChecker;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.ToastUtils;

public class PermissionsActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 45554;
    private static final String EXTRA_PERMISSIONS = "extraPermissions";
    private static final String CHECK_ONLY_AUDIO = "checkAudio";
    private static final String IS_CALLED_CAUSE_INCOMING_CALL = "isCalledCauseIncomingCall";

    private enum permissionFeatures {
        CAMERA,
        MICROPHONE,
        BLUETOOTH
    }

    private PermissionsChecker checker;
    private boolean requiresCheck;

    public static void startActivity(Context context, boolean checkOnlyAudio,boolean isCalledCauseIncomingCall, String... permissions) {
        Intent intent = new Intent(context, PermissionsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        intent.putExtra(CHECK_ONLY_AUDIO, checkOnlyAudio);
        intent.putExtra(IS_CALLED_CAUSE_INCOMING_CALL, isCalledCauseIncomingCall);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, boolean checkOnlyAudio, String... permissions) {
        Intent intent = new Intent(context, PermissionsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        intent.putExtra(CHECK_ONLY_AUDIO, checkOnlyAudio);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            throw new RuntimeException("This Activity needs to be launched using the static startActivityForResult() method.");
        }
        setContentView(R.layout.activity_permissions);

        checker = new PermissionsChecker(this);
        requiresCheck = true;


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requiresCheck) {
            checkPermissions();
        } else {
            requiresCheck = true;
        }
    }

    private void checkPermissions() {
        String[] permissions = getPermissions();
        boolean checkOnlyAudio = getCheckOnlyAudio();


        if (checkOnlyAudio) {
            checkPermissionAudio(permissions[1]);
        } else {
            checkPermissionAudioVideo(permissions);
        }
    }

    private void checkPermissionAudio(String audioPermission) {
        if (checker.lacksPermissions(audioPermission)) {
            requestPermissions(new String[]{audioPermission});
        } else {
            allPermissionsGranted();
        }
    }

    private void checkPermissionAudioVideo(String[] permissions) {
        if (checker.lacksPermissions(permissions)) {
            requestPermissions(permissions);
        } else {
            allPermissionsGranted();
        }
    }

    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    private boolean getCheckOnlyAudio() {
        return getIntent().getBooleanExtra(CHECK_ONLY_AUDIO, false);
    }

    private void requestPermissions(String[] permissions) {
        // asking for bluetooth connect because QBWebRtc using it in call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissions = addItem(permissions.length, permissions, Manifest.permission.BLUETOOTH_CONNECT);
        }
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    private void allPermissionsGranted() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            requiresCheck = true;
            allPermissionsGranted();
        } else {
            for (String permission : permissions) {
                if (!shouldShowRequestPermissionRationale(permission)) {
                    SharedPrefsHelper.getInstance().save(permission, false);
                }
            }
            requiresCheck = false;
            showDeniedResponse(grantResults);
            finish();
        }
    }

    private void showDeniedResponse(int[] grantResults) {

        if (grantResults.length > 1) {
            for (int i = 0; i < grantResults.length && i < permissionFeatures.values().length; i++) {
                if (grantResults[i] != 0) {
                    ToastUtils.longToast(getString(R.string.permission_unavailable, permissionFeatures.values()[i]));
                }
            }
        } else {
            ToastUtils.longToast(getString(R.string.permission_unavailable, permissionFeatures.MICROPHONE));
        }
    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        if (grantResults.length < 1) return false;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // Function to add x in arr
    public static String[] addItem(int length, String[] arr, String newItem)
    {
        int i;
        // create a new array of size n+1
        String[] newArr = new String[length + 1];

        for (i = 0; i < length; i++)
            newArr[i] = arr[i];

        newArr[length] = newItem;

        return newArr;
    }
}