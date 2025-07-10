package com.saatco.murshadik;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.BaseSession;
import com.quickblox.videochat.webrtc.QBRTCScreenCapturer;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.audio.QBAudioManager;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionEventsCallback;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionStateCallback;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.QbUsersDbManager;
import com.saatco.murshadik.api.ApiMethodsHelper;
import com.saatco.murshadik.api.request.CallRequest;
import com.saatco.murshadik.fcm.CallService;
import com.saatco.murshadik.fcm.LoginService;
import com.saatco.murshadik.fragments.AudioConversationFragment;
import com.saatco.murshadik.fragments.BaseConversationFragment;
import com.saatco.murshadik.fragments.ConversationFragmentCallback;
import com.saatco.murshadik.fragments.IncomeCallFragment;
import com.saatco.murshadik.fragments.IncomeCallFragmentCallbackListener;
import com.saatco.murshadik.fragments.ScreenShareFragment;
import com.saatco.murshadik.fragments.VideoConversationFragment;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ErrorUtils;
import com.saatco.murshadik.utils.FragmentExecuotr;
import com.saatco.murshadik.utils.PermissionsChecker;
import com.saatco.murshadik.utils.PushNotificationSender;
import com.saatco.murshadik.utils.QBEntityCallbackImpl;
import com.saatco.murshadik.utils.SettingsUtil;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.UsersUtils;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.utils.WebRtcSessionManager;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionListener;
import org.webrtc.CameraVideoCapturer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * QuickBlox team
 */
