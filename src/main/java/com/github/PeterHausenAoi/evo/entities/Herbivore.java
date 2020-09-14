package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.flow.Grid;
import main.java.com.github.PeterHausenAoi.evo.flow.GridCell;
import main.java.com.github.PeterHausenAoi.evo.flow.Tickable;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.awt.geom.Line2D;
import java.util.*;

public class Herbivore extends Actor implements Tickable, Edible {
    private static final String TAG = Herbivore.class.getSimpleName();

    private static final Color BOX_COLOR = Color.PURPLE;
    private double mNutrient;

    private Set<BaseEntity> mConePredators = new HashSet<>();

    private Set<BaseEntity> mPrevRadiusPredatorBatch = new HashSet<>();
    private Set<BaseEntity> mRadiusPredators = new HashSet<>();

    public Herbivore(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

        mAnglePerSec = Math.random() * 500 + 1;
        mAnglePerSec = 100;

        mSpeed = Math.random() * 500 + 50;
        mSpeed = 100;

        mCurrangle = 0.0;
//        mViewDistance = Math.random() * 450 + 50;
        mViewDistance = 200;
        mViewAngle = Math.random() * 160 + 10;

        mMaxHealth = Math.random() * 120 + 10;
        mMaxHealth = 200;
        mCurrHealth = mMaxHealth;

        mFoodPriority = Math.random();
        mFoodWeight = Math.random();

        mStarvationRate = Math.random() * 10 + 5;
        mStarvationRate = 1;
        mNutrient = Math.random() * 10 + 10;


        mAudioRadius = Math.random() * 200 + 10;
        mAudioRadius = 150;
        mMaxFleeDist = Math.random() * 900 + 100;
//        mMaxFleeDist = 1000;

        double viewX = Math.random() * 1900;
        double viewY = Math.random() * 900;

        double targetX = (viewX - mCenter.getX().doubleValue());
        double targetY = (viewY - mCenter.getY().doubleValue());

        double dist = Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2));
        double ratio = dist / mViewDistance;

        double testX = targetX / ratio;
        double testY = targetY / ratio;

        mViewFocus = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                mCenter.getX().doubleValue() + testX, mCenter.getY().doubleValue() + testY);

        Point p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), mViewAngle / 2);

        mViewClock = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());

        p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), 360 - mViewAngle / 2 );

        mViewCounter= new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());

    }

    protected Point getInvertedVector(BaseEntity newPredatorEntity){
        double predatorX = newPredatorEntity.getCenter().getX().doubleValue() - mCenter.getX().doubleValue();
        double predatorY = newPredatorEntity.getCenter().getY().doubleValue() - mCenter.getY().doubleValue();

        double fleeX = predatorX * -1;
        double fleeY = predatorY * -1;

        double dist = Math.sqrt(Math.pow(fleeX, 2) + Math.pow(fleeY, 2));
        double ratio = mMaxFleeDist / dist;

        double vectX = fleeX * ratio;
        double vectY = fleeY * ratio;

        return new Point(vectX, vectY);
    }

    @Override
    public void tick(long frameTime, Grid grid) {
        eat();

        if(starve(frameTime)){
            return;
        }

        BaseEntity newTargetEntity = getTarget(grid);

        Set<BaseEntity> newConePredatorEntities = getConePredators(grid);
        mRadiusPredators = getRadiusPredators(grid);

        if ((newConePredatorEntities.size() > 0 || mRadiusPredators.size() > 0) && !Objects.equals(mMode, FLEE_MODE)){
            mMode = FLEE_MODE;
        }

        if (isFoodPrio()){
            Log.doLog(TAG, "FORCE FEED");
            mMode = EAT_MODE;
            mConePredators = new HashSet<>();
            mRadiusPredators = new HashSet<>();
            mPrevRadiusPredatorBatch = new HashSet<>();
            mFleeDist = 0;
        }

        if (mMode.equals(FLEE_MODE)){
            boolean targetAcquired = false;

            if (mCenter.getX().intValue() < 0 || mCenter.getX().intValue() > 1900
                    || mCenter.getY().intValue() < 0 || mCenter.getY().intValue() > 900){

                double x;
                double y;

                double xDiff;
                double yDiff;

                double vectX;
                double vectY;

                if (mCenter.getX().intValue() < 0 || mCenter.getX().intValue() > 1900){
                    x = mCenter.getX().doubleValue() < 0 ? 1 : 1900 - 1;
                    y = mCenter.getY().doubleValue();
                    vectX = 0;

                    xDiff = x - mCenter.getX().doubleValue();
                    yDiff = 0;

                    if(x == 1899 && y == 899){
                        vectY = (mMaxFleeDist - mFleeDist) * -1;
                    }else{
                        double sig = mVect == null ? 0 : Math.signum(mVect.getY().doubleValue());

                        if (sig == 0){
                            sig = mCenter.getY().doubleValue() < 1900 / 2 ? 1 : -1;
                        }

                        vectY = (mMaxFleeDist - mFleeDist) * sig;
                    }
                }else{
                    x = mCenter.getX().doubleValue();
                    y = mCenter.getY().doubleValue() < 0 ? 1 : 900 - 1;
                    vectY = 0;

                    xDiff = 0;
                    yDiff = y - mCenter.getY().doubleValue();

                    double sig = mVect == null ? 0 : Math.signum(mVect.getX().doubleValue());

                    if (sig == 0){
                        sig = mCenter.getX().doubleValue() < 900 / 2 ? 1 : -1;
                    }

                    vectX = (mMaxFleeDist - mFleeDist) * sig;
                }

                mTarget = new Point(x + vectX, y + vectY);
                mCenter = new Point(x, y);

                mViewFocus = new Line2D.Double(mViewFocus.getX1() + xDiff, mViewFocus.getY1() + yDiff,
                        mViewFocus.getX2() + xDiff, mViewFocus.getY2() + yDiff);

                mViewClock = new Line2D.Double(mViewClock.getX1() + xDiff, mViewClock.getY1() + yDiff,
                        mViewClock.getX2() + xDiff, mViewClock.getY2() + yDiff);

                mViewCounter= new Line2D.Double(mViewCounter.getX1() + xDiff, mViewCounter.getY1() + yDiff,
                        mViewCounter.getX2() + xDiff, mViewCounter.getY2() + yDiff);

                mTopLeft = new Point(mCenter.getX().intValue() - mWidth / 2,mCenter.getY().intValue() - mHeight / 2);
                mTopRight = new Point(mCenter.getX().intValue() + mWidth / 2, mCenter.getY().intValue() - mHeight / 2);
                mBotLeft = new Point(mCenter.getX().intValue() - mWidth / 2, mCenter.getY().intValue() + mHeight / 2);
                mBotRight = new Point(mCenter.getX().intValue() + mWidth / 2, mCenter.getY().intValue() + mHeight / 2);

                mPoints = new ArrayList<>();
                mPoints.add(mTopLeft);
                mPoints.add(mTopRight);
                mPoints.add(mBotLeft);
                mPoints.add(mBotRight);

                buildBorders();

                targetAcquired = true;

            }else if (newConePredatorEntities.size() > 0 || mRadiusPredators.size() > 0){
                int prevSize = mConePredators.size();
                mConePredators.addAll(newConePredatorEntities);

                Set<BaseEntity> sum = new HashSet<>();
                sum.addAll(mPrevRadiusPredatorBatch);
                sum.addAll(mRadiusPredators);

                boolean radiusDiff = sum.size() != mPrevRadiusPredatorBatch.size()
                        || sum.size() != mRadiusPredators.size()
                        || mPrevRadiusPredatorBatch.size() != mRadiusPredators.size();

                if (prevSize != mConePredators.size() || radiusDiff){
                    targetAcquired = true;

                    double x = 0;
                    double y = 0;

                    for (BaseEntity pred : mConePredators){
                        Point newVect = getInvertedVector(pred);
                        x += newVect.getX().doubleValue();
                        y += newVect.getY().doubleValue();
                    }

                    for (BaseEntity pred : mRadiusPredators){
                        Point newVect = getInvertedVector(pred);
                        x += newVect.getX().doubleValue();
                        y += newVect.getY().doubleValue();
                    }

                    double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                    double ratio = mMaxFleeDist / dist;

                    double vectX = x * ratio;
                    double vectY = y * ratio;

                    Point newVect = new Point(vectX, vectY);

                    mTarget = new Point(mCenter.getX().doubleValue() + newVect.getX().doubleValue(),
                            mCenter.getY().doubleValue() + newVect.getY().doubleValue());

                    mTargetEntity = null;

                    mPrevRadiusPredatorBatch = mRadiusPredators;
                }
            }

            if(mTarget == null || isTargetReached() || targetAcquired){
                mPositionState = ROTATION_STATE;
                mCurrangle = 0.0;

                double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(), mViewFocus.getP1(), new java.awt.Point(mTarget.getX().intValue(),mTarget.getY().intValue()));
                double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

                if (Math.abs(angle1) < Math.abs(angle2)){
                    mTargetAngle = angle1;
                }else{
                    mTargetAngle = angle2;
                }
            }

        }else{
            boolean targetAcquired = false;
            boolean outOfBounds = false;

            if (newTargetEntity != mTargetEntity){
                if (newTargetEntity == null){
                    mTarget = null;
                    mTargetEntity = null;
                }else{
                    targetAcquired = true;
                    mTarget = new Point(newTargetEntity.getCenter().getX(), newTargetEntity.getCenter().getY());
                    mTargetEntity = newTargetEntity;
                }
            }

            if (mTarget != null) {
                outOfBounds = mCenter.getX().intValue() < 0 || mCenter.getX().intValue() > 1900
                        || mCenter.getY().intValue() < 0 || mCenter.getY().intValue() > 900;
            }

            if (outOfBounds){
                int ddd = 0;
            }

            if(mTarget == null || isTargetReached() || targetAcquired || outOfBounds){
                if (newTargetEntity == null || isTargetReached()){
                    mTarget = new Point(Math.random() * grid.getWidth().doubleValue(), Math.random() * grid.getHeight().doubleValue());
                    mTargetEntity = null;
                }

                mPositionState = ROTATION_STATE;
                mCurrangle = 0.0;

                double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(), mViewFocus.getP1(), new java.awt.Point(mTarget.getX().intValue(),mTarget.getY().intValue()));
                double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

                if (Math.abs(angle1) < Math.abs(angle2)){
                    mTargetAngle = angle1;
                }else{
                    mTargetAngle = angle2;
                }
            }
        }

        if(MOVE_STATE.equals(mPositionState)){
            Point prevPos = mCenter;

            move(frameTime);

            if (mMode.equals(FLEE_MODE)){
                mFleeDist += calcDist(prevPos.getX().doubleValue(), prevPos.getY().doubleValue(),
                        mCenter.getX().doubleValue(), mCenter.getY().doubleValue());

                if (mFleeDist > mMaxFleeDist - 10){
                    mMode = EAT_MODE;
                    mConePredators = new HashSet<>();
                    mRadiusPredators = new HashSet<>();
                    mPrevRadiusPredatorBatch = new HashSet<>();
                    mFleeDist = 0;
                }
            }
        }else{
            rotate(frameTime);

            if (mPositionState.equals(MOVE_STATE)){
                mConePredators = new HashSet<>();
                mRadiusPredators = new HashSet<>();
            }
        }

        grid.placeEntity(this);
        updateAbandonedCells();
    }

    @Override
    protected void initFoodClazzez() {
        Set<Class<? extends BaseEntity>> clazzes = new HashSet<>();
        clazzes.add(Food.class);
        mFoodClazzez = Collections.unmodifiableSet(clazzes);
    }

    @Override
    protected void initPredatorClazzez() {
        Set<Class<? extends BaseEntity>> clazzes = new HashSet<>();
        clazzes.add(Carnivore.class);
        mPredatorClazzez = Collections.unmodifiableSet(clazzes);
    }

    @Override
    public Set<Class<? extends BaseEntity>> getFoodClazzez() {
        return mFoodClazzez;
    }

    @Override
    public Set<Class<? extends BaseEntity>> getPredatorClazzez() {
        return mPredatorClazzez;
    }

    @Override
    public void draw(GraphicsContext g) {
        g.setFill(BOX_COLOR);
        double width = mTopRight.getX().doubleValue() - mTopLeft.getX().doubleValue();
        double height = mBotLeft.getY().doubleValue() - mTopLeft.getY().doubleValue();
        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue(), width, height);

        double hpBar = 50.0;
        g.setFill(Color.DARKRED);
        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 20, hpBar, 10);

        g.setFill(Color.DARKGREEN);
        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 20, hpBar * mCurrHealth / mMaxHealth, 10);

        g.setFill(Color.YELLOW);
        g.fillText(mConePredators.size() + " - " + mRadiusPredators.size(), mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 10);

        g.setStroke(Color.AQUA);
        g.setLineWidth(2);
        g.strokeLine(mViewFocus.getX1(), mViewFocus.getY1(), mViewFocus.getX2(), mViewFocus.getY2());

        g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
        g.setLineWidth(2);
        g.strokeLine(mViewClock.getX1(), mViewClock.getY1(), mViewClock.getX2(), mViewClock.getY2());

        g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
        g.setLineWidth(2);
        g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewClock.getX2(), mViewClock.getY2());

        g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
        g.setLineWidth(2);
        g.strokeLine(mViewCounter.getX1(), mViewCounter.getY1(), mViewCounter.getX2(), mViewCounter.getY2());

        g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
        g.setLineWidth(2);
        g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewCounter.getX2(), mViewCounter.getY2());

        g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
        g.setLineWidth(2);
        g.strokeOval(mCenter.getX().doubleValue() - mAudioRadius, mCenter.getY().doubleValue() - mAudioRadius, mAudioRadius * 2, mAudioRadius * 2);

        g.setStroke(Color.PINK);
        g.setLineWidth(2);
        g.strokeLine(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(), mTarget.getX().doubleValue(), mTarget.getY().doubleValue());
    }

    @Override
    public List<Point> getPoints() {
        return mPoints;
    }

    public boolean isDead() {
        return mDead;
    }

    @Override
    public double getNutrient() {
        return mNutrient;
    }

    @Override
    public void digest() {
//        Log.doLog(TAG, "EATEN");
        mCurrHealth = 0;
        mDead = true;

        for (GridCell cell : mContainers){
            cell.removeEntity(this);
        }

        mContainers.clear();
    }
}