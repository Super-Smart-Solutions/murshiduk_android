package com.saatco.murshadik.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.saatco.murshadik.R;

import androidx.fragment.app.Fragment;


public class MessagingFragment extends Fragment {//MesiboMessagingFragment {


    public MessagingFragment() {
        // Required empty public constructor
    }

    public static MessagingFragment newInstance() {
        MessagingFragment fragment = new MessagingFragment();
        Bundle bl = new Bundle();
      //  bl.putString(MesiboUI.PEER, "+15675551234");
      //  bl.putLong(MesiboUI.GROUP_ID, 0);
        fragment.setArguments(bl);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messaging, container, false);
    }

}
