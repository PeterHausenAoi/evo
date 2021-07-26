package com.github.PeterHausenAoi.evo.entities;

import com.github.PeterHausenAoi.evo.evolution.Specimen;
import com.github.PeterHausenAoi.evo.flow.Grid;
import com.github.PeterHausenAoi.evo.flow.GridCell;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

abstract public class Actor extends MovingEntity implements Movable {
    private static final String TAG = Actor.class.getSimpleName();

    protected static final String EAT_MODE = "EAT_MODE";

    protected static final String FLEE_MODE = "FLEE_MODE";
    protected static final String MOVE_STATE = "MOVE_STATE";

    protected static final String ROTATION_STATE = "ROTATION_STATE";

    protected int mGen;

    protected double mAnglePerSec;
    protected double mViewDistance = 100.0;
    protected double mViewAngle = 90.0;

    protected Double mCurrangle;

    protected Double mTargetAngle;
    protected String mPositionState;
    protected BaseEntity mTargetEntity;

    protected String mMode = EAT_MODE;

    protected double mFleeDist;
    protected double mMaxFleeDist;

    protected double mMaxHealth;
    protected double mCurrHealth;

    protected double mAudioRadius;
    protected double mStarvationRate;
    protected boolean mDead = false;

    protected double mFoodPriority;
    protected double mFoodWeight;
    protected Integer mLifeTime;
    protected Integer mKillCount;

    protected Set<Class<? extends BaseEntity>> mFoodClazzez;
    protected Set<Class<? extends BaseEntity>> mPredatorClazzez;

    public Actor(Actor.ActorBuilder builder){
        this(builder.getX(), builder.getY(), builder.getWidth(), builder.getHeight());

        mAnglePerSec = builder.getAnglePerSec();
        mViewDistance = builder.getViewDistance();
        mViewAngle = builder.getViewAngle();
        mSpeed = builder.getSpeed();
        mMaxFleeDist = builder.getMaxFleeDist();
        mMaxHealth = builder.getMaxHealth();
        mAudioRadius = builder.getAudioRadius();
        mStarvationRate = builder.getStarvationRate();
        mFoodPriority = builder.getFoodPriority();
        mFoodWeight = builder.getFoodWeight();
        mGen = builder.getGen();

        mCurrHealth = mMaxHealth;
        mLifeTime = 0;
        mKillCount = 0;
    }

    public Actor(int x, int y, int width, int height) {
        super(x, y, width, height);
        mWidth = width;
        mHeight = height;
        initFoodClazzez();
        initPredatorClazzez();
        mLifeTime = 0;
    }

    protected boolean isFoodPriority(){
        double hunger = 1.0 - getHealthPerc();
        double weightedHunger = hunger * mFoodWeight;
//        Log.doLog(TAG, weightedHunger + " vs " + mFoodPriority);
//        return false;
        return weightedHunger > mFoodPriority;
    }

    protected double getHealthPerc(){
        return mCurrHealth / mMaxHealth;
    }

    protected BaseEntity getTarget(Grid grid){
        List<BaseEntity> ents = new ArrayList<>();
        ents.addAll(grid.getConeEntities(mCenter,
                new Point(mViewFocus.getP2().getX(), mViewFocus.getP2().getY()),
                new Point(mViewClock.getP2().getX(), mViewClock.getP2().getY())
                )
        );

        ents.addAll(grid.getConeEntities(mCenter,
                new Point(mViewFocus.getP2().getX(), mViewFocus.getP2().getY()),
                new Point(mViewCounter.getP2().getX(), mViewCounter.getP2().getY())
                )
        );

        ents.addAll(grid.getRadiusEntities(mCenter, mAudioRadius));

        double minDist = 0;
        BaseEntity minEnt = null;

        for (BaseEntity ent : ents){
            Class<?> cl = ent.getClass();
            if (ent.equals(this) || !(isValidFood(cl))){
                continue;
            }

            double dist = calcDist(ent.getCenter().getX().doubleValue(), ent.getCenter().getY().doubleValue(),
                    mCenter.getX().doubleValue(), mCenter.getY().doubleValue());

            if (dist < minDist || minEnt == null){
                minDist = dist;
                minEnt = ent;
            }
        }

        return minEnt;
    }

    protected Set<BaseEntity> getRadiusPredators(Grid grid){
        Set<BaseEntity> ents = new HashSet<>();
        ents.addAll(grid.getRadiusEntities(mCenter, mAudioRadius));

        return ents.stream().filter(baseEntity -> !baseEntity.equals(this) && isValidPredator(baseEntity.getClass()))
                .collect(Collectors.toSet());
    }

