package com.saatco.murshadik.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saatco.murshadik.ChatUserActivity;
import com.saatco.murshadik.GroupChatActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.ISearchGroups;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.GroupAdapter;
import com.saatco.murshadik.model.GroupChat;

import java.util.ArrayList;

public class GroupFragment extends Fragment implements GroupAdapter.OnSelectItemClickListener, ISearchGroups {

    View rootView;

    ArrayList<GroupChat> groups = new ArrayList<>();
    RecyclerView recyclerView;
    GroupAdapter adapter;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    public GroupFragment() {
        // Required empty public constructor
    }


    public static GroupFragment newInstance(Bundle bundle) {
        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group, container, false);

        setDetails();

        return rootView;
    }



    private void setDetails(){

        //********************* add user region as group *******************//
        GroupChat groupChat = new GroupChat();
        groupChat.setName(ProfileHelper.getAccount(getContext()).getLocation());

        groups.add(groupChat);

        recyclerView = rootView.findViewById(R.id.rv_groups);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new GroupAdapter(groups,getContext(),this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onGroupClick(View view, int position, GroupChat group) {

        Intent intent = new Intent(getActivity(), GroupChatActivity.class);
        intent.putExtra("GROUP",group);
        startActivity(intent);
    }

    @Override
    public void onSearchGroups(String search) {

        filter(search);

    }

    void filter(String text){
        ArrayList<GroupChat> temp = new ArrayList();

        for(GroupChat d: groups){
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }

        //update recyclerview
        if(recyclerView != null) {
            ((GroupAdapter) recyclerView.getAdapter()).updateList(temp);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Your callback initialization here
        ((ChatUserActivity) getActivity()).iSearchGroups = this;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Your callback initialization here
        ((ChatUserActivity) getActivity()).iSearchGroups = this;

    }

}
