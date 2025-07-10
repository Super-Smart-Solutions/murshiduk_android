package com.saatco.murshadik.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.connections.tcp.QBTcpChatConnectionFabric;
import com.quickblox.chat.connections.tcp.QBTcpConfigurationBuilder;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.saatco.murshadik.ChatActivityNew;
import com.saatco.murshadik.Helpers.ChatHelper;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.IChatUserOnline;
import com.saatco.murshadik.ISearchChatUsers;
import com.saatco.murshadik.NewMainActivityDesign;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.ConsultantAdapter;
import com.saatco.murshadik.adapters.SwipeToDeleteCallback;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentConsultansBinding;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.SharedPrefsHelper;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConsultansFragment extends Fragment implements ConsultantAdapter.OnSelectItemClickListener, ISearchChatUsers, IChatUserOnline {

    View rootView;

    RecyclerView recyclerView;
    ConsultantAdapter adapter;
    ArrayList<User> sortedConsultants = new ArrayList<User>();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss a", Locale.ENGLISH);

    ArrayList<QBChatDialog> dialogs = new ArrayList<>();
    SpinKitView spinKitView;

    QBIncomingMessagesManager incomingMessagesManager;

    private QBChatService chatService;

    public ConsultansFragment() {
        // Required empty public constructor
    }

    public static ConsultansFragment newInstance(Bundle bundle) {
        ConsultansFragment fragment = new ConsultansFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatService = ChatHelper.getInstance().getQbChatService();
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int senderId = Integer.parseInt(intent.getStringExtra("sender_id"));
            String lastMessage = intent.getStringExtra("last_message");

            if (recyclerView.getAdapter() != null)
                ((ConsultantAdapter) recyclerView.getAdapter()).updateLastMessage(senderId, lastMessage, true);
            sortedConsultants.sort(Collections.reverseOrder());
        }
    };

    private final BroadcastReceiver mChatUsers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getChatDialogs();
        }
    };


    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        super.onDestroy();

        if (getContext() != null) {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver);
        }
        if (incomingMessagesManager != null)
            incomingMessagesManager.removeDialogMessageListrener(listener);

    }


    // ********************** receive new message from user and update chat lis counts ********************* //

    private final QBChatDialogMessageListener listener = new QBChatDialogMessageListener() {
        @Override
        public void processMessage(String dialogID, QBChatMessage qbChatMessage, Integer senderID) {

            updateChatDialog(dialogID, qbChatMessage, senderID);

//            getUnreadMessagesCount(dialogs);

        }

        @Override
        public void processError(String dialogID, QBChatException e, QBChatMessage qbChatMessage, Integer senderID) {

        }
    };


    @Override
    public void onResume() {
        super.onResume();

        if (getContext() == null) return;

        // ********************** get chat dialogs from QB SDK ********************* //

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter("update_message"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mChatUsers, new IntentFilter("chat_user_service"));


        if (chatService == null) {
            QBTcpConfigurationBuilder configurationBuilder = new QBTcpConfigurationBuilder();
            configurationBuilder.setSocketTimeout(0);
            QBChatService.setConnectionFabric(new QBTcpChatConnectionFabric(configurationBuilder));
            QBChatService.setDebugEnabled(true);
            chatService = QBChatService.getInstance();
            chatService.setReconnectionAllowed(true);
        }

        // ********************** initialize incomming message listener ********************* //

        incomingMessagesManager = ChatHelper.getInstance().getQbChatService().getIncomingMessagesManager();

        if (incomingMessagesManager != null)
            incomingMessagesManager.addDialogMessageListener(listener);
        else {

            if (SharedPrefsHelper.getInstance().getQbUser() != null) {
                ChatHelper.getInstance().getQbChatService().login(SharedPrefsHelper.getInstance().getQbUser(), new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                        incomingMessagesManager = ChatHelper.getInstance().getQbChatService().getIncomingMessagesManager();
                        if (incomingMessagesManager != null)
                            incomingMessagesManager.addDialogMessageListener(listener);

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mChatUsers);

        // ********************** save user chat list into memory ********************* //
        StorageHelper.saveAllChatUsers(sortedConsultants);

    }

    FragmentConsultansBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsultansBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();


        spinKitView = binding.spinKit;
        recyclerView = binding.rvConsultants;

        setDetails();
        enableSwipeToDelete();

        getChatDialogs();

        return rootView;
    }

    private void updateChatDialog(String dialogId, QBChatMessage qbChatMessage, int senderId) {

        if (StorageHelper.isUserExistInChatList(senderId, sortedConsultants)) {
            if (recyclerView.getAdapter() != null)
                ((ConsultantAdapter) recyclerView.getAdapter()).updateLastMessage(senderId, qbChatMessage.getBody(), false);
            sortedConsultants.sort(Collections.reverseOrder());
        } else {
            getChatDialogs();
        }

    }


    private void setDetails() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConsultantAdapter(sortedConsultants, getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    @Override
    public void onConsultantClick(View view, int position, User user) {

        Intent intent = new Intent(getActivity(), ChatActivityNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("USER", user);
        startActivity(intent);
    }

    void filter(String text) {
        ArrayList<User> temp = new ArrayList();

        for (User d : sortedConsultants) {
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }

        //update recyclerview
        ((ConsultantAdapter) recyclerView.getAdapter()).updateList(temp);
    }


    @Override
    public void onSearchChatUsers(String search) {

        filter(search);
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

    // ********************** get all chats from api ********************* //

    private void getAllChats() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<User>> call = apiInterface.getChatUsers("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {

                try {

                    if (response.body() != null) {

                        for (User user : response.body()) {
                            for (User userChat : sortedConsultants) {
                                if (user.getChatId().equals(userChat.getChatId())) {
                                    userChat.setId(user.getId());
                                    userChat.setPhotoUrl(user.getPhotoUrl());
                                    userChat.setOnline(user.isOnline());
                                    userChat.setName(user.getName());
                                }
                            }
                        }

                        StorageHelper.saveAllChatUsers(sortedConsultants);
                        adapter.updateList(sortedConsultants);
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
            }
        });
    }


    private void getChatDialogs() {

        spinKitView.setVisibility(View.VISIBLE);

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.sortDesc("last_message_date_sent");

        QBRestChatService.getChatDialogs(null, requestBuilder)
                .performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
                    @Override
                    public void onSuccess(ArrayList<QBChatDialog> result, Bundle params) {
                        // This code runs on a background thread
                        dialogs = result;
                        Log.d("TAG", "22@11 getChatDialogs: onSuccess");

                        Executor mainThreadExecutor = Executors.newSingleThreadExecutor();
                        mainThreadExecutor.execute(() ->  processChatDialogs(dialogs));


                        Log.d("TAG", "22@11 getChatDialogs: processChatDialogs done");
                        // This method can potentially do heavy work
                        if (getActivity() == null) return;



                    }

                    @Override
                    public void onError(QBResponseException responseException) {
                        // Handle error on the background thread (optional)
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            spinKitView.setVisibility(View.GONE);
                        });

                    }
                });
    }

    private void processChatDialogs(ArrayList<QBChatDialog> dialogs) {
        Log.d("TAG", "22@11 getChatDialogs: processChatDialogs start");

        ArrayList<User> users = new ArrayList<>();
        Calendar tempDate = Calendar.getInstance();

        Log.d("TAG", "22@11 getChatDialogs: processChatDialogs startFilter " + DateTime.now() + " " + dialogs.size());
        for (QBChatDialog qbChatDialog : dialogs) {
            int opponentId = getOpponentId(qbChatDialog);
            if (opponentId == 0) {
                continue; // Skip dialogs without opponent
            }

            User user = createUserFromDialog(qbChatDialog, tempDate, opponentId);
            if (user.getLastMessage() != null) {
                users.add(user);
            }
        }
        Log.d("TAG", "22@11 getChatDialogs: processChatDialogs endFilter " + DateTime.now());


        sortedConsultants = users;
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            spinKitView.setVisibility(View.GONE);

            adapter.updateList(users);

            getUsers(dialogs);
//            getUnreadMessagesCount(dialogs);
        });


    }

    private int getOpponentId(QBChatDialog dialog) {
        for (Integer id : dialog.getOccupants()) {
            if (id != Integer.parseInt(ProfileHelper.getAccount(getContext()).getChatId())) {
                return id;
            }
        }
        return 0;
    }

    private User createUserFromDialog(QBChatDialog dialog, Calendar tempDate, int opponentId) {
        User user = new User();
        user.setChatId(String.valueOf(opponentId));
        user.setName(dialog.getName());
        user.setPhotoUrl(dialog.getPhoto());
        user.setMsgCount(dialog.getUnreadMessageCount());
        user.setLastMessage(dialog.getLastMessage());
        user.setDateTime(dateFormat.format(tempDate.getTimeInMillis()));
        user.chatDialog = dialog;
        user.setOnline(true);
        return user;
    }

