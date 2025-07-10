package com.saatco.murshadik;

import java.util.HashMap;


public class ChatMessageHolder {

    private final String mMessage;
    private final boolean mSentMessage;
    private final String mTimestamp;
    private int mStatus;
    private static final HashMap<Long, ChatMessageHolder> mMessageMap = new HashMap<Long, ChatMessageHolder>();


    public ChatMessageHolder(long id, String message , boolean isSentMessage , String time, int status){
        mSentMessage = isSentMessage;
        mMessage = message;
        mTimestamp = time;
        mStatus = status;
        mMessageMap.put(Long.valueOf(id), this);
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isSentMessage(){
        return mSentMessage;
    }

    public String getTime() {
        return mTimestamp;
    }

    public int getStatus() {
        return mStatus;
    }

    public static void setStatus(long id, int status) {
        ChatMessageHolder msg = mMessageMap.get(Long.valueOf(id));
        if(null == msg) return;
        msg.mStatus = status;
    }


}
