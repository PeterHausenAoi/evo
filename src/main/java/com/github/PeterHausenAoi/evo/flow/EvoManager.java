package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import main.java.com.github.PeterHausenAoi.evo.entities.Critter;
import main.java.com.github.PeterHausenAoi.evo.entities.Food;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EvoManager {
    private static final String TAG = EvoManager.class.getSimpleName();

    private int mWidth;
    private int mHeight;
    private int mCellWidth;

    private int mTickRate;
    private long mFrameTime;

    private long mSubFrameTime;
    private long mTickCount;

    private long mFoodSpawnTime = 10;

    private Grid mGrid;

    List<Critter> mCritters;
    List<Food> mFoods;

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
        mFoods = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Critter crit = new Critter((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 30,30);
            mCritters.add(crit);
            mGrid.placeEntity(crit);
        }
    }

    private void tick(){
        for (Critter crit : mCritters) {
            crit.tick(mFrameTime, mGrid);
        }

        mCritters = mCritters.stream().filter(critter -> !critter.isDead()).collect(Collectors.toList());
    }

    private void spawnFood(){
        if (mFoods.size() > mCritters.size() * 0.2){
            return;
        }

        Log.doLog(TAG, "spawnFood");
        Food f = new Food((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 30,30);
        f.setHandler(new EvoManager.FoodHandler(f));

        mGrid.placeEntity(f);
        mFoods.add(f);
    }

    public void step(long nanoDiff){
        long millidiff = nanoDiff / 1000000;

        mSubFrameTime += millidiff;

        if (mSubFrameTime < mFrameTime){
            return;
        }

        mTickCount++;

        if(mTickCount % (mFoodSpawnTime) == 0){
            spawnFood();
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

        for (Food food : mFoods){
            food.draw(mGraphics);
        }
    }

    public class FoodHandler{
        Food mFood;

        public FoodHandler(Food food) {
            this.mFood = food;
        }

        public void handle(){
            EvoManager.this.mFoods.remove(mFood);
        }
    }

    public class CritterHandler{
        Critter mCrit;

        public CritterHandler(Critter crit) {
            this.mCrit = crit;
        }

        public void handle(){
            EvoManager.this.mCritters.remove(mCrit);
        }
    }
}