//    private void getUnreadMessagesCount(ArrayList<QBChatDialog> result) {
//        Set<String> dialogsIds = new HashSet<>();
//        for (QBChatDialog qbChatDialog : result)
//            dialogsIds.add(qbChatDialog.getDialogId());
//        QBRestChatService.getTotalUnreadMessagesCount(dialogsIds, new Bundle()).performAsync(new QBEntityCallback<Integer>() {
//            @Override
//            public void onSuccess(Integer integer, Bundle bundle) {
//                if (getActivity() == null) return;
//
//                if (integer > 0) {
//                    ((NewMainActivityDesign) getActivity()).setTextViewBadgeMessagesCount(integer.toString());
//                } else {
//                    ((NewMainActivityDesign) getActivity()).hideTextViewBadgeMessagesCount();
//                }
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//
//            }
//        });
//    }

    private void getUsers(ArrayList<QBChatDialog> dialogs) {

        // Collect unique user IDs efficiently (consider using HashSet)
        Set<Integer> userIds = new HashSet<>();
        for (QBChatDialog dialog : dialogs) {
            userIds.addAll(dialog.getOccupants());
        }
        int currentUserId;
        try {
            currentUserId = Integer.parseInt(ProfileHelper.getAccount(getContext()).getChatId());
        } catch (Exception e) {
            return;
        }

        userIds.removeAll(Collections.singletonList(currentUserId));

        Log.d("TAG", "22@11 getUsers: first");
        // Make a single batch request to retrieve users (consider library support)
        QBUsers.getUsersByIDs(new ArrayList<>(userIds), null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                Log.d("TAG", "22@11 getUsers: onSuccess");

                Map<Integer, QBUser> userMap = new HashMap<>();
                for (QBUser qbUser : qbUsers) {
                    userMap.put(qbUser.getId(), qbUser);
                }

                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    updateUsersWithDetails(sortedConsultants, userMap);

                    spinKitView.setVisibility(View.GONE);
                });


            }

            @Override
            public void onError(QBResponseException e) {
                spinKitView.setVisibility(View.GONE);
            }
        });

        Log.d("TAG", "22@11 getUsers: second");

    }

    private void updateUsersWithDetails(ArrayList<User> users, Map<Integer, QBUser> userMap) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getChatId() != null) {
                QBUser qbUser = userMap.get(Integer.parseInt(user.getChatId()));
                if (qbUser != null && isJSONValid(qbUser.getCustomData())) {
                    JsonObject jsonObject = JsonParser.parseString(qbUser.getCustomData()).getAsJsonObject();
                    user.setOnline(Boolean.parseBoolean(String.valueOf(jsonObject.get("is_online"))));
                    // ... update other user details from jsonObject (similar logic)
                    adapter.updateItem(user, i);
                }
            }
        }
    }

    public boolean isJSONValid(String test) {

        if (test == null) return false;
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private void enableSwipeToDelete() {
        if (getContext() == null) return;
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                if (getActivity() == null) return;
                new AlertDialog.Builder(getActivity()).setMessage(getResources().getString(R.string.remove_confirmation)).setCancelable(false).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        final int position = viewHolder.getAdapterPosition();
                        final User user = adapter.getData().get(position);

                        boolean forceDelete = false;

                        sortedConsultants.removeIf(user1 -> user1.getChatId().equals(user.getChatId()));

                        adapter.updateList(sortedConsultants);

                        QBRestChatService.deleteDialog(user.chatDialog.getDialogId(), forceDelete).performAsync(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {


                                getChatDialogs();

                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });

                        dialog.dismiss();

                    }
                }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        adapter.updateList(sortedConsultants);
                        dialog.dismiss();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();


            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}

