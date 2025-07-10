package com.saatco.murshadik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.saatco.murshadik.databinding.ActivityGroupChatBinding;
import com.saatco.murshadik.databinding.ActivityGroupSkillsChatBinding;
import com.saatco.murshadik.model.Message;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.Util;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.List;

public class GroupSkillsChatActivity extends BaseActivity {

    ActivityGroupSkillsChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupSkillsChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.imgBack.setOnClickListener( v -> {
//            createDialogWithCustomParameters();
            getDialogs();
        });
    }




    private void getDialogs(){

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(50);
        requestBuilder.setSkip(0);
        requestBuilder.in(Consts.QB_CUSTOM_DATA_CHAT_TYPE, "public");

        QBRestChatService.getChatDialogs(QBDialogType.PUBLIC_GROUP, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                if (!qbChatDialogs.get(0).isJoined()){
                    try {
                        qbChatDialogs.get(0).join(new DiscussionHistory());
                        qbChatDialogs.get(0).sendMessage("محمود ");
                        updateDialogWithCustomParameters(qbChatDialogs.get(0));
                        List<Integer> o = qbChatDialogs.get(0).getOccupants();
                        Log.d("chat-public", "onSuccess no of Occupants: " + qbChatDialogs.get(0).getOccupants());
                        qbChatDialogs.get(0).addMessageListener(new QBChatDialogMessageListener() {
                            @Override
                            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                                Log.d("chat-group", "processMessage: " + qbChatMessage.toString());
                            }

                            @Override
                            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

                            }
                        });
                    } catch (XMPPException | SmackException e) {
                        throw new RuntimeException(e);
                    }
                }
                Log.v("qb chat dialog: ", qbChatDialogs.toString());
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }




    private void createDialogWithCustomParameters() {
        QBDialogCustomData customData = new QBDialogCustomData();
        customData.putInteger(Consts.QB_CUSTOM_DATA_SKILL_ID, 123);
        customData.putString(Consts.QB_CUSTOM_DATA_SKILL_NAME, "النخيل");
        customData.putString(Consts.QB_CUSTOM_DATA_CHAT_TYPE, "public");
        customData.setId(Consts.QB_CUSTOM_DATA_ID);

        QBChatDialog chatDialog = new QBChatDialog();
        chatDialog.setName("النخيل");
        chatDialog.setType(QBDialogType.PUBLIC_GROUP);
        chatDialog.setCustomData(customData);

        QBRestChatService.createChatDialog(chatDialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog chatDialog, Bundle bundle) {
                ToastUtils.longToast("public group created ");
                try {
                    chatDialog.join(new DiscussionHistory());
                    chatDialog.sendMessage("heeeiiiiwoooww");
                    if (chatDialog.isJoined()){
                        ToastUtils.longToast("joined group success");
                    }
                } catch (XMPPException | SmackException exception) {
                    ToastUtils.longToast("exception.getMessage()");
                }
            }

            @Override
            public void onError(QBResponseException exception) {
                ToastUtils.longToast("outer exception.getMessage()");
            }
        });
    }

    private void updateDialogWithCustomParameters(QBChatDialog chatDialog) {

        QBRestChatService.getChatDialogById(chatDialog.getDialogId()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                QBDialogCustomData customData = qbChatDialog.getCustomData();
                return;
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
        if (true) return;

        QBDialogCustomData customData = chatDialog.getCustomData();
        if (customData == null) customData = new QBDialogCustomData();
        customData.putInteger(Consts.QB_CUSTOM_DATA_SKILL_ID, 123);
        customData.putString(Consts.QB_CUSTOM_DATA_SKILL_NAME, "التمور");
        customData.putString(Consts.QB_CUSTOM_DATA_CHAT_TYPE, "public");
        customData.setId(Consts.QB_CUSTOM_DATA_ID);

        chatDialog.setName("تمور");
        chatDialog.setType(QBDialogType.PUBLIC_GROUP);
        chatDialog.setCustomData(customData);

        QBRestChatService.updateChatDialog(chatDialog, new QBRequestUpdateBuilder()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog chatDialog, Bundle bundle) {
                ToastUtils.longToast("public group created ");
                try {
                    chatDialog.join(new DiscussionHistory());
                    chatDialog.sendMessage("heeeiiiiwoooww");
                    Log.d("chat-public", "onSuccess custom data updated: " + chatDialog.getCustomData().getString(Consts.QB_CUSTOM_DATA_SKILL_NAME));

                    if (chatDialog.isJoined()){
                        ToastUtils.longToast("joined group success");
                    }
                } catch (XMPPException | SmackException exception) {
                    ToastUtils.longToast("exception.getMessage()");
                }
            }

            @Override
            public void onError(QBResponseException exception) {
                ToastUtils.longToast("outer" + exception.getMessage());
            }
        });
    }

    // now we can get the same parameters from any dialog we load
    private void getDataFromDialog(QBChatDialog chatDialog) {
        Integer customInteger = chatDialog.getCustomData().getInteger("customInteger");
        String customString = chatDialog.getCustomData().getString("customString");
    }
}