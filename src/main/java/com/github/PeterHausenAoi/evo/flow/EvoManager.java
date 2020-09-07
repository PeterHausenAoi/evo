package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import main.java.com.github.PeterHausenAoi.evo.entities.Critter;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

public class EvoManager {
    private static final String TAG = EvoManager.class.getSimpleName();

    private int mWidth;
    private int mHeight;
    private int mCellWidth;

    private int mTickRate;
    private long mFrameTime;

    private GraphicsContext mGraphics;
    private Grid mGrid;

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
        crit = new Critter((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 30,30);
        mGrid.placeEntity(crit);
    }
    Critter crit;

    private void tick(){
        crit.tick(mFrameTime, mGrid);
    }

    private void process(){
        while(!mShouldStop){
            tick();
            draw();

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

        crit.draw(mGraphics);
    }
}