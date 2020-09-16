package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.entities.*;
import main.java.com.github.PeterHausenAoi.evo.evolution.EvolutionChamber;
import main.java.com.github.PeterHausenAoi.evo.graphics.ImageFactory;
import main.java.com.github.PeterHausenAoi.evo.graphics.Resizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EvoManager {
    private static final String TAG = EvoManager.class.getSimpleName();
    public static boolean DEBUG_DISPLAY = false;

    private int mWidth;
    private int mHeight;
    private int mCellWidth;

    private int mTickRate;
    private long mFrameTime;

    private long mSubFrameTime;
    private long mTickCount;

    private long mFoodSpawnTickCount = 30 * 1;

    private long mBaseHerbSpawnTime = (int)(30.0 * 10);
    private long mBaseCarnSpawnTime = (int)(30.0 * 45.0);
    private long mBaseHunterSpawnTime = (int)(30.0 * 60.0);

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

    private Image bg;

    public EvoManager(GraphicsContext graphics, int width, int height, int cellWidth, int tickRate) {
        this.mGraphics = graphics;

        this.mWidth = width;
        this.mHeight = height;

        this.mCellWidth = cellWidth;

        this.mTickRate = tickRate;
        this.mFrameTime = 1000 / mTickRate;

        this.mGrid = new Grid(width, height, cellWidth);

        bg = ImageFactory.getImage("bg.png", new Resizer(mWidth, mHeight, null));

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

//        for (int i = 0; i < 1; i++) {
//            Herbivore herb = new Herbivore((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 40,40);
//            mHerbivores.add(herb);
//            mGrid.placeEntity(herb);
//        }
//
//        for (int i = 0; i < 4; i++) {
//            Carnivore car = new Carnivore((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 30,50);
//            mCarnivores.add(car);
//            mGrid.placeEntity(car);
//        }
////
//        for (int i = 0; i < 4; i++) {
//            Hunter hunt = new Hunter((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 30,40);
//            mHunters.add(hunt);
//            mGrid.placeEntity(hunt);
//        }
    }

    public GridCell getCell(int x, int y){
        return mGrid.getCell(x, y);
    }

    private void tick(){
        for (Carnivore car : mCarnivores) {
            if (car.isDead()){
                mCarnCh.addSpecimen(car);
                continue;
            }

            car.tick(mFrameTime, mGrid);

            if (car.isDead()){
                mCarnCh.addSpecimen(car);
            }
        }

        mCarnivores.stream().filter(Carnivore::isDead).forEach(Carnivore::clearContainers);
        mCarnivores = mCarnivores.stream().filter(car -> !car.isDead()).collect(Collectors.toList());
//        mCurrCarnSpawnTime = mCarnivores.size() == 0 ? mBaseCarnSpawnTime : mBaseCarnSpawnTime / mCarnivores.size();

        for (Herbivore herb : mHerbivores) {
            if (herb.isDead()){
                mHerbCh.addSpecimen(herb);
                continue;
            }

            herb.tick(mFrameTime, mGrid);

            if (herb.isDead()){
                mHerbCh.addSpecimen(herb);
            }
        }

        mHerbivores.stream().filter(Herbivore::isDead).forEach(Herbivore::clearContainers);
        mHerbivores = mHerbivores.stream().filter(herb -> !herb.isDead()).collect(Collectors.toList());
//        mCurrHerbSpawnTime = mHerbivores.size() == 0 ? mBaseHerbSpawnTime : mBaseHerbSpawnTime / mHerbivores.size();

        for (Hunter hunt : mHunters) {
            if (hunt.isDead()){
                mHunterCh.addSpecimen(hunt);
                continue;
            }

            hunt.tick(mFrameTime, mGrid);

            if (hunt.isDead()){
                mHunterCh.addSpecimen(hunt);
            }
        }

        mHunters.stream().filter(Hunter::isDead).forEach(Hunter::clearContainers);
        mHunters = mHunters.stream().filter(hunt -> !hunt.isDead()).collect(Collectors.toList());
//        mCurrHunterSpawnTime = mHunters.size() == 0 ? mBaseHunterSpawnTime : mBaseHunterSpawnTime / mHunters.size();
    }

    private long countFoodClazz(List<Edible> eds, List<Class<? extends BaseEntity>> foodClazzez){
        return eds.stream().filter(edible -> {
            for (Class<?> clazz : foodClazzez){
                if(edible.getClass().isAssignableFrom(clazz)){
                    return true;
                }
            }

            return false;
        }).count();
    }

    private void spawnHerbivore(){
//        Log.doLog(TAG, "spawnHerbivore");
        Herbivore herb = mHerbCh.getNextSpecimen();
        mHerbivores.add(herb);
        mGrid.placeEntity(herb);
    }

    private void spawnCarnivore(){
//        Log.doLog(TAG, "spawnCarnivore");
        Carnivore carn = mCarnCh.getNextSpecimen();
        mCarnivores.add(carn);
        mGrid.placeEntity(carn);
    }

    private void spawnHunter(){
//        Log.doLog(TAG, "spawnHunter");
        Hunter hunt = mHunterCh.getNextSpecimen();
        mHunters.add(hunt);
        mGrid.placeEntity(hunt);
    }

    private void spawnFood(){
        if (mFoods.size() > 100){
            return;
        }

//        Log.doLog(TAG, "spawnFood");
        Food f = new Food((int)(Math.random() * mWidth), (int)(Math.random() * mHeight), 40,40);
        f.setHandler(new EvoManager.FoodHandler(f));

        mGrid.placeEntity(f);
        mFoods.add(f);
    }

    long mHerbTickCount = 0;
    long mCarnTickCount = 0;
    long mHunterTickCount = 0;

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
        mHunterTickCount++;

        List<Edible> edibles = new ArrayList<>();

        edibles.addAll(mFoods);
        edibles.addAll(mHerbivores);
        edibles.addAll(mCarnivores);
        edibles.addAll(mHunters);

        long herbFood = countFoodClazz(edibles, Herbivore.FOOD_CLAZZEZ);
        long carnFood = countFoodClazz(edibles, Carnivore.FOOD_CLAZZEZ);
        long hunterFood = countFoodClazz(edibles, Hunter.FOOD_CLAZZEZ);

        mCurrHerbSpawnTime = herbFood == 0 ? mBaseHerbSpawnTime : mBaseHerbSpawnTime / herbFood;
        mCurrCarnSpawnTime = carnFood == 0 ? mBaseCarnSpawnTime : mBaseCarnSpawnTime / carnFood;
        mCurrHunterSpawnTime = hunterFood == 0 ? mBaseHunterSpawnTime : mBaseHunterSpawnTime / hunterFood;

        if(mTickCount % (mFoodSpawnTickCount) == 0){
//            Log.doLog(TAG, "|" + (System.currentTimeMillis() - curr));
            curr = System.currentTimeMillis();
            spawnFood();
        }


        if(mHerbTickCount >= mCurrHerbSpawnTime){
            spawnHerbivore();
            mHerbTickCount = 0;
        }

        if(mCarnTickCount    >= mCurrCarnSpawnTime){
            spawnCarnivore();
            mCarnTickCount = 0;
        }

        if(mHunterTickCount >= mCurrHunterSpawnTime){
            spawnHunter();
            mHunterTickCount = 0;
        }

        mSubFrameTime = 0;

        tick();
        draw();
    }

    public void draw(){
        mGraphics.clearRect(0,0,mWidth, mHeight);
        mGraphics.drawImage(bg, 0, 0);

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

        mGraphics.setStroke(Color.CORNFLOWERBLUE);
        mGraphics.strokeRect(0,0, 1900, 900);
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