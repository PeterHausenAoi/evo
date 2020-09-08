package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import main.java.com.github.PeterHausenAoi.evo.entities.Critter;

import java.util.ArrayList;
import java.util.List;

public class EvoManager {
    private static final String TAG = EvoManager.class.getSimpleName();

    private int mWidth;
    private int mHeight;
    private int mCellWidth;

    private int mTickRate;
    private long mFrameTime;

    private long mSubFrameTime;

    private Grid mGrid;
    List<Critter> mCritters;

    private GraphicsContext mGraphics;

    public EvoManager(GraphicsContext graphics, int width, int height, int cellWidth, int tickRate) {
        this.mGraphics = graphics;

        this.mWidth = width;
        this.mHeight = height;

        this.mCellWidth = cellWidth;

        this.mTickRate = tickRate;
        this.mFrameTime = 1000 / mTickRate;

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

    public void step(long nanoDiff){
        long millidiff = nanoDiff / 1000000;

        mSubFrameTime += millidiff;

        if (mSubFrameTime < mFrameTime){
            return;
        }

        mSubFrameTime = 0;

        tick();
        draw();
    }

    public void draw(){
        mGraphics.clearRect(0,0,mWidth, mHeight);
        mGrid.draw(mGraphics);

        for (Critter crit : mCritters) {
            crit.draw(mGraphics);
        }
    }
}