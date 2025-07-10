package com.saatco.murshadik.utils;

import android.Manifest;

/**
 * QuickBlox team
 */
public interface Consts {

    int ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS = 422;

    int MAX_OPPONENTS_COUNT = 6;
    int MAX_LOGIN_LENGTH = 15;
    int MAX_FULLNAME_LENGTH = 20;

    int CAMERA_REQUEST = 800;
    int CAMERA_REQUEST_OK = 801;


    String EXTRA_QB_USER = "qb_user";
    String EXTRA_USER_ID = "user_id";
    String EXTRA_USER_LOGIN = "user_login";
    String EXTRA_USER_PASSWORD = "user_password";
    String EXTRA_PENDING_INTENT = "pending_Intent";
    String EXTRA_CONTEXT = "context";
    String EXTRA_OPPONENTS_LIST = "opponents_list";
    String EXTRA_CONFERENCE_TYPE = "conference_type";

    String EXTRA_IS_INCOMING_CALL = "conversation_reason";
    String EXTRA_IS_CLINIC_CALL = "is_clinic_call";
    String EXTRA_IS_CLINIC_START_FOR_CALL = "is_clinic_start_for_call";
    String EXTRA_CALL_DURATION = "call_duration";
    String EXTRA_CLINIC_APPOINTMENT_ID = "clinic_appointment_id";
    String EXTRA_LOGIN_RESULT = "login_result";
    String EXTRA_LOGIN_ERROR_MESSAGE = "login_error_message";

    String EXTRA_IS_CONSULTATION_CALL = "is_consultation_call";
    String EXTRA_CONSULTATION_APPOINTMENT_ID = "consultation_appointment_id";


    int EXTRA_LOGIN_RESULT_CODE = 1002;

    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    String ERROR_LOGIN = "unable to login";
    String GROUP_FCM_TOPIC = "group-";
    String PROFILE_UPDATED = "Profile picture has been updated";
    int HOURS_FORMAT_24 = 24 * 60 * 60 * 1000;

    String MARKET_FCM = "-market";
    String REPORT_BUG_URL = "https://mewa-ershad.net/Mobile/reportabug?uID=";
    String CHAT_BOT_URL = "https://mewa-ershad.net/mobile/cbs?username=" ;
    String TERMS_URL = "https://mewa-ershad.net/Mobile/Terms";
    String FAQS = "https://mewa-ershad.net/mobile/faqs";
    String WEB_LINK = "https://saatco.net/";
    int CAT_ID_DIGITAL_LIBRARY = 15;
    int CAT_ID_RESEARCH_APPLICATIONS = 86;

    int HOME_INDEX = 0;
    int MARKET_INDEX = 1;
    int CONSULTATION_INDEX = 2;
    int NOTIFICATION_INDEX = 3;
    int PROFILE_INDEX = 4;

    String DEFAULT_WEATHER_CODE = "OERK";
    String ARTICLE_HEADING = "الإرشاد الزراعي";
    String CALENDER_HEADING = "التقويم الزراعي" ;
    String CALENDER_ONE_URL = "https://mewa-ershad.net/media/pdf/b08995b9-cf21-4a2c-8594-72111d627458.pdf" ;
    String CALENDER_TWO_URL = "https://mewa-ershad.net/media/pdf/8e68c781-9154-4216-a138-d8e5a8ee442a.pdf" ;

    int NOTI_TYPE_MARKET = 2;
    int NOTI_TYPE_LAB = 3;
    int NOTI_TYPE_QUESTION = 6;
    int NOTI_TYPE_ANSWER = 5;
    int NOTI_TYPE_CHAT = 7;
    int NOTI_TYPE_GUIDE = 9;

    int HEALTHY = 0;
    int DISEASE = 1 ;
    int PLANTING = 2;

    int WEEK_FILTER = 7;
    int MONTH_FILTER = 30;
    int YEAR_FILTER = 360;

    String DEFAULT_LAT = "24.5806831";
    String DEFAULT_LONG = "46.5183167";

    int CONSULTANT = 6;
    int FARMER = 5;

    String MESSAGE_TYPE = "murshadik.message_type";
    String MESSAGE_LINK_TYPE = "murshadik.message_link";

    String IS_SUBSCRIBE_TO_POSHES = "isSubscribeToPushes";

    //created by amin
    String CONSULTANT_NAME = "consultant_name";
    String TOKEN = "token";
    String IS_INCOMING_CALL = "is_incoming_call";
    int CALL_NOTIFY_ID = 4;

    String SHOW_ALL = "الكل";

    String MSG_CHANNEL = "msgs_channel";
    String CALL_CHANNEL = "call_channel";

    String PUSHES_PUSH_TYPE_MESSAGE = "message";
    String PUSHES_PUSH_TYPE = "push_type";
    String PUSHES_CHAT_ID = "chat_id";
    String IS_START_FROM_NOTIFY = "is_notification";

    String SHORTCUT_BADGER_COUNTER = "shortcut_badger_counter";
    String SHORTCUT_BADGER_COUNTER_CHAT_MSG = "shortcut_badger_counter_chat_msg";
    String ON_OFF_IS_ONLINE = "on_off_is_online";
    String IS_START_FROM_ALARM_IS_ONLINE = "is_start_from_alarm_is_online";
    String SEND_IS_ONLINE_BTN_NOTIFY = "send_is_online_btn_notify";
    String NOTIFY = "notify";
    int IS_ONLINE_NOTIFY_ID = 51;

    String FARM_DATA_EXTRA = "farm_extra_data";

    String MIME_PDF = "application/pdf";
    String EXTRA_IS_MY_WORKER_PROFILE = "imwp";
    String EXTRA_WORKER_KEY = "worker";
    String QB_CUSTOM_DATA_SKILL_ID = "skillId";
    String QB_CUSTOM_DATA_SKILL_NAME = "skillName";
    String QB_CUSTOM_DATA_CHAT_TYPE = "chatType";
    int QB_CUSTOM_DATA_ID = 55;


    // clinic service
    long INVALID_RETURN_TIME = -999999999;

    String EXTRA_CLINIC_NOTIFICATION = "clinic_notification";
    String CLINIC_CHANNEL_NOTIFICATION_ID = "clinic_channel_notification_id";
    int CLINIC_NOTIFY_ID = 515;

    // Dashboard V3 design
}