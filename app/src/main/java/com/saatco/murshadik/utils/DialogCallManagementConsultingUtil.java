package com.saatco.murshadik.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.Helpers.ChatHelper;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.PermissionsActivity;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.AvailableConsultantsAdapter;
import com.saatco.murshadik.api.ApiMethodsHelper;
import com.saatco.murshadik.enums.ManagementType;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class DialogCallManagementConsultingUtil {


    ManagementType managementSkillType;
    Activity activity;
    TextView tv_region, tv_skill, tv_type;
    ArrayList<Item> skills_list;
    ArrayList<User> sortedConsultants;
    private QBChatDialog qbChatDialog;
    private User receiverUser;
    Dialog startCallingDialog;
    PermissionsChecker checker;
    String selectedSkill;
    Item selectedSkillItem;

    public DialogCallManagementConsultingUtil(Activity activity) {
        this.activity = activity;
        checker = new PermissionsChecker(activity);
    }


    //#step 1

    public void openSkillsDialog(ManagementType managementType) {
        managementSkillType = managementType;
        final int skillId = managementType == ManagementType.BRANCHES ? 10077 : 10106;

        skills_list = DataUtilHelper.getSkillsFromLocalStorage(activity);
        if (skills_list == null) {
            ApiMethodsHelper.getSkills(msg ->
                    dialogForRegionsAndSkills(
                            msg.stream()
                                    .filter(i -> i.getSkillId() == skillId)
                                    .collect(Collectors.toList())
                                    .get(0).getChildrens()
                            , activity.getString(R.string.select_a_skill)));
        } else {
            dialogForRegionsAndSkills(
                    skills_list.stream()
                            .filter(i -> i.getId() == skillId)
                            .collect(Collectors.toList())
                            .get(0).getChildrens()
                    , activity.getString(R.string.select_a_skill));
        }

    }


    //#step 2
    public void showSearchingDialog() {
        Dialog dialog = new Dialog(activity);//, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_call_consult_search);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(activity, R.drawable.round_border_dialog_call_consult_bg));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dialog.getWindow().setBackgroundBlurRadius(50);
        }

        dialog.show();


        getOnlineConsultants(true, msg -> {
            if (!msg) {
                dialog.dismiss();
//                showSelectSkillAndRegion();
                ToastUtils.longToast("لا يوجد مرشدين متاحين حسب التخصص المختار");
                return;
            }
            if (sortedConsultants.isEmpty()) return;

            getChatDialogs();
            int randomPick = Util.getRandomInt(0, sortedConsultants.size());
            receiverUser = sortedConsultants.get(randomPick);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startCallingDialog();
            dialog.dismiss();

        });


    }

    //#step 3
    public void startCallingDialog() {

        if (startCallingDialog != null)
            startCallingDialog.dismiss();

        startCallingDialog = new Dialog(activity);//, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        startCallingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        startCallingDialog.setContentView(R.layout.dialog_call_consult_founded_list);
        startCallingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startCallingDialog.getWindow().setBackgroundBlurRadius(50);
        }

        RecyclerView rv_founded_consultant = startCallingDialog.findViewById(R.id.rv_founded_consultant);

        AvailableConsultantsAdapter adapter = new AvailableConsultantsAdapter(sortedConsultants, activity, selectedSkill,
                (view, position, consultant, isVideoCall) -> {
                    receiverUser = consultant;
                    startCall(isVideoCall);
                });

        rv_founded_consultant.setAdapter(adapter);
        rv_founded_consultant.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        rv_founded_consultant.setItemAnimator(new DefaultItemAnimator());

        startCallingDialog.show();

    }
    //=============== end steps ==============


    //=============== helper methods ==============
    private Item getSkillItemByName(String skill_name) {
        if (skills_list == null) return null;
        for (Item skill : skills_list) {
            for (Item s : skill.getChildrens()) {
                if (s.getNameAr().equals(skill_name))
                    return s;
            }
            if (skill.getNameAr().equals(skill_name))
                return skill;
        }
        return null;
    }

    private Item getSkillItemByNameWithDeepSearch(String skillName, ArrayList<Item> items) {
        if (items == null) return null;
        for (Item item : items) {
            if (item.getNameAr().equals(skillName))
                return item;
            if (item.hasChildren()) {
                Item temp = getSkillItemByNameWithDeepSearch(skillName, item.getChildrens());
                if (temp != null)
                    return temp;
            }
        }
        return null;
    }


    private void dialogForRegionsAndSkills(ArrayList<Item> items, String title) {
//        ArrayList<Item> temp = new ArrayList<>();

//        for (Item item : items) {
//            if (item.getChildrens() != null && !item.getChildrens().isEmpty() && item.getNameAr().contains("الاستشارات")) {
//                temp.addAll(item.getChildrens().stream().filter(i -> (managementSkillType == ManagementType.CONSULTING) == i.getNameAr().contains("فرع الوزارة")).collect(Collectors.toList()));
//            }
//        }
//        ArrayList<Item> itemsWithSubItems = new ArrayList<>(temp);
        DialogUtil.DialogListOfItemsWithIcon dialogListOfItemsWithIcon = new DialogUtil.DialogListOfItemsWithIcon(activity, title, items, msg -> {
            if (msg.hasChildren()) {
                dialogForRegionsAndSkills(msg.getChildrens(), msg.getNameAr());
                return;
            }
            selectedSkill = msg.getNameAr();
            Item item = getSkillItemByNameWithDeepSearch(selectedSkill, items);
            if (item == null) return;
            showSearchingDialog();
        });
        dialogListOfItemsWithIcon.show();


    }

    private void initAndUpdateConsultantData() {
        ViewModelProvider viewModelProvider = new ViewModelProvider((ViewModelStoreOwner) activity);
        UserViewModel userViewModel = viewModelProvider.get(UserViewModel.class);
        userViewModel.updateAllUsers();
    }

    private void getOnlineConsultants(boolean isSorted, MyCallbackHandler<Boolean> myCallbackHandler) {
        ViewModelProvider viewModelProvider = new ViewModelProvider((ViewModelStoreOwner) activity);
        UserViewModel userViewModel = viewModelProvider.get(UserViewModel.class);
        String region_name, skill;

        region_name = "";
        skill = selectedSkill;

        final boolean[] isFinished = {false};
        if (!region_name.contains("المناطق") && !region_name.isEmpty()) {
            userViewModel.getConsultantsByCategoryAndRegion("%" + skill + "%", "%" + region_name + "%").observe((LifecycleOwner) activity, users -> {
                if (isFinished[0]) return;
                isFinished[0] = true;
                sortedConsultants = new ArrayList<>();
                for (User user : users) {
                    user.setRoleId(6);
                    if (user.isOnline())
                        sortedConsultants.add(user);
                }
                if (sortedConsultants.isEmpty()) {
                    userViewModel.getConsultantsByCategory("%" + skill + "%").observe((LifecycleOwner) activity, users1 -> {

                        sortedConsultants = new ArrayList<>();
                        for (User user : users1) {
                            user.setRoleId(6);
                            if (user.isOnline())
                                sortedConsultants.add(user);
                        }
                        if (sortedConsultants.isEmpty())
                            myCallbackHandler.onResponse(false);
                        else
                            myCallbackHandler.onResponse(true);
                    });
                } else
                    myCallbackHandler.onResponse(true);
            });
        } else {
            userViewModel.getConsultantsByCategory("%" + skill + "%").observe((LifecycleOwner) activity, users -> {
                if (isFinished[0]) return;
                isFinished[0] = true;
                sortedConsultants = new ArrayList<>();
                for (User user : users) {
                    user.setRoleId(6);
                    if (user.isOnline())
                        sortedConsultants.add(user);
                }
                if (sortedConsultants.isEmpty())
                    myCallbackHandler.onResponse(false);
                else
                    myCallbackHandler.onResponse(true);
            });
        }

    }

    private void createChatDialog() {

        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        occupantIdsList.add(Integer.valueOf(receiverUser.getChatId()));

        QBChatDialog dialogNew = new QBChatDialog();
        dialogNew.setName("Chat with " + receiverUser.getName());
        dialogNew.setPhoto("1786");
        dialogNew.setType(QBDialogType.PRIVATE);
        dialogNew.setOccupantsIds(occupantIdsList);

        QBChatDialog dialog = DialogUtils.buildDialog("Chat with " + receiverUser.getName(), QBDialogType.PRIVATE, occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle params) {
                qbChatDialog = result;
                sendMessage(1, "");

            }

            @Override
            public void onError(QBResponseException responseException) {
                Log.v("QBERRE", responseException.toString());
            }
        });
    }

    private boolean checkBluetoothConnectPermission() {
        return checker.lacksPermissions(Manifest.permission.BLUETOOTH_CONNECT) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(activity, checkOnlyAudio, Consts.PERMISSIONS);
    }

    //******************* START CALL from QB SDK **********************//
    private void startCall(boolean isVideoCall) {
        if (checker.lacksPermissions(Consts.PERMISSIONS) || checkBluetoothConnectPermission()) {
            startPermissionsActivity(!isVideoCall);
            return;
        }
        if (receiverUser.getChatId() != null) {

            if (receiverUser.chatDialog != null) {
                qbChatDialog = receiverUser.chatDialog;
                sendMessage(1, "");
            } else {
                createChatDialog();
            }

            ArrayList<Integer> opponentsList = new ArrayList<>();
            opponentsList.add(Integer.valueOf(receiverUser.getChatId()));

            QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                    ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                    : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

            QBRTCClient qbrtcClient = QBRTCClient.getInstance(activity.getApplicationContext());
            QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
            WebRtcSessionManager.getInstance(activity).setCurrentSession(newQbRtcSession);
            String newSessionID = newQbRtcSession.getSessionID();
            //******************* send push to user **********************/
            PushNotificationSender.sendPushMessage(opponentsList, newSessionID, ProfileHelper.getAccount(activity.getApplicationContext()).getName(), "1");

            PrefUtil.writePreferenceValue(activity.getApplicationContext(), "call_user_name", receiverUser.getName());

            CallActivity.start(activity, false);
        } else {
            Util.showToast("المرشد غير متاح", activity);
        }
    }

    //send text message
    private void sendMessage(int type, String location) {

        String message = "[رسالة تلقائية] تم الاتصال بك ";

        if (TextUtils.isEmpty(message) && type == 1) {
            Toast.makeText(activity.getApplicationContext(), "لرجاء كتابة الرسالة", Toast.LENGTH_SHORT).show();
        } else {

            if (ChatHelper.getInstance().isLogged()) {


                QBChatMessage chatMessage = new QBChatMessage();

                chatMessage.setProperty("custom", String.valueOf(receiverUser.getId()));
                chatMessage.setProperty("type", String.valueOf(type));
                chatMessage.setProperty("url", String.valueOf(location));
                chatMessage.setRecipientId(Integer.valueOf(receiverUser.getChatId()));
                chatMessage.setSaveToHistory(true);
                chatMessage.setDateSent(System.currentTimeMillis() / 1000);
                chatMessage.setBody(message);
                chatMessage.setMarkable(true);

                try {

                    if (qbChatDialog != null) {
                        qbChatDialog.sendMessage(chatMessage);
                    }

                } catch (Exception e) {
                    Log.d("BASSAM", "dialog" + e.getLocalizedMessage());
                    Toast.makeText(activity.getApplicationContext(), "Unable to send a message", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void getChatDialogs() {

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.sortDesc("updated_at");

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> result, Bundle params) {

                for (QBChatDialog qbChatDialog : result) {
                    for (User user : sortedConsultants) {
                        if (user.getChatId() != null) {
                            if (Integer.parseInt(user.getChatId()) == qbChatDialog.getUserId()) {
                                user.chatDialog = qbChatDialog;
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(QBResponseException responseException) {

            }
        });
    }


}
