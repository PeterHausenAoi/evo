package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import main.java.com.github.PeterHausenAoi.evo.entities.Carnivore;
import main.java.com.github.PeterHausenAoi.evo.entities.Herbivore;
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

    private long mFoodSpawnTime = 5;

    private Grid mGrid;

    List<Herbivore> mHerbivores;
    List<Carnivore> mCarnivores;
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

        mHerbivores = new ArrayList<>();
        mFoods = new ArrayList<>();
        mCarnivores = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Herbivore herb = new Herbivore((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 20,20);
            mHerbivores.add(herb);
            mGrid.placeEntity(herb);
        }

        for (int i = 0; i < 20; i++) {
            Carnivore car = new Carnivore((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 30,30);
            mCarnivores.add(car);
            mGrid.placeEntity(car);
        }
    }

    private void tick(){
        for (Carnivore car : mCarnivores) {
            car.tick(mFrameTime, mGrid);
        }

        mCarnivores = mCarnivores.stream().filter(car -> !car.isDead()).collect(Collectors.toList());

        for (Herbivore herb : mHerbivores) {
            herb.tick(mFrameTime, mGrid);
        }

        mHerbivores = mHerbivores.stream().filter(herb -> !herb.isDead()).collect(Collectors.toList());
    }

    private void spawnFood(){
        if (mFoods.size() > mHerbivores.size() * 0.5){
            return;
        }

//        Log.doLog(TAG, "spawnFood");
        Food f = new Food((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 50,50);
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

        for (Food food : mFoods){
            food.draw(mGraphics);
        }

        for (Herbivore herb : mHerbivores) {
            herb.draw(mGraphics);
        }

        for (Carnivore car : mCarnivores) {
            car.draw(mGraphics);
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
}