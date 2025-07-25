package com.saatco.murshadik.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.quickblox.chat.QBChatService;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.QbUsersDbManager;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.fcm.CallService;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.UsersUtils;
import com.saatco.murshadik.utils.WebRtcSessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public abstract class BaseConversationFragment extends BaseToolBarFragment implements CallActivity.CurrentCallStateCallback {
    private static final String TAG = BaseConversationFragment.class.getSimpleName();

    public static final String MIC_ENABLED = "is_microphone_enabled";
    protected QbUsersDbManager dbManager;
    protected WebRtcSessionManager sessionManager;

    private boolean isIncomingCall;
    protected TextView timerCallText;
    protected ConversationFragmentCallback conversationFragmentCallback;
    protected QBUser currentUser;
    protected ArrayList<QBUser> opponents;
    protected boolean isStarted;

    private ToggleButton micToggleVideoCall;
    private ImageButton handUpVideoCall;
    protected View outgoingOpponentsRelativeLayout;
    protected TextView allOpponentsTextView;
    protected TextView ringingTextView;
    protected VideoView video_view_loading;

    public static BaseConversationFragment newInstance(BaseConversationFragment baseConversationFragment, boolean isIncomingCall) {
        Log.d(TAG, "isIncomingCall =  " + isIncomingCall);
        Bundle args = new Bundle();
        args.putBoolean(Consts.EXTRA_IS_INCOMING_CALL, isIncomingCall);
        baseConversationFragment.setArguments(args);
        return baseConversationFragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            conversationFragmentCallback = (ConversationFragmentCallback) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity
                    + " must implement ConversationFragmentCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (conversationFragmentCallback != null) {
            conversationFragmentCallback.addCurrentCallStateListener(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initFields();
        initViews(view);
        initActionBar();
        initButtonsListener();
        prepareAndShowOutgoingScreen();

        return view;
    }

    private void initActionBar() {
        configureToolbar();
        configureActionBar();

        toolbar.setVisibility(View.GONE);
        actionBar.hide();
    }

    private void prepareAndShowOutgoingScreen() {
        configureOutgoingScreen();
       //BSM allOpponentsTextView.setText(CollectionsUtils.makeStringFromUsersFullNames(opponents));
        String name = PrefUtil.getStringPref(getContext(),"call_user_name");
        allOpponentsTextView.setText(name);
    }

    protected abstract void configureOutgoingScreen();

    protected abstract void configureActionBar();

    protected abstract void configureToolbar();

    protected void initFields() {
        dbManager = QbUsersDbManager.getInstance(getActivity().getApplicationContext());
        sessionManager = WebRtcSessionManager.getInstance(getActivity());
        currentUser = QBChatService.getInstance().getUser();
        if (currentUser == null) {
            currentUser = SharedPrefsHelper.getInstance().getQbUser();
        }

        if (getArguments() != null) {
            isIncomingCall = getArguments().getBoolean(Consts.EXTRA_IS_INCOMING_CALL, false);
        }
        initOpponentsList();
        Log.d(TAG, "Opponents: " + opponents.toString());
    }

    @Override
    public void onStart() {
        super.onStart();

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("name", ProfileHelper.getAccount(getContext()).getChatId());

        if (conversationFragmentCallback != null) {
            if (isIncomingCall) {
                conversationFragmentCallback.acceptCall(userInfo);
            } else {
                conversationFragmentCallback.startCall(userInfo);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (conversationFragmentCallback != null) {
            conversationFragmentCallback.removeCurrentCallStateListener(this);
        }
        super.onDestroy();
    }

    protected void initViews(View view) {
        micToggleVideoCall = (ToggleButton) view.findViewById(R.id.toggle_mic);
        micToggleVideoCall.setChecked(SharedPrefsHelper.getInstance().get(MIC_ENABLED, true));
        handUpVideoCall = (ImageButton) view.findViewById(R.id.button_hangup_call);
        outgoingOpponentsRelativeLayout = view.findViewById(R.id.layout_background_outgoing_screen);
        allOpponentsTextView = view.findViewById(R.id.text_outgoing_opponents_names);
        ringingTextView = view.findViewById(R.id.text_ringing);
        video_view_loading = view.findViewById(R.id.video_view_loading);

//        initLoadingVideos();

        if (isIncomingCall) {
            hideOutgoingScreen();
        }
    }

    private void initLoadingVideos(){
        video_view_loading.setVideoURI(Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.intro_loading));
        video_view_loading.start();
        video_view_loading.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!mp.isPlaying()){
                    video_view_loading.setVideoURI(Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.loading));
                    video_view_loading.start();
                }
                if (outgoingOpponentsRelativeLayout.getVisibility() == View.GONE)
                {
                    video_view_loading.stopPlayback();
                    mp.release();
                }
            }
        });

    }

    protected void initButtonsListener() {
        micToggleVideoCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefsHelper.getInstance().save(MIC_ENABLED, isChecked);
                if (conversationFragmentCallback != null) {
                    conversationFragmentCallback.onSetAudioEnabled(isChecked);
                }
            }
        });

        handUpVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonsEnabled(false);
                handUpVideoCall.setEnabled(false);
                handUpVideoCall.setActivated(false);
                CallService.stop(getContext());

                if (conversationFragmentCallback != null) {
                    conversationFragmentCallback.onHangUpCurrentSession();
                }
                Log.d(TAG, "Call is Stopped");
            }
        });
    }

    private void clearButtonsState() {
        SharedPrefsHelper.getInstance().delete(MIC_ENABLED);
        SharedPrefsHelper.getInstance().delete(AudioConversationFragment.SPEAKER_ENABLED);
        SharedPrefsHelper.getInstance().delete(VideoConversationFragment.CAMERA_ENABLED);
    }

    protected void actionButtonsEnabled(boolean enabled) {
        micToggleVideoCall.setEnabled(enabled);
        micToggleVideoCall.setActivated(enabled);
    }

    private void startTimer() {
        if (!isStarted) {

            if (Build.VERSION.SDK_INT > 23) {
                 timerCallText.setVisibility(View.VISIBLE);
            }

                toolbar.setVisibility(View.GONE);
                actionBar.hide();
                isStarted = true;
        }
    }

    private void hideOutgoingScreen() {
        outgoingOpponentsRelativeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onCallStarted() {
        hideOutgoingScreen();
        startTimer();
        actionButtonsEnabled(true);
        allOpponentsTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_outgoing_opponents_names_audio_call));
        ringingTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_call_type));
    }

    @Override
    public void onCallStopped() {
        CallService.stop(getContext());
        isStarted = false;
        clearButtonsState();
        actionButtonsEnabled(false);
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        initOpponentsList();
    }

    private void initOpponentsList() {
        Log.v(TAG, "initOpponentsList()");
        if (conversationFragmentCallback != null) {
            List<Integer> opponentsIds = conversationFragmentCallback.getOpponents();

            if (opponentsIds != null) {
                ArrayList<QBUser> usersFromDb = dbManager.getUsersByIds(opponentsIds);
                opponents = UsersUtils.getListAllUsersFromIds(usersFromDb, opponentsIds);
            }

            QBUser caller = dbManager.getUserById(conversationFragmentCallback.getCallerId());
            if (caller == null) {
                caller = new QBUser(conversationFragmentCallback.getCallerId());
                caller.setFullName(String.valueOf(conversationFragmentCallback.getCallerId()));
            }

            if (isIncomingCall) {
                opponents.add(caller);
                opponents.remove(QBChatService.getInstance().getUser());
            }
        }
    }

    public QBRTCTypes.QBRTCConnectionState getConnectionState(Integer userID) {
        QBRTCTypes.QBRTCConnectionState result = null;
        if (conversationFragmentCallback != null) {
            result = conversationFragmentCallback.getPeerChannel(userID);
        }
        return result;
    }
}