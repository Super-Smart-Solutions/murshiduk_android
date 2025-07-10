package com.saatco.murshadik.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saatco.ItemOffsetDecoration;
import com.saatco.murshadik.CategoryChildActivity;
import com.saatco.murshadik.ChatActivityNew;
import com.saatco.murshadik.ChatUserActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.IChatCategoryOnline;
import com.saatco.murshadik.ISearchChat;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.SelectUserActivity;
import com.saatco.murshadik.adapters.ChatCategoryAdapter;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class CategoryFragment extends Fragment implements ChatCategoryAdapter.OnSelectItemClickListener , ISearchChat , IChatCategoryOnline {

    View rootView;
    ArrayList<Item> categories = new ArrayList<>();
    RecyclerView recyclerView;
    ChatCategoryAdapter adapter;
    ArrayList<User> sortedConsultants = new ArrayList<User>();
    ArrayList<User> consultants = new ArrayList<>();

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(Bundle bundle) {
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart(){
        super.onStart();


    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_category, container, false);

        Gson gson = new Gson();
        String json = PrefUtil.getStringPref(getContext(),"CATEGORY_CONSULTANT");
        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        categories = gson.fromJson(json, type);


        recyclerView = rootView.findViewById(R.id.rv_category);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        if(categories != null) {
            adapter = new ChatCategoryAdapter(categories, getContext(), this);
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);



        return rootView;
    }


    @Override
    public void onCategoryClick(View view, int position, Item item) {


        User user = new User();
        user.setPhoneNumber("966591794404");
        user.setName(item.getNameAr());

        if(item.getChildrens().size() > 0) {
            Intent intent = new Intent(getActivity(), CategoryChildActivity.class);
            intent.putExtra("CHILDREN", item.getChildrens());
            intent.putExtra("NAME", item.getNameAr());
            startActivity(intent);
        }else{

            /*ArrayList<User> consultantsByCategory = new ArrayList<>();

            for(User consultant : sortedConsultants){
               // if(item.getNameAr().contains(consultant.getStatus().replace(",","")))
                //   consultantsByCategory.add(consultant);
                if(consultant.getStatus() != null) {
                    String[] items = consultant.getStatus().split("\\s*,\\s*");
                    for (String skill : items) {
                        if (item.getNameAr().equals(skill))
                            consultantsByCategory.add(consultant);
                    }
                }
            }*/

            Intent intent = new Intent(getActivity(), SelectUserActivity.class);
            intent.putExtra("is_sorted",true);
            intent.putExtra("skill",item.getNameAr());
            startActivity(intent);
        }

    }

    @Override
    public void onUserClick(View view, int position, User item) {

        Intent intent = new Intent(getContext(), ChatActivityNew.class);
        intent.putExtra("USER",item);
        startActivity(intent);
    }

    @Override
    public void onSearchChat(String search) {

        if(search.equals(""))
            ((ChatCategoryAdapter)recyclerView.getAdapter()).updateList(categories);
        else
             filter(search);

    }

    void filter(String text){


        Set<Item> itemSet = new HashSet<Item>();

        for(User d: sortedConsultants){
            if (d.getFullname().toLowerCase().contains(text.toLowerCase()) || d.getStatus().toLowerCase().contains(text.toLowerCase()) && !d.getObjectId().equals(ProfileHelper.getAccount(getActivity()).getUdid())) {
                Item item = new Item();
                item.setUser(true);
                item.setUser(d);
                itemSet.add(item);
            }
        }

        ArrayList<Item> temp = new ArrayList(itemSet);

        //update recyclerview
        ((ChatCategoryAdapter)recyclerView.getAdapter()).updateList(temp);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Your callback initialization here
        ((ChatUserActivity) getActivity()).setActivityListener(this);
        ((ChatUserActivity) getActivity()).setActivityOnlineOfflineCategoryListener(this);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Your callback initialization here
        ((ChatUserActivity) getActivity()).setActivityListener(this);
        ((ChatUserActivity) getActivity()).setActivityOnlineOfflineCategoryListener(this);

    }

    @Override
    public void onOnlineList(Collection<Integer> onlineIds) {

    }

    @Override
    public void onJoinUser(Integer chatId) {

    }

    @Override
    public void onLeaveUser(Integer chatId) {

    }

    private void getOnlineUsers() {

    }

}