    protected Set<BaseEntity> getConePredators(Grid grid){
        Set<BaseEntity> ents = new HashSet<>();
        ents.addAll(grid.getConeEntities(mCenter,
                new Point(mViewFocus.getP2().getX(), mViewFocus.getP2().getY()),
                new Point(mViewClock.getP2().getX(), mViewClock.getP2().getY())
                )
        );

        ents.addAll(grid.getConeEntities(mCenter,
                new Point(mViewFocus.getP2().getX(), mViewFocus.getP2().getY()),
                new Point(mViewCounter.getP2().getX(), mViewCounter.getP2().getY())
                )
        );

        return ents.stream().filter(baseEntity -> !baseEntity.equals(this) && isValidPredator(baseEntity.getClass()))
                .collect(Collectors.toSet());
    }


    protected boolean isValidPredator(Class<?> foodClazz){
        for (Class<?> clazz : mPredatorClazzez){
            if(foodClazz.isAssignableFrom(clazz)){
                return true;
            }
        }

        return false;
    }

    protected boolean isValidFood(Class<?> foodClazz){
        for (Class<?> clazz : mFoodClazzez){
            if(foodClazz.isAssignableFrom(clazz)){
                return true;
            }
        }

        return false;
    }

    protected void eat(Grid grid){
        for (GridCell cell : mContainers){
            List<BaseEntity> foods = cell.getEntities().stream().filter(baseEntity -> !baseEntity.equals(this)
                    && isValidFood(baseEntity.getClass())
                    && baseEntity instanceof Edible
                    && (baseEntity.isColliding(this) || this.isColliding(baseEntity)))
                    .collect(Collectors.toList());

            for (BaseEntity f : foods){
                if(f instanceof Actor){
                    Actor actor = (Actor)f;
                    if (actor.isDead()){
                        continue;
                    }
                }

                updatePosition(grid, f);

                Edible food = (Edible)f;
                food.digest();
                f.clearContainers();

                this.mKillCount++;
                this.mCurrHealth += food.getNutrient();

                if(this.mCurrHealth > mMaxHealth){
                    mCurrHealth = mMaxHealth;
                }

                if (mTargetEntity == food){
                    mTargetEntity = null;
                    mTarget = null;
                }
            }
        }
    }

    protected boolean starve(long frameTime){
        mCurrHealth -= mStarvationRate / 1000 * frameTime;

        if(mCurrHealth <= 0){
//            Log.doLog(TAG, "STARVED");
            mDead = true;

            for (GridCell cell : mContainers){
                cell.removeEntity(this);
            }

            mContainers.clear();
            return true;
        }

        return false;
    }

    protected Point rotatePoint(Point pointToRotate, Point centerPoint, double angle){
        double[] pt = {pointToRotate.getX().doubleValue(), pointToRotate.getY().doubleValue()};

        AffineTransform.getRotateInstance(Math.toRadians(angle), centerPoint.getX().doubleValue(), centerPoint.getY().doubleValue())
                .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
        double newX = pt[0];
        double newY = pt[1];

        return new Point(newX, newY);
    }

    protected double getViewAngle(){
        double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(),
                mViewFocus.getP1(), new Point2D.Double(mViewFocus.getP1().getX(), mViewFocus.getP1().getY() - 100));
        double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

        double targetAngle;

        if (angle1 < angle2){
            targetAngle = Math.abs(angle1);
        }else{
            targetAngle = Math.abs(angle2);
        }

