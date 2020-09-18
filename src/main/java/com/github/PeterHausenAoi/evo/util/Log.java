package com.github.PeterHausenAoi.evo.util;

public class Log {
    private static final String TAG = Log.class.getSimpleName();

    public static void doLog(String tag, String msg){
        LoggerService.log(tag, msg);
    }
}