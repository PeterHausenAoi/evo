package com.github.PeterHausenAoi.evo.util;

public class LogMessage implements ServiceMessage{
    private static final String TAG = LogMessage.class.getSimpleName();

    private final String mTag;
    private final long mTimestamp;
    private final String mMessage;

    public LogMessage(String tag, String message) {
        this.mTag = tag;
        this.mMessage = message;
        mTimestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getTag() {
        return mTag;
    }
}