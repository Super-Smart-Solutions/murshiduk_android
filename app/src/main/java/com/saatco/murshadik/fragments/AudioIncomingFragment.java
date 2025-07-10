package com.saatco.murshadik.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

//import com.mesibo.api.Mesibo;
//import com.mesibo.calls.MesiboIncomingAudioCallFragment;
import com.saatco.murshadik.R;

import androidx.fragment.app.Fragment;

//import static com.mesibo.api.Mesibo.CALLSTATUS_COMPLETE;


public class AudioIncomingFragment extends Fragment{//MesiboIncomingAudioCallFragment implements Mesibo.CallListener {

    ImageButton btnAccept,btnReject;
    TextView tvName,tvNumber,tvTitle,tvLocation;
    ImageView photo;

   // protected Mesibo.UserProfile userProfile;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);

        rootView = view;

       /*  tvName = view.findViewById(R.id.incoming_name);
         tvNumber = view.findViewById(R.id.incoming_phone_number);
         photo = view.findViewById(R.id.photo_image);
         tvTitle = view.findViewById(R.id.tvVideCall);
         tvLocation = view.findViewById(R.id.incoming_location);

        btnAccept = view.findViewById(R.id.incoming_call_connect);
        btnReject = view.findViewById(R.id.incoming_call_disconnect);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callAnswer();
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callHangup();
            }
        });

       // setProfile(userProfile);

        tvName.setText(userProfile.name);
        tvTitle.setText("Audio Call");
        tvLocation.setText("");
        tvNumber.setText(userProfile.address);
        tvTitle.setVisibility(View.GONE);

        Glide.with(getActivity()).load(userProfile.picturePath).into(photo);*/

        return view;
    }

    /*

    public void setProfile(Mesibo.UserProfile userProfile){

        this.userProfile = userProfile;

    }

    // Hangup handler
    public void callHangup() {
        hangup(); // call hangup from the super class
        getActivity().finish();
    }

    public void callAnswer() {
        answer(); // call answer from the super class
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

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public boolean Mesibo_onCall(long peerid, long callid, Mesibo.UserProfile profile, int flags) {
        return true;
    }

    @Override
    public boolean Mesibo_onCallStatus(long peerid, long callid, int status, int flags, String desc) {

        Log.v("fxxx","Call completed!" + status);
        if(status == CALLSTATUS_COMPLETE){
            Log.v("fxxx","Call completed!");
        }
        return true;
    }

    @Override
    public void Mesibo_onCallServer(int type, String url, String username, String credential) {

    }

    private void showRatingDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_rating);
        dialog.show();
    }

     */
}

