package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import main.java.com.github.PeterHausenAoi.evo.entities.Critter;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EvoManager {
    private static final String TAG = EvoManager.class.getSimpleName();

    private int mWidth;
    private int mHeight;
    private int mCellWidth;

    private int mTickRate;
    private long mFrameTime;

    private Grid mGrid;
    List<Critter> mCritters;

    private GraphicsContext mGraphics;
    private Thread mThread;
    private boolean mShouldPause;

    private boolean mShouldStop;

    public EvoManager(GraphicsContext graphics, int width, int height, int cellWidth, int tickRate) {
        this.mGraphics = graphics;

        this.mWidth = width;
        this.mHeight = height;

        this.mCellWidth = cellWidth;

        this.mTickRate = tickRate;
        this.mFrameTime = 1000 / mTickRate;

        mShouldPause = false;
        mShouldStop = false;

        this.mGrid = new Grid(width, height, cellWidth);

        mCritters = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Critter crit = new Critter((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 30,30);
            mCritters.add(crit);
            mGrid.placeEntity(crit);
        }
    }

    private void tick(){
        for (Critter crit : mCritters) {
            crit.tick(mFrameTime, mGrid);
        }
    }

    private void process(){
        while(!mShouldStop){
            long startTime = System.currentTimeMillis();
            tick();
            draw();

            long runtime = System.currentTimeMillis() - startTime;
            Log.doLog(TAG, "Runtime: " + String.valueOf(runtime));
            try {
                Thread.sleep(mFrameTime);

//                while (mShouldPause){
//                    Thread.sleep(mFrameTime);
//                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void playPause(){
//        mShouldPause = !mShouldPause;
    }

    public void start(){
        mThread = new Thread(this::process);
        mThread.start();
    }

    public void draw(){
        mGraphics.clearRect(0,0,mWidth, mHeight);
        mGrid.draw(mGraphics);

        for (Critter crit : mCritters) {
            crit.draw(mGraphics);
        }
    }
}