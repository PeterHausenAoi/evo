package main.java.com.github.PeterHausenAoi.evo.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoggerService extends AbstractService<LogMessage>{
    private static final String TAG = LoggerService.class.getSimpleName();

    private static LoggerService mInstance = null;

    public static synchronized LoggerService getInstance(){
        if (mInstance == null){
            mInstance = new LoggerService();

            try {
                mInstance.start();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return mInstance;
    }

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss");

    public static void log(String tag, String msg){
        getInstance().addMsg(new LogMessage(tag, msg));
    }

    private final Object mLock;
    private List<LogHandler> mHandlers;

    public LoggerService() {
        mHandlers = new ArrayList<>();
        mLock = new Object();

        mHandlers.add(new PrintHandler());
    }

    private String formatMessage(LogMessage msg){
        StringBuilder builder = new StringBuilder();
        builder.append(mSdf.format(new Date(msg.getTimestamp())))
                .append(" | ")
                .append(msg.getTag())
                .append(": ")
                .append(msg.getMessage());

        return builder.toString();
    }

    @Override
    protected void processMsg(LogMessage msg) {
        String formatted = formatMessage(msg);

        synchronized (mLock){
            for (LogHandler logHandler : mHandlers){
                logHandler.handle(formatted);
            }
        }
    }

    public void addHandler(LogHandler logHandler){
        synchronized (mLock){
            mHandlers.add(logHandler);
        }
    }

    public void deleteHandler(LogHandler logHandler){
        synchronized (mLock){
            mHandlers.remove(logHandler);
        }
    }

    private static class PrintHandler implements LogHandler{

        @Override
        public void handle(String msg) {
            System.out.println(msg);
        }
    }
}