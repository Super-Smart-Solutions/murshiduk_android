package com.saatco.murshadik.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by Devlomi on 31/01/2018.
 */

//this class to identify extras and actions
//and for getting some custom intents
public class IntentUtils {

    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_REAL_PATH = "real-path";
    public static final String EXTRA_REAL_PATH_LIST = "real-path-list";
    public static final String EXTRA_STAT = "stat";
    public static final String EXTRA_MY_UID = "my_uid";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_GROUP_ID = "extra-group-id";


    public static final String EXTRA_PATH_RESULT = "path_result";
    public static final String EXTRA_MESSAGE_ID = "messageId";
    public static final String EXTRA_DATA_RESULT = "data";
    public static final String EXTRA_CONTACT_LIST = "contactList";

    public static final String EXTRA_SHARED_TEXT = "shared_text";
    public static final String EXTRA_URI_LIST = "uri_list";
    public static final String EXTRA_URI = "uri";
    public static final String EXTRA_MIME_TYPE = "mime_type";


    public static final String EXTRA_FORWARDED = "forwarded";


    public static final String EXTRA_PROFILE_PATH = "extra_profile_path";


    public static final String EXTRA_FIRST_VISIBLE_ITEM_POSITION = "extra_first_visible_item_position";
    public static final String EXTRA_LAST_VISIBLE_ITEM_POSITION = "extra_last_visible_item_position";
    public static final String EXTRA_CURRENT_MESSAGE_ID = "current_message_id";
    public static final String EXTRA_STARTING_POSITION = "extra_starting_item_position";
    public static final String EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position";

    public static final String INTENT_ACTION_SYNC_CONTACTS = "intent-action-sync-contacts";
    public static final String INTENT_ACTION_HANDLE_REPLY = "intent-action-handle-reply";
    public static final String INTENT_ACTION_MARK_AS_READ = "intent-action-mark-as-read";
    public static final String INTENT_ACTION_UPDATE_MESSAGE_STATE = "intent-action-update-message-state";
    public static final String INTENT_ACTION_UPDATE_VOICE_MESSAGE_STATE = "intent-action-update-voice-message-state";
    public static final String INTENT_ACTION_NETWORK_REQUEST = "intent-action-network-request";
    public static final String INTENT_ACTION_FETCH_AND_CREATE_GROUP = "intent-action-fetch-and-create-group";
    public static final String INTENT_ACTION_FETCH_GROUP_INFO = "intent-action-fetch-group-info";

    public static final String INTENT_ACTION_FETCH_USER_GROUPS_AND_BROADCASTS = "intent-action-fetch-user-groups";
    public static final String INTENT_ACTION_MESSAGE_DELETED = "intent-action-message-deleted";


    public static final String PHONE = "phone";
    public static final String INTENT_ACTION_DISMISS_NOTIFICATION = "dismiss-handleNewMessage";
    public static final String UID = "uid";
    public static final String ID = "id";
    public static final String ACTION_START_PLAY = "start_play";
    public static final String ACTION_SEEK_TO = "seek_to";
    public static final String URL = "url";
    public static final String POS = "pos";
    public static final String PROGRESS = "progress";
    public static final String ACTION_STOP_AUDIO = "stop_audio";
    public static final String EXTRA_HEADSETSTATE_CHANGED = "headsetstate_changed";
    public static final String EXTRA_GROUP_COUNT = "extra-group-count";
    public static final String EXTRA_SELECTED_USERS = "extra-selected-users";
    public static final String INTENT_ACTION_UPDATE_GROUP = "intent-action-update-group";
    public static final String EXTRA_EVENT_ID = "extra-event-id";
    public static final String EXTRA_CONTEXT_START = "extra-context-start";
    public static final String EXTRA_EVENT_TYPE = "extra-event-type";
    public static final String EXTRA_CONTEXT_END = "extra-context-end";
    public static final String EXTRA_GROUP_EVENT = "extra-group-event";
    public static final String EXTRA_STATUS_ID = "extra-status-id";
    public static final String EXTRA_TEXT_STATUS = "extra-text-status";


    public static final String EXTRA_CHAT_ID = "extra-chat-id";
    public static final String ACTION_TYPE = "action-type";
    public static final String FCM_TOKEN = "fcm-token";
    public static final String PHONE_CALL_TYPE = "phone-call-type";
    public static final String ISVIDEO = "is_video";
    public static final String CALL_ID = "call-id";
    public static final String CALL_ACTION_TYPE = "call-action-type";
    public static final String IS_COMING_FROM_NOTIFICATION = "is-coming-from-notification";
    public static final String CAMERA_VIEW_SHOW_PICK_IMAGE_BUTTON = "camera-view-show-pick-image";
    public static final String IS_STATUS = "isStatus";
    public static final String START_SINCH = "start-sinch";
    public static final String IS_GROUP = "isGroup";
    public static final String IS_BROADCAST = "isBroadcast";

    public static String EXTRA_FROMID = "fromId";

    public static int NOTIFICATION_ACTION_ANSWER = 1;
    public static int NOTIFICATION_ACTION_DECLINE = 2;
    public static int NOTIFICATION_ACTION_CLICK = 3;

    public static boolean isActivityForIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }



}