public class CallActivity extends BaseActivity implements IncomeCallFragmentCallbackListener,
        QBRTCSessionStateCallback<QBRTCSession>, QBRTCClientSessionCallbacks,
        ConversationFragmentCallback, ScreenShareFragment.OnSharingEvents {
    private static final String TAG = CallActivity.class.getSimpleName();

    public static final String INCOME_CALL_FRAGMENT = "income_call_fragment";
    public static final int REQUEST_PERMISSION_SETTING = 545;

    private final ArrayList<CurrentCallStateCallback> currentCallStateCallbackList = new ArrayList<>();
    private final QbUsersDbManager dbManager = QbUsersDbManager.getInstance(this);
    private Handler showIncomingCallWindowTaskHandler;
    private ConnectionListenerImpl connectionListener;
    private ServiceConnection callServiceConnection;
    private Runnable showIncomingCallWindowTask;
    private boolean isInComingCall = false;
    private List<Integer> opponentsIdsList;
    private SharedPreferences sharedPref;
    private boolean isVideoCall = false;
    private PermissionsChecker checker;
    private CallService callService;
    private boolean isAccepted;
    private boolean isAnswered;
    private boolean isRejected;
    private static final String EXTRA_PERMISSIONS = "extraPermissions";

//    private AudioConversationFragment audioConversationFragment;


//    private TextView tv_call_state;

    public static void start(Context context, boolean isIncomingCall) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PermissionsActivity.startActivity(context, false, true, Manifest.permission.BLUETOOTH_CONNECT);
            return;
        }
        Intent intent = new Intent(context, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Consts.EXTRA_IS_INCOMING_CALL, isIncomingCall);
        SharedPrefsHelper.getInstance().save(Consts.EXTRA_IS_INCOMING_CALL, isIncomingCall);
        context.startActivity(intent);
        CallService.start(context, isIncomingCall);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });


        checker = new PermissionsChecker(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                return;
            }
        });

        // starting the call service if user has android 12 or newer because of the new google terms
        // that says "you cant start broadcast of service from notification

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PermissionsActivity.startActivity(this, false, true, Manifest.permission.BLUETOOTH_CONNECT);
            return;
        }

        if (getIntent().getBooleanExtra(Consts.EXTRA_IS_INCOMING_CALL, false))
            CallService.start(this, true);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            // For newer than Android Oreo: call setShowWhenLocked, setTurnScreenOn
            setShowWhenLocked(true);
            setTurnScreenOn(true);

            // If you want to display the keyguard to prompt the user to unlock the phone:
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        } else {
            // For older versions, do it as you did before.
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON); //this line been added by amin

        }

        //modify window flags so as to display it on lock screen
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON); //this line been added by amin

        // to wake up screen
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyApp:Call");

        wakeLock.acquire(30 * 60 * 1000L /*10 minutes*/);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Consts.CALL_NOTIFY_ID);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mIncomingCall,
                new IntentFilter("incoming_call"));
    }

    private void initScreen() {
        callService.setCallTimerCallback(new CallTimerCallback());
        isVideoCall = callService.isVideoCall();

        opponentsIdsList = callService.getOpponents();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        initSettingsStrategy();
        addListeners();

        if (getIntent() != null && getIntent().getExtras() != null) {
            isInComingCall = getIntent().getExtras().getBoolean(Consts.EXTRA_IS_INCOMING_CALL, false);
        } else {
            isInComingCall = sharedPrefsHelper.get(Consts.EXTRA_IS_INCOMING_CALL, false);
        }

        if (callService.isCallMode()) {
            checkPermission();
            if (callService.isSharingScreenState()) {
                startScreenSharing(null);
                return;
            }
            addConversationFragment(isInComingCall);
        } else {

            if (!isInComingCall) { //BSM
                Log.v("uuu", "Playeed!!");
                callService.playRingtone();
            } else {
                Log.v("uuu", "Playeed!! xxx");
                if (Util.isPhoneNormal(App.getInstance())) {
                    callService.playDefault();
                    callService.playVibrate();
                } else if (Util.isPhoneVibrate(App.getInstance()))
                    callService.playVibrate();

            }

            startSuitableFragment(isInComingCall);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_POWER:
            case KeyEvent.KEYCODE_HOME:
                if (callService.isRingtonePlaying())
                    callService.stopRingtone();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addListeners() {
        addSessionEventsListener(this);
        addSessionStateListener(this);

        connectionListener = new ConnectionListenerImpl();
        addConnectionListener(connectionListener);
    }

    private void removeListeners() {
        removeSessionEventsListener(this);
        removeSessionStateListener(this);
        removeConnectionListener(connectionListener);
        callService.removeCallTimerCallback();
    }

    private void bindCallService() {
        callServiceConnection = new CallServiceConnection();
        Intent intent = new Intent(this, CallService.class);
        bindService(intent, callServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult requestCode=" + requestCode + ", resultCode= " + resultCode);
        if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
            if (data != null) {
                boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
                if (isLoginSuccess) {
                    initScreen();
                } else {
                    CallService.stop(this);
                    finish();
                }
            }
        }
        if (requestCode == QBRTCScreenCapturer.REQUEST_MEDIA_PROJECTION
                && resultCode == Activity.RESULT_OK && data != null) {
            startScreenSharing(data);
            Log.i(TAG, "Starting Screen Capture");
        }
    }

    private void startScreenSharing(final Intent data) {
        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(ScreenShareFragment.class.getSimpleName());
        if (!(fragmentByTag instanceof ScreenShareFragment)) {
            ScreenShareFragment screenShareFragment = ScreenShareFragment.newInstance();
            FragmentExecuotr.addFragment(getSupportFragmentManager(), R.id.fragment_container, screenShareFragment, ScreenShareFragment.class.getSimpleName());

            if (data != null) {
                callService.startScreenSharing(data);
            }
        }
    }

    private void startSuitableFragment(boolean isInComingCall) {
        QBRTCSession session = WebRtcSessionManager.getInstance(this).getCurrentSession();
        if (session != null) {
            if (isInComingCall) {
                initIncomingCallTask();
                startLoadAbsentUsers();
                addIncomeCallFragment();
                checkPermission();
            } else {
                addConversationFragment(isInComingCall);
                getIntent().removeExtra(Consts.EXTRA_IS_INCOMING_CALL);
                sharedPrefsHelper.save(Consts.EXTRA_IS_INCOMING_CALL, false);
            }
        } else {
            finish();
        }
    }

    private void checkPermission() {
        boolean cam = SharedPrefsHelper.getInstance().get(Consts.PERMISSIONS[0], true);
        boolean mic = SharedPrefsHelper.getInstance().get(Consts.PERMISSIONS[1], true);

        if (isVideoCall && (checker.lacksPermissions(Consts.PERMISSIONS) || checker.checkBluetoothConnectPermission())) {
            if (cam) {
                PermissionsActivity.startActivity(this, false, Consts.PERMISSIONS);
            } else {
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                ErrorUtils.showSnackbar(rootView, R.string.error_permission_video, R.string.dlg_allow, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPermissionSystemSettings();
                    }
                });
            }
        } else if (checker.lacksPermissions(Consts.PERMISSIONS[1]) || checker.checkBluetoothConnectPermission()) {
            if (mic) {
                PermissionsActivity.startActivity(this, true, Consts.PERMISSIONS);
            } else {
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                ErrorUtils.showSnackbar(rootView, R.string.error_permission_audio, R.string.dlg_allow, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPermissionSystemSettings();
                    }
                });
            }
        }
    }

    private void startPermissionSystemSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    private void startLoadAbsentUsers() {

        ArrayList<QBUser> usersFromDb = dbManager.getAllUsers();
        ArrayList<Integer> allParticipantsOfCall = new ArrayList<>();

        if (opponentsIdsList != null) {
            allParticipantsOfCall.addAll(opponentsIdsList);
        }

        if (isInComingCall) {
            Integer callerID = callService.getCallerId();
            if (callerID != null) {
                allParticipantsOfCall.add(callerID);
            }

        }

        ArrayList<Integer> idsUsersNeedLoad = UsersUtils.getIdsNotLoadedUsers(usersFromDb, allParticipantsOfCall);
        if (!idsUsersNeedLoad.isEmpty()) {
            requestExecutor.loadUsersByIds(idsUsersNeedLoad, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                    dbManager.saveAllUsers(users, false);
                    notifyCallStateListenersNeedUpdateOpponentsList(users);
                }
            });
        }
    }

    private void initSettingsStrategy() {
        if (opponentsIdsList != null) {
            SettingsUtil.setSettingsStrategy(opponentsIdsList, sharedPref, this);
        }
    }

    private void initIncomingCallTask() {
        showIncomingCallWindowTaskHandler = new Handler(Looper.myLooper());
        showIncomingCallWindowTask = () -> {
            /*if (callService.currentSessionExist()) {
                BaseSession.QBRTCSessionState currentSessionState = callService.getCurrentSessionState();
                if (QBRTCSession.QBRTCSessionState.QB_RTC_SESSION_NEW.equals(currentSessionState)) {
                    callService.rejectCurrentSession(new HashMap<>());
                } else {
                    callService.stopRingtone();
                    hangUpCurrentSession();
                }
            }*/
            // This is a fix to prevent call stop in case calling to user with more then one device logged in.
            //ToastUtils.longToast("Call was stopped by UserNoActions timer");
            callService.clearCallState();
            callService.clearButtonsState();
            WebRtcSessionManager.getInstance(getApplicationContext()).setCurrentSession(null);
            CallService.stop(CallActivity.this);
            finish();
        };
    }

    public void hangUpCurrentSession() {

        if (isAnswered) {
            CallRequest.updateCallStatus(CallRequest.COMPLETED, isVideoCall ? 2 : 1);

            // if the call is clinic call, update the call duration
            if (SharedPrefsHelper.getInstance().get(Consts.EXTRA_IS_CLINIC_CALL, false)) {
                int appointmentId = SharedPrefsHelper.getInstance().get(Consts.EXTRA_CLINIC_APPOINTMENT_ID, -1);
                ApiMethodsHelper.putCallDurationApi(appointmentId, callService.getCallTimeSeconds());

                SharedPrefsHelper.getInstance().delete(Consts.EXTRA_IS_CLINIC_CALL);
                SharedPrefsHelper.getInstance().delete(Consts.EXTRA_CLINIC_APPOINTMENT_ID);
            }

            // if the call is consultation call, update the call duration
            if (SharedPrefsHelper.getInstance().get(Consts.EXTRA_IS_CONSULTATION_CALL, false)) {
                int appointmentId = SharedPrefsHelper.getInstance().get(Consts.EXTRA_CONSULTATION_APPOINTMENT_ID, -1);
                ApiMethodsHelper.putConsultationCallDurationApi(appointmentId, callService.getCallTimeSeconds());

                SharedPrefsHelper.getInstance().delete(Consts.EXTRA_IS_CONSULTATION_CALL);
                SharedPrefsHelper.getInstance().delete(Consts.EXTRA_CONSULTATION_APPOINTMENT_ID);
            }
        } else {
            CallRequest.updateCallStatus(CallRequest.CANCELLED, isVideoCall ? 2 : 1);
            PushNotificationSender.sendPushMessage((ArrayList<Integer>) opponentsIdsList, "", ProfileHelper.getAccount(CallActivity.this).getFirstName(), "99");

        }

        callService.stopRingtone();

        callService.hangUpCurrentSession(new HashMap<>());
        CallService.stop(this);
        finish();

    }


    private void startIncomeCallTimer(long time) {
        Log.d(TAG, "startIncomeCallTimer");
        showIncomingCallWindowTaskHandler.postAtTime(showIncomingCallWindowTask, SystemClock.uptimeMillis() + time);
    }

    private void stopIncomeCallTimer() {
        Log.d(TAG, "stopIncomeCallTimer");
        showIncomingCallWindowTaskHandler.removeCallbacks(showIncomingCallWindowTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindCallService();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(callServiceConnection);
        if (callService != null) {
            removeListeners();
        }

        PrefUtil.writeBooleanValue(App.getInstance(), "is_call_activity_active", false);

    }

    @Override
    public void finish() {
        //Fix bug when user returns to call from service and the backstack doesn't have any screens
        CallService.stop(this);
        PrefUtil.writeBooleanValue(App.getInstance(), "is_call_activity_active", false);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Consts.CALL_NOTIFY_ID);

        // OpponentsActivity.start(this);
        //Intent intent = new Intent(this,MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(intent);

        super.finish();
    }


    private void addIncomeCallFragment() {
        Log.d(TAG, "Adding IncomeCallFragment");
        if (callService.currentSessionExist()) {
            IncomeCallFragment fragment = new IncomeCallFragment();
            FragmentExecuotr.addFragment(getSupportFragmentManager(), R.id.fragment_container, fragment, INCOME_CALL_FRAGMENT);
        } else {
            Log.d(TAG, "SKIP Adding IncomeCallFragment");
        }
    }

    private void addConversationFragment(boolean isIncomingCall) {
        BaseConversationFragment conversationFragment = BaseConversationFragment.newInstance(
                isVideoCall
                        ? new VideoConversationFragment()
                        : new AudioConversationFragment(),
                isIncomingCall);
        FragmentExecuotr.addFragment(getSupportFragmentManager(), R.id.fragment_container, conversationFragment, conversationFragment.getClass().getSimpleName());
    }

    private void showNotificationPopUp(final int text, final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout connectionView = (LinearLayout) View.inflate(CallActivity.this, R.layout.connection_popup, null);
                if (show) {
                    ((TextView) connectionView.findViewById(R.id.notification)).setText(text);
                    if (connectionView.getParent() == null) {
                        ((ViewGroup) CallActivity.this.findViewById(R.id.fragment_container)).addView(connectionView);
                    }
                } else {
                    ((ViewGroup) CallActivity.this.findViewById(R.id.fragment_container)).removeView(connectionView);
                }
            }
        });
    }

    ////////////////////////////// ConnectionListener //////////////////////////////
    private class ConnectionListenerImpl extends AbstractConnectionListener {
        @Override
        public void connectionClosedOnError(Exception e) {
            showNotificationPopUp(R.string.connection_was_lost, true);
        }

        @Override
        public void reconnectionSuccessful() {
            showNotificationPopUp(R.string.connection_was_lost, false);
        }
    }

    ////////////////////////////// QBRTCSessionStateCallbackListener ///////////////////////////
    @Override
    public void onDisconnectedFromUser(QBRTCSession session, Integer userID) {
        Log.d(TAG, "Disconnected from user: " + userID);

    }

    @Override
    public void onConnectedToUser(QBRTCSession session, final Integer userID) {
        notifyCallStateListenersCallStarted();
        if (isInComingCall) {
            stopIncomeCallTimer();
        }

       /* if(!isInComingCall){
            isAnswered = true;
            CallRequest.updateCallStatus(CallRequest.ACCEPTED,isVideoCall ? 2 : 1);
        }*/

        Log.d(TAG, "onConnectedToUser() is started");
    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession session, Integer userID) {
    }

    @Override
    public void onStateChanged(QBRTCSession qbrtcSession, BaseSession.QBRTCSessionState qbrtcSessionState) {
        if (!isInComingCall && qbrtcSessionState == BaseSession.QBRTCSessionState.QB_RTC_SESSION_CONNECTING) {
            PrefUtil.writePreferenceValue(getApplicationContext(), "CALL_ID", qbrtcSession.getSessionID());
            CallRequest.updateCallStatus(CallRequest.INITIATED, isVideoCall ? 2 : 1);
        }
    }

    ////////////////////////////// QBRTCClientSessionCallbacks //////////////////////////////
    @Override
    public void onUserNotAnswer(QBRTCSession session, Integer userID) {
        if (callService.isCurrentSession(session)) {
            callService.stopRingtone();
        }

        if (!isInComingCall) {
            CallRequest.updateCallStatus(CallRequest.NO_ANSWER, isVideoCall ? 2 : 1);
            PushNotificationSender.sendPushMessage((ArrayList<Integer>) opponentsIdsList, session.getSessionID(), ProfileHelper.getAccount(CallActivity.this).getFirstName(), "99");
        }
    }

    @Override
    public void onSessionStartClose(final QBRTCSession session) {

        Log.v(TAG, "onSessionStartClose");
        if (callService.isCurrentSession(session)) {
            callService.removeSessionStateListener(this);
            notifyCallStateListenersCallStopped();
        }
    }

    @Override
    public void onReceiveHangUpFromUser(final QBRTCSession session, final Integer userID, Map<String, String> map) {


        if (callService.isCurrentSession(session)) {
            if (userID.equals(session.getCallerID())) {
                hangUpCurrentSession();
                Log.v(TAG, "Initiator hung up the call");
            }
            QBUser participant = dbManager.getUserById(userID);
            final String participantName = participant != null ? participant.getFullName() : String.valueOf(userID);
            //  ToastUtils.shortToast("User " + participantName + " " + getString(R.string.text_status_hang_up) + " conversation");

        }

        if (!isInComingCall) {
            if (isAnswered)
                CallRequest.updateCallStatus(CallRequest.COMPLETED, isVideoCall ? 2 : 1);
            else {
                CallRequest.updateCallStatus(CallRequest.CANCELLED, isVideoCall ? 2 : 1);
            }
        }

        if (ProfileHelper.getAccount(getApplicationContext()).getRoleId() == 5) {
            Intent intent = new Intent(CallActivity.this, RatingActivity.class);
            intent.putExtra("consultant_id", PrefUtil.getInteger(App.getInstance(), "USER_ID"));
            startActivity(intent);

            finish();
        }

    }

    @Override
    public void onChangeReconnectionState(QBRTCSession qbrtcSession, Integer integer, QBRTCTypes.QBRTCReconnectionState qbrtcReconnectionState) {

    }

    @Override
    public void onCallAcceptByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo) {
        if (callService.isCurrentSession(session)) {
            callService.stopRingtone();
        }

        if (!isInComingCall) {
            isAccepted = true;
            isAnswered = true;
            CallRequest.updateCallStatus(CallRequest.ACCEPTED, isVideoCall ? 2 : 1);
        }
    }

    @Override
    public void onReceiveNewSession(final QBRTCSession session) {
        Log.v(TAG, "Session " + session.getSessionID() + " Received");

        if (!isInComingCall) {
            //    PrefUtil.writePreferenceValue(getApplicationContext(),"CALL_ID",session.getSessionID());
            //   CallRequest.updateCallStatus(CallRequest.INITIATED,isVideoCall ? 2 : 1);
        }

    }


    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {
        startIncomeCallTimer(0);

        if (!isInComingCall) {
            CallRequest.updateCallStatus(CallRequest.BUSY, isVideoCall ? 2 : 1);
        }
    }

    @Override
    public void onSessionClosed(final QBRTCSession session) {
        if (callService.isCurrentSession(session)) {
            Log.d(TAG, "Stopping session");
            Log.v("xWx", "onSessionClosed");
            callService.stopForeground(true);
            if (ProfileHelper.getAccount(getApplicationContext()).getRoleId() == 5 && isAccepted) {
                Intent intent = new Intent(CallActivity.this, RatingActivity.class);
                intent.putExtra("consultant_id", PrefUtil.getInteger(App.getInstance(), "USER_ID"));
                startActivity(intent);
            }

            finish();
        }
    }

    @Override
    public void onCallRejectByUser(QBRTCSession session, Integer userID, Map<String, String> userInfo) {
        if (callService.isCurrentSession(session)) {
            callService.stopRingtone();
        }

        if (!isInComingCall && !isAnswered) {
            isRejected = true;
            CallRequest.updateCallStatus(CallRequest.REJECTED, isVideoCall ? 2 : 1);
        }
    }

    ////////////////////////////// IncomeCallFragmentCallbackListener ////////////////////////////
    @Override
    public void onAcceptCurrentSession() {
        if (callService.currentSessionExist()) {
            addConversationFragment(true);
        } else {
            Log.d(TAG, "SKIP addConversationFragment method");
        }

        isAccepted = true;
    }

    @Override
    public void onRejectCurrentSession() {
        callService.rejectCurrentSession(new HashMap<>());
    }

    ////////////////////////////// ConversationFragmentCallback ////////////////////////////
    @Override
    public void addConnectionListener(ConnectionListener connectionCallback) {
        callService.addConnectionListener(connectionCallback);
    }

    @Override
    public void removeConnectionListener(ConnectionListener connectionCallback) {
        callService.removeConnectionListener(connectionCallback);
    }

    @Override
    public void addSessionStateListener(QBRTCSessionStateCallback clientConnectionCallbacks) {
        callService.addSessionStateListener(clientConnectionCallbacks);
    }

    @Override
    public void addSessionEventsListener(QBRTCSessionEventsCallback eventsCallback) {
        callService.addSessionEventsListener(eventsCallback);
    }

    @Override
    public void onSetAudioEnabled(boolean isAudioEnabled) {
        callService.setAudioEnabled(isAudioEnabled);
    }

    @Override
    public void onHangUpCurrentSession() {
        hangUpCurrentSession();
    }

    @TargetApi(21)
    @Override
    public void onStartScreenSharing() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        QBRTCScreenCapturer.requestPermissions(this);
    }

    @Override
    public void onSwitchCamera(CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler) {
        callService.switchCamera(cameraSwitchHandler);
    }

    @Override
    public void onSetVideoEnabled(boolean isNeedEnableCam) {
        callService.setVideoEnabled(isNeedEnableCam);
    }

    @Override
    public void onSwitchAudio() {
        callService.switchAudio();
    }

    @Override
    public void removeSessionStateListener(QBRTCSessionStateCallback clientConnectionCallbacks) {
        callService.removeSessionStateListener(clientConnectionCallbacks);
    }

    @Override
    public void removeSessionEventsListener(QBRTCSessionEventsCallback eventsCallback) {
        callService.removeSessionEventsListener(eventsCallback);
    }

    @Override
    public void addCurrentCallStateListener(CurrentCallStateCallback currentCallStateCallback) {
        if (currentCallStateCallback != null) {
            currentCallStateCallbackList.add(currentCallStateCallback);
        }
    }

    @Override
    public void removeCurrentCallStateListener(CurrentCallStateCallback currentCallStateCallback) {
        currentCallStateCallbackList.remove(currentCallStateCallback);
    }

    @Override
    public void addOnChangeAudioDeviceListener(OnChangeAudioDevice onChangeDynamicCallback) {

    }

    @Override
    public void removeOnChangeAudioDeviceListener(OnChangeAudioDevice onChangeDynamicCallback) {

    }

    @Override
    public void acceptCall(Map<String, String> userInfo) {
        callService.acceptCall(userInfo);
    }

    @Override
    public void startCall(Map<String, String> userInfo) {
        Map<String, String> info = new HashMap<>();
        info.put("name", ProfileHelper.getAccount(getApplicationContext()).getChatId());
        callService.startCall(info);
    }

    @Override
    public boolean currentSessionExist() {
        return callService.currentSessionExist();
    }

    @Override
    public List<Integer> getOpponents() {
        return callService.getOpponents();
    }

    @Override
    public Integer getCallerId() {
        return callService.getCallerId();
    }

    @Override
    public void addVideoTrackListener(QBRTCClientVideoTracksCallbacks<QBRTCSession> callback) {
        callService.addVideoTrackListener(callback);
    }

    @Override
    public void removeVideoTrackListener(QBRTCClientVideoTracksCallbacks<QBRTCSession> callback) {
        callService.removeVideoTrackListener(callback);
    }

    @Override
    public BaseSession.QBRTCSessionState getCurrentSessionState() {
        return callService.getCurrentSessionState();
    }

    @Override
    public QBRTCTypes.QBRTCConnectionState getPeerChannel(Integer userId) {
        return callService.getPeerChannel(userId);
    }

    @Override
    public boolean isMediaStreamManagerExist() {
        return callService.isMediaStreamManagerExist();
    }

    @Override
    public boolean isCallState() {
        return callService.isCallMode();
    }

    @Override
    public HashMap<Integer, QBRTCVideoTrack> getVideoTrackMap() {
        return callService.getVideoTrackMap();
    }

    @Override
    public QBRTCVideoTrack getVideoTrack(Integer userId) {
        return callService.getVideoTrack(userId);
    }

    @Override
    public void onStopPreview() {
        callService.stopScreenSharing();
        addConversationFragment(false);
    }


    private void notifyCallStateListenersCallStarted() {
        for (CurrentCallStateCallback callback : currentCallStateCallbackList) {
            callback.onCallStarted();
        }
    }

    private void notifyCallStateListenersCallStopped() {
        for (CurrentCallStateCallback callback : currentCallStateCallbackList) {
            callback.onCallStopped();
        }
    }

    private void notifyCallStateListenersNeedUpdateOpponentsList(final ArrayList<QBUser> newUsers) {
        for (CurrentCallStateCallback callback : currentCallStateCallbackList) {
            callback.onOpponentsListUpdated(newUsers);
        }
    }

    private void notifyCallStateListenersCallTime(String callTime) {
        for (CurrentCallStateCallback callback : currentCallStateCallbackList) {
            callback.onCallTimeUpdate(callTime);
        }
    }

    private class CallServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CallService.CallServiceBinder binder = (CallService.CallServiceBinder) service;
            callService = binder.getService();
            if (callService.currentSessionExist()) {
                //we have already currentSession == null, so it's no reason to do further initialization
                if (QBChatService.getInstance().isLoggedIn()) {
                    initScreen();
                } else {
                    login();
                }
            } else {
                finish();
            }
        }

        private void login() {
            QBUser qbUser = SharedPrefsHelper.getInstance().getQbUser();
            Intent tempIntent = new Intent(CallActivity.this, LoginService.class);
            PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
            LoginService.start(CallActivity.this, qbUser, pendingIntent);
        }
    }

    private class CallTimerCallback implements CallService.CallTimerListener {
        @Override
        public void onCallTimeUpdate(String time) {
            runOnUiThread(() -> notifyCallStateListenersCallTime(time));
        }
    }

    public interface OnChangeAudioDevice {
        void audioDeviceChanged(QBAudioManager.AudioDevice newAudioDevice);
    }

    private final BroadcastReceiver mIncomingCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hangUpCurrentSession();
        }
    };

    public interface CurrentCallStateCallback {
        void onCallStarted();

        void onCallStopped();

        void onOpponentsListUpdated(ArrayList<QBUser> newUsers);

        void onCallTimeUpdate(String time);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mIncomingCall);

    }
}
