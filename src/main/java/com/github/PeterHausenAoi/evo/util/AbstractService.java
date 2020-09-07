package main.java.com.github.PeterHausenAoi.evo.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractService<V extends ServiceMessage> {
    public final String TAG = AbstractService.class.getSimpleName();

    private final BlockingQueue<V> mQueue;
    protected boolean mShouldStop = false;

    protected Thread mThread = null;

    public AbstractService() {
        mQueue = new LinkedBlockingQueue<>();
    }

    public void addMsg(V msg){
        if (mShouldStop){
            return;
        }

        try {
            mQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
            mShouldStop = true;
        }
    }

    protected void pollQueue(){
        while (!mShouldStop && !Thread.interrupted()){
            try {
                V msg = mQueue.poll(1, TimeUnit.SECONDS);

                if(msg != null){
                    processMsg(msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                mShouldStop = true;
            }
        }
    }

    protected abstract void processMsg(V msg);

    public void start() throws IllegalAccessException {
        if(mThread != null){
            throw new IllegalAccessException();
        }

        mThread = new Thread(this::pollQueue);
        mThread.start();
    }

    public void stop(){
        mShouldStop = true;
        mThread.interrupt();
        mQueue.clear();
    }
}