        return targetAngle;
    }

    protected double angleBetween2Lines(Point2D A1, Point2D A2, Point2D B1, Point2D B2) {
        float angle1 = (float) Math.atan2(A2.getY() - A1.getY(), A1.getX() - A2.getX());
        float angle2 = (float) Math.atan2(B2.getY() - B1.getY(), B1.getX() - B2.getX());

        return (float) Math.toDegrees(angle1 - angle2);
    }

    protected boolean isTargetReached(){
        return Objects.equals(mTarget.getX(), mCenter.getX()) && Objects.equals(mTarget.getY(), mCenter.getY());
    }

    protected double calcDist(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    protected void rotate(long frameTime){
        double prevAngle = mCurrangle;
        double turnAngle = mAnglePerSec / 1000 * frameTime;

        mCurrangle += Math.abs(turnAngle);

        mCurrangle = round(mCurrangle, 4);
        mTargetAngle = round(mTargetAngle, 4);

        if (mCurrangle >= Math.abs(mTargetAngle)){
            mCurrangle = Math.abs(mTargetAngle);
            turnAngle = (mCurrangle - prevAngle);
            mPositionState = MOVE_STATE;
        }

        turnAngle *= Math.signum(mTargetAngle);

        Point p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), turnAngle);

        mViewFocus = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());

        p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), mViewAngle / 2);

        mViewClock = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());

        p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), 360 - mViewAngle / 2 );

        mViewCounter= new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());
    }

    public int getGen() {
        return mGen;
    }

    public boolean isDead(){
        return mDead;
    }

    protected Set<Class<? extends BaseEntity>> getFoodClazzez() {
        return mFoodClazzez;
    }
    public Set<Class<? extends BaseEntity>> getPredatorClazzez() {
        return mPredatorClazzez;
    }

    abstract public void tick(long frameTime, Grid grid);
    abstract protected void initFoodClazzez();
    abstract protected void initPredatorClazzez();

    public abstract Specimen toSpecimen();

    public Integer getLifeTime() {
        return mLifeTime;
    }

    public static class ActorBuilder{
        protected int gen;
        protected int x;
        protected int y;
        protected int mWidth;
        protected int mHeight;
        protected double mAnglePerSec;
        protected double mViewDistance;
        protected double mViewAngle;
        protected double mSpeed;
        protected double mFleeDist;
        protected double mMaxFleeDist;
        protected double mMaxHealth;
        protected double mAudioRadius;
        protected double mStarvationRate;
        protected double mFoodPriority;
        protected double mFoodWeight;

        public ActorBuilder() {
        }

        public int getGen() {
            return gen;
        }

        public ActorBuilder setGen(int gen) {
            this.gen = gen;
            return this;
        }

        public int getX() {
            return x;
        }

        public ActorBuilder setX(int x) {
            this.x = x;
            return this;
        }

        public int getY() {
            return y;
        }

        public ActorBuilder setY(int y) {
            this.y = y;
            return this;
        }

        public int getWidth() {
            return mWidth;
        }

        public ActorBuilder setWidth(int mWidth) {
            this.mWidth = mWidth;
            return this;
        }

        public int getHeight() {
            return mHeight;
        }

        public ActorBuilder setHeight(int mHeight) {
            this.mHeight = mHeight;
            return this;
        }

        public double getAnglePerSec() {
            return mAnglePerSec;
        }

        public ActorBuilder setAnglePerSec(double mAnglePerSec) {
            this.mAnglePerSec = mAnglePerSec;
            return this;
        }

        public double getViewDistance() {
            return mViewDistance;
        }

        public ActorBuilder setViewDistance(double mViewDistance) {
            this.mViewDistance = mViewDistance;
            return this;
        }

        public double getViewAngle() {
            return mViewAngle;
        }

        public ActorBuilder setViewAngle(double mViewAngle) {
            this.mViewAngle = mViewAngle;
            return this;
        }

        public double getSpeed() {
            return mSpeed;
        }

        public ActorBuilder setSpeed(double mSpeed) {
            this.mSpeed = mSpeed;
            return this;
        }

        public double getFleeDist() {
            return mFleeDist;
        }

        public ActorBuilder setFleeDist(double mFleeDist) {
            this.mFleeDist = mFleeDist;
            return this;
        }

        public double getMaxFleeDist() {
            return mMaxFleeDist;
        }

        public ActorBuilder setMaxFleeDist(double mMaxFleeDist) {
            this.mMaxFleeDist = mMaxFleeDist;
            return this;
        }

        public double getMaxHealth() {
            return mMaxHealth;
        }

        public ActorBuilder setMaxHealth(double mMaxHealth) {
            this.mMaxHealth = mMaxHealth;
            return this;
        }

        public double getAudioRadius() {
            return mAudioRadius;
        }

        public ActorBuilder setAudioRadius(double mAudioRadius) {
            this.mAudioRadius = mAudioRadius;
            return this;
        }

        public double getStarvationRate() {
            return mStarvationRate;
        }

        public ActorBuilder setStarvationRate(double mStarvationRate) {
            this.mStarvationRate = mStarvationRate;
            return this;
        }

        public double getFoodPriority() {
            return mFoodPriority;
        }

        public ActorBuilder setFoodPriority(double mFoodPriority) {
            this.mFoodPriority = mFoodPriority;
            return this;
        }

        public double getFoodWeight() {
            return mFoodWeight;
        }

        public ActorBuilder setFoodWeight(double mFoodWeight) {
            this.mFoodWeight = mFoodWeight;
            return this;
        }
    }
}
