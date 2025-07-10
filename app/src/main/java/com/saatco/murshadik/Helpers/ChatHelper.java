package com.saatco.murshadik.Helpers;

import android.os.Bundle;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.saatco.murshadik.App;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatHelper  {


    private static final String TAG = ChatHelper.class.getSimpleName();

    public static final int CHAT_HISTORY_ITEMS_PER_PAGE = 25;
    private static final String CHAT_HISTORY_ITEMS_SORT_FIELD = "date_sent";

    private static ChatHelper instance;

    private final QBChatService qbChatService;

    public static synchronized ChatHelper getInstance() {
        if (instance == null) {
            QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
            QBChatService.setDebugEnabled(true);
            QBChatService.setConfigurationBuilder(buildChatConfigs());
            QBChatService.setDefaultPacketReplyTimeout(30000);
            instance = new ChatHelper();
        }
        return instance;
    }

    public boolean isLogged() {
        return QBChatService.getInstance().isLoggedIn();
    }



    private ChatHelper() {
        qbChatService = QBChatService.getInstance();
        qbChatService.setUseStreamManagement(true);
    }

    private static QBChatService.ConfigurationBuilder buildChatConfigs() {
        QBChatService.ConfigurationBuilder configurationBuilder = new QBChatService.ConfigurationBuilder();

        configurationBuilder.setSocketTimeout(App.SOCKET_TIMEOUT);
        configurationBuilder.setUseTls(App.USE_TLS);
        configurationBuilder.setKeepAlive(App.KEEP_ALIVE);
        configurationBuilder.setAutojoinEnabled(true);
        configurationBuilder.setAutoMarkDelivered(App.AUTO_MARK_DELIVERED);
        configurationBuilder.setReconnectionAllowed(App.RECONNECTION_ALLOWED);
        configurationBuilder.setAllowListenNetwork(App.ALLOW_LISTEN_NETWORK);
        configurationBuilder.setPort(App.CHAT_PORT);

        return configurationBuilder;
    }

    public QBChatService getQbChatService(){
        return qbChatService;
    }

    public void addConnectionListener(ConnectionListener listener) {
        qbChatService.addConnectionListener(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        qbChatService.removeConnectionListener(listener);
    }

    public void updateUser(final QBUser user, final QBEntityCallback<QBUser> callback) {
        QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                callback.onSuccess(user, bundle);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }


    public void loginToChat(final QBUser user, final QBEntityCallback<Void> callback) {
        if (qbChatService.isLoggedIn()) {
            callback.onSuccess(null, null);
            return;
        }
        if (user == null) {
            callback.onError(new QBResponseException("User is Null"));
            return;
        }

        qbChatService.login(user, callback);
    }

    public void join(QBChatDialog chatDialog, final QBEntityCallback<Void> callback) {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);

        chatDialog.join(history, callback);
    }

    public void join(List<QBChatDialog> dialogs) throws Exception {
        for (QBChatDialog dialog : dialogs) {
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            dialog.join(history);
        }
    }

    public void leaveChatDialog(QBChatDialog chatDialog) throws XMPPException, SmackException.NotConnectedException {
        chatDialog.leave();
    }

    public void destroy() {
        qbChatService.destroy();
    }



    public void deleteDialogs(Collection<QBChatDialog> dialogs, final QBEntityCallback<ArrayList<String>> callback) {
        StringifyArrayList<String> dialogsIds = new StringifyArrayList<>();
        for (QBChatDialog dialog : dialogs) {
            dialogsIds.add(dialog.getDialogId());
        }

        QBRestChatService.deleteDialogs(dialogsIds, false, null).performAsync(callback);
    }

    public void deleteDialog(QBChatDialog qbDialog, QBEntityCallback<Void> callback) {

    }

    public void exitFromDialog(QBChatDialog qbDialog, QBEntityCallback<QBChatDialog> callback) {
        try {
            leaveChatDialog(qbDialog);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            callback.onError(new QBResponseException(e.getMessage()));
        }

        QBUser currentUser = QBChatService.getInstance().getUser();
        QBDialogRequestBuilder qbRequestBuilder = new QBDialogRequestBuilder();
        qbRequestBuilder.removeUsers(currentUser.getId());

        qbDialog.setName(buildDialogNameWithoutUser(qbDialog.getName(), currentUser.getFullName()));

        QBRestChatService.updateGroupChatDialog(qbDialog, qbRequestBuilder).performAsync(callback);
    }

    private static String buildDialogNameWithoutUser(String dialogName, String userName) {
        String regex = ", " + userName + "|" + userName + ", ";
        return dialogName.replaceAll(regex, "");
    }

    public void updateDialogUsers(QBChatDialog qbDialog,
                                  final List<QBUser> newQbDialogUsersList,
                                  QBEntityCallback<QBChatDialog> callback) {

    }

    public void loadChatHistory(QBChatDialog dialog, int skipPagination,
                                final QBEntityCallback<ArrayList<QBChatMessage>> callback) {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setSkip(skipPagination);
        messageGetBuilder.setLimit(CHAT_HISTORY_ITEMS_PER_PAGE);
        messageGetBuilder.sortDesc(CHAT_HISTORY_ITEMS_SORT_FIELD);
        messageGetBuilder.markAsRead(false);

    }


    public void getDialogById(String dialogId, final QBEntityCallback<QBChatDialog> callback) {
        QBRestChatService.getChatDialogById(dialogId).performAsync(callback);
    }


    public void loadFileAsAttachment(File file, QBEntityCallback<QBAttachment> callback,
                                     QBProgressCallback progressCallback) {
       /* QBContent.uploadFileTask(file, false, null, progressCallback).performAsync(
                new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(callback) {
                    @Override
                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                        QBAttachment attachment = new QBAttachment(QBAttachment.IMAGE_TYPE);
                        attachment.setId(qbFile.getUid());
                        attachment.setSize(qbFile.getSize());
                        attachment.setName(qbFile.getName());
                        attachment.setContentType(qbFile.getContentType());
                        callback.onSuccess(attachment, bundle);
                    }
                });*/
    }



}
