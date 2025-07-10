package com.saatco.murshadik.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.mesibo.api.Mesibo;
//import com.mesibo.calls.MesiboVideoCallFragment;
import com.saatco.murshadik.R;

import androidx.fragment.app.Fragment;


public class VideoCallFragment extends Fragment {//MesiboVideoCallFragment implements Mesibo.CallListener, View.OnTouchListener {

    //protected Mesibo.UserProfile mProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View controlView = inflater.inflate(R.layout.fragment_messaging, container, false);

      /*  ImageButton mAcceptViewButton = controlView.findViewById(R.id.incoming_call_connect);
        ImageButton mDeclineViewButton = controlView.findViewById(R.id.incoming_call_disconnect);

        TextView tvName = controlView.findViewById(R.id.call_name);

        mDeclineViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangup();
            }
        });

        mAcceptViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer(true);
            }
        });

        tvName.setText(mProfile.name);*/

        return controlView;
    }

    /*
    public void setProfile(Mesibo.UserProfile profile){

        mProfile = profile;

    }

    @Override
    public void onResume() {
        super.onResume();
        Mesibo.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Mesibo.removeListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public boolean Mesibo_onCall(long peerid, long callid, Mesibo.UserProfile userProfile, int i) {
        return false;
    }

    @Override
    public boolean Mesibo_onCallStatus(long peerid, long callid, int status, int flags, String desc) {

        return false;
    }

    @Override
    public void Mesibo_onCallServer(int type, String url, String username, String credential) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }*/
}
