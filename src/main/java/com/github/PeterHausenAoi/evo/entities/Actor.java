package main.java.com.github.PeterHausenAoi.evo.entities;

import main.java.com.github.PeterHausenAoi.evo.flow.Grid;
import main.java.com.github.PeterHausenAoi.evo.flow.GridCell;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

abstract public class Actor extends BaseEntity implements Movable {
    private static final String TAG = Actor.class.getSimpleName();

    protected static final String EAT_MODE = "EAT_MODE";
    protected static final String FLEE_MODE = "FLEE_MODE";

    protected static final String MOVE_STATE = "MOVE_STATE";
    protected static final String ROTATION_STATE = "ROTATION_STATE";

    protected Point mTarget;

    protected int mWidth;
    protected int mHeight;

    protected double mAnglePerSec;
    protected double mViewDistance = 100.0;

    protected double mViewAngle = 90.0;
    protected Line2D mViewFocus;
    protected Line2D mViewClock;

    protected Line2D mViewCounter;

    protected double mSpeed = 50.0;

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

    protected Point mVect;

    protected Set<Class<? extends BaseEntity>> mFoodClazzez;
    protected Set<Class<? extends BaseEntity>> mPredatorClazzez;

    public Actor(int x, int y, int width, int height) {
        super(x, y, width, height);
        initFoodClazzez();
        initPredatorClazzez();
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

    protected Set<BaseEntity> getPredators(Grid grid){
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

    protected void eat(){
        for (GridCell cell : mContainers){
            List<BaseEntity> foods = cell.getEntities().stream().filter(baseEntity -> !baseEntity.equals(this)
                    && isValidFood(baseEntity.getClass())
                    && baseEntity instanceof Edible
                    && (baseEntity.isColliding(this) || this.isColliding(baseEntity)))
                    .collect(Collectors.toList());

            for (BaseEntity f : foods){
                f.clearContainers();

                Edible food = (Edible)f;
                food.digest();

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

    @Override
    public void move(long frameTime) {
        double targetX = (mTarget.getX().doubleValue() - mCenter.getX().doubleValue());
        double targetY = (mTarget.getY().doubleValue() - mCenter.getY().doubleValue());

        double dist = Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2));

        double travelDist = mSpeed / 1000.0 * frameTime;

        double ratio = dist / travelDist;

        double ratX = targetX / ratio;
        double ratY = targetY / ratio;

        mVect = new Point(ratX, ratY);

        double newX = mCenter.getX().doubleValue() + mVect.getX().doubleValue();
        double newY = mCenter.getY().doubleValue() + mVect.getY().doubleValue();

        if(mVect.getX().doubleValue() > 0 && newX > mTarget.getX().doubleValue()
                || mVect.getX().doubleValue() < 0 && newX < mTarget.getX().doubleValue()){
            newX = mTarget.getX().doubleValue();
        }

        if(mVect.getY().doubleValue() > 0 && newY > mTarget.getY().doubleValue()
                || mVect.getY().doubleValue() < 0 && newY < mTarget.getY().doubleValue()){
            newY = mTarget.getY().doubleValue();
        }

        double vectX = newX - mCenter.getX().doubleValue();
        double vectY = newY - mCenter.getY().doubleValue();

        mCenter = new Point(newX, newY);
        mViewFocus = new Line2D.Double(mViewFocus.getX1() + vectX, mViewFocus.getY1() + vectY,
                mViewFocus.getX2() + vectX, mViewFocus.getY2() + vectY);

        mViewClock = new Line2D.Double(mViewClock.getX1() + vectX, mViewClock.getY1() + vectY,
                mViewClock.getX2() + vectX, mViewClock.getY2() + vectY);

        mViewCounter= new Line2D.Double(mViewCounter.getX1() + vectX, mViewCounter.getY1() + vectY,
                mViewCounter.getX2() + vectX, mViewCounter.getY2() + vectY);

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
    }

    protected Point rotatePoint(Point pointToRotate, Point centerPoint, double angle){
        double[] pt = {pointToRotate.getX().doubleValue(), pointToRotate.getY().doubleValue()};

        AffineTransform.getRotateInstance(Math.toRadians(angle), centerPoint.getX().doubleValue(), centerPoint.getY().doubleValue())
                .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
        double newX = pt[0];
        double newY = pt[1];

        return new Point(newX, newY);
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

    protected void updateAbandonedCells(){
        List<GridCell> delList = new ArrayList<>();

        for (GridCell cont : mContainers){
            if (!cont.isColliding(this)){
                cont.removeEntity(this);
                delList.add(cont);
            }
        }

        mContainers.removeAll(delList);
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
}