package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import main.java.com.github.PeterHausenAoi.evo.entities.Carnivore;
import main.java.com.github.PeterHausenAoi.evo.entities.Herbivore;
import main.java.com.github.PeterHausenAoi.evo.entities.Food;
import main.java.com.github.PeterHausenAoi.evo.entities.Hunter;
import main.java.com.github.PeterHausenAoi.evo.evolution.EvolutionChamber;
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

    private long mFoodSpawnTickCount = 30 * 2;

    private long mBaseHerbSpawnTime = 30 * 10;
    private long mBaseCarnSpawnTime = 30 * 20;
    private long mBaseHunterSpawnTime = 30 * 50;

    private long mCurrHerbSpawnTime = mBaseHerbSpawnTime;
    private long mCurrCarnSpawnTime = mBaseCarnSpawnTime;
    private long mCurrHunterSpawnTime = mBaseHunterSpawnTime;

    private Grid mGrid;

    List<Herbivore> mHerbivores;
    List<Carnivore> mCarnivores;
    List<Hunter> mHunters;
    List<Food> mFoods;

    EvolutionChamber<Herbivore> mHerbCh;
    EvolutionChamber<Carnivore> mCarnCh;
    EvolutionChamber<Hunter> mHunterCh;

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
        mHunters = new ArrayList<>();

        mHerbCh = new EvolutionChamber<>(Herbivore.getSpeciesDescriptor());
        mCarnCh = new EvolutionChamber<>(Carnivore.getSpeciesDescriptor());
        mHunterCh = new EvolutionChamber<>(Hunter.getSpeciesDescriptor());

        //        for (int i = 0; i < 50; i++) {
//            Herbivore herb = mHerbCh.getNextSpecimen();
//            mHerbivores.add(herb);
//            mGrid.placeEntity(herb);
//        }

//        for (int i = 0; i < 10; i++) {
//            Herbivore herb = new Herbivore((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 20,20);
//            mHerbivores.add(herb);
//            mGrid.placeEntity(herb);
//        }

        for (int i = 0; i < 15; i++) {
            Carnivore car = new Carnivore((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 30,30);
            mCarnivores.add(car);
            mGrid.placeEntity(car);
        }

        for (int i = 0; i < 4; i++) {
            Hunter hunt = new Hunter((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 20,20);
            mHunters.add(hunt);
            mGrid.placeEntity(hunt);
        }
    }

    private void tick(){
        for (Carnivore car : mCarnivores) {
            car.tick(mFrameTime, mGrid);

            if (car.isDead()){
                mCarnCh.addSpecimen(car);
            }
        }

        mCarnivores = mCarnivores.stream().filter(car -> !car.isDead()).collect(Collectors.toList());
        mCurrCarnSpawnTime = mCarnivores.size() == 0 ? mBaseCarnSpawnTime : mBaseCarnSpawnTime / mCarnivores.size();

        for (Herbivore herb : mHerbivores) {
            herb.tick(mFrameTime, mGrid);

            if (herb.isDead()){
                mHerbCh.addSpecimen(herb);
            }
        }

        mHerbivores = mHerbivores.stream().filter(herb -> !herb.isDead()).collect(Collectors.toList());
        mCurrHerbSpawnTime = mHerbivores.size() == 0 ? mBaseHerbSpawnTime : mBaseHerbSpawnTime / mHerbivores.size();

        for (Hunter hunt : mHunters) {
            hunt.tick(mFrameTime, mGrid);

            if (hunt.isDead()){
                mHunterCh.addSpecimen(hunt);
            }
        }

        mHunters = mHunters.stream().filter(hunt -> !hunt.isDead()).collect(Collectors.toList());
        mCurrHunterSpawnTime = mHunters.size() == 0 ? mBaseHunterSpawnTime : mBaseHunterSpawnTime / mHunters.size();
    }

    private void spawnHerbivore(){
        Log.doLog(TAG, "spawnHerbivore");
        Herbivore herb = mHerbCh.getNextSpecimen();
        mHerbivores.add(herb);
        mGrid.placeEntity(herb);
    }

    private void spawnCarnivore(){
        Log.doLog(TAG, "spawnCarnivore");
        Carnivore carn = mCarnCh.getNextSpecimen();
        mCarnivores.add(carn);
        mGrid.placeEntity(carn);
    }

    private void spawnFood(){
//        if (mFoods.size() > mHerbivores.size() * 0.5){
//            return;
//        }

//        Log.doLog(TAG, "spawnFood");
        Food f = new Food((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 50,50);
        f.setHandler(new EvoManager.FoodHandler(f));

        mGrid.placeEntity(f);
        mFoods.add(f);
    }

    long mHerbTickCount = 0;
    long mCarnTickCount = 0;

    long curr = 0;

    public void step(long nanoDiff){
        long millidiff = nanoDiff / 1000000;

        mSubFrameTime += millidiff;

        if (mSubFrameTime < mFrameTime){
            return;
        }

        mTickCount++;
        mHerbTickCount++;
        mCarnTickCount++;

        if(mTickCount % (mFoodSpawnTickCount) == 0){
            Log.doLog(TAG, "|" + (System.currentTimeMillis() - curr));
            curr = System.currentTimeMillis();
            spawnFood();
        }


        if(mHerbTickCount >= mCurrHerbSpawnTime){
//            spawnHerbivore();
            mHerbTickCount = 0;
        }

        if(mCarnTickCount >= mCurrCarnSpawnTime){
//            spawnCarnivore();
            mCarnTickCount = 0;
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

        for (Hunter hunt : mHunters){
            hunt.draw(mGraphics);
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