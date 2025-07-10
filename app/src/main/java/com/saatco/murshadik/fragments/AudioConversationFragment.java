package com.saatco.murshadik.fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.audio.QBAudioManager;
import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.utils.CollectionsUtils;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.UiUtils;

import java.util.ArrayList;
import java.util.Objects;

import androidx.core.content.ContextCompat;


public class AudioConversationFragment extends BaseConversationFragment implements CallActivity.OnChangeAudioDevice {
    private static final String TAG = AudioConversationFragment.class.getSimpleName();

    public static final String SPEAKER_ENABLED = "is_speaker_enabled";

    private ToggleButton audioSwitchToggleButton;
    private TextView alsoOnCallText;
    private TextView firstOpponentNameTextView;
    private TextView otherOpponentsTextView;

    @Override
    public void onStart() {
        super.onStart();
        if (conversationFragmentCallback != null) {
            conversationFragmentCallback.addOnChangeAudioDeviceListener(this);
        }
    }

    @Override
    protected void configureOutgoingScreen() {
        if (getActivity() == null) return;
        outgoingOpponentsRelativeLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white));
        allOpponentsTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
        ringingTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
    }

    @Override
    protected void configureToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white));
        toolbar.setTitleTextColor(ContextCompat.getColor(requireActivity(), R.color.toolbar_title_color));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(requireActivity(), R.color.toolbar_subtitle_color));
    }

    @Override
    protected void configureActionBar() {
        actionBar.setSubtitle(String.format(getString(R.string.subtitle_text_logged_in_as), currentUser.getFullName()));
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);

        timerCallText = view.findViewById(R.id.timer_call);
        ImageView firstOpponentAvatarImageView = view.findViewById(R.id.image_caller_avatar);
        firstOpponentAvatarImageView.setBackground(UiUtils.getColorCircleDrawable(opponents.get(0).getId()));

        alsoOnCallText = view.findViewById(R.id.text_also_on_call);
        setVisibilityAlsoOnCallTextView();

        firstOpponentNameTextView = view.findViewById(R.id.text_caller_name);
        String userName = opponents.get(0).getFullName();

        /*ArrayList<User> consultants = StorageHelper.getConsultants() != null ? StorageHelper.getConsultants() : new ArrayList<>();
        ArrayList<User> farmers = StorageHelper.getAllFarmers() != null ? StorageHelper.getAllFarmers() : new ArrayList<>();

        if(farmers.size() > 0)
            consultants.addAll(farmers);

        for(User user : consultants) {
            if (user.getChatId() == opponents.get(0).getId()) {
                userName = user.getFullname();
                break;
            }
        }*/

        firstOpponentNameTextView.setText(userName);
        otherOpponentsTextView = (TextView) view.findViewById(R.id.text_other_inc_users);
        otherOpponentsTextView.setText(getOtherOpponentsNames());

        allOpponentsTextView.setText(userName);

        audioSwitchToggleButton = view.findViewById(R.id.toggle_speaker);
        audioSwitchToggleButton.setVisibility(View.VISIBLE);
        audioSwitchToggleButton.setChecked(SharedPrefsHelper.getInstance().get(SPEAKER_ENABLED, true));

        actionButtonsEnabled(false);

        if (conversationFragmentCallback != null && conversationFragmentCallback.isCallState()) {
            onCallStarted();
        }
    }

    private void setVisibilityAlsoOnCallTextView() {
        if (opponents.size() < 2) {
            alsoOnCallText.setVisibility(View.INVISIBLE);
        }
    }

    private String getOtherOpponentsNames() {
        ArrayList<QBUser> otherOpponents = new ArrayList<>(opponents);
        otherOpponents.remove(0);
        return CollectionsUtils.makeStringFromUsersFullNames(otherOpponents);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (conversationFragmentCallback != null) {
            conversationFragmentCallback.removeOnChangeAudioDeviceListener(this);
        }
    }

    @Override
    protected void initButtonsListener() {
        super.initButtonsListener();
        audioSwitchToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPrefsHelper.getInstance().save(SPEAKER_ENABLED, isChecked);
            if (conversationFragmentCallback != null) {
                conversationFragmentCallback.onSwitchAudio();
            }
        });
    }

    @Override
    protected void actionButtonsEnabled(boolean enabled) {
        super.actionButtonsEnabled(enabled);
        audioSwitchToggleButton.setActivated(enabled);
    }

    @Override
    int getFragmentLayout() {
        return R.layout.fragment_audio_conversation;
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        super.onOpponentsListUpdated(newUsers);
        firstOpponentNameTextView.setText(opponents.get(0).getFullName());
        otherOpponentsTextView.setText(opponents.get(0).getFullName());
    }

    @Override
    public void onCallTimeUpdate(String time) {
        timerCallText.setText(time);
    }

    @Override
    public void audioDeviceChanged(QBAudioManager.AudioDevice newAudioDevice) {
        audioSwitchToggleButton.setChecked(newAudioDevice != QBAudioManager.AudioDevice.SPEAKER_PHONE);
    }

    // **** creating method to change the call state
    /**
     * setting call state for text view in the view-outgoing-screen
     * @param text the text that will show on the text view of this fragment*/
    public void setCallStateTV(String text){
        this.ringingTextView.setText(text);
    }
}