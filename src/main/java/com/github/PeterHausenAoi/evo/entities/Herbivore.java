package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.flow.Grid;
import main.java.com.github.PeterHausenAoi.evo.flow.GridCell;
import main.java.com.github.PeterHausenAoi.evo.flow.Tickable;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Herbivore extends Actor implements Tickable, Edible {
    private static final String TAG = Herbivore.class.getSimpleName();

    private static final Color BOX_COLOR = Color.PURPLE;
    private double mNutrient;

    public Herbivore(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

        mAnglePerSec = Math.random() * 500 + 1;

        mSpeed = Math.random() * 500 + 50;
//        mSpeed = 100;

        mCurrangle = 0.0;
        mViewDistance = Math.random() * 450 + 50;
        mViewAngle = Math.random() * 160 + 10;

        mMaxHealth = Math.random() * 120 + 10;
        mCurrHealth = mMaxHealth;

        mStarvationRate = Math.random() * 10 + 5;
//        mStarvationRate = 1;

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

        mNutrient = Math.random() * 10 + 10;
    }



    @Override
    public void tick(long frameTime, Grid grid) {
        eat();

        if(starve(frameTime)){
            return;
        }

        BaseEntity newTargetEntity = getTarget(grid);
        BaseEntity newPredatorEntity = getPredator(grid);

//        if (newPredatorEntity != null){
//            mMode = FLEE_MODE;
//        }else{
//            mMode = EAT_MODE;
//            mFleeTime = 0;
//        }

        if (mMode.equals(FLEE_MODE)){
            mFleeTime++;
        }else{
            boolean targetAcquired = false;

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

            if(mTarget == null || isTargetReached() || targetAcquired){
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
            move(frameTime);
        }else{
            rotate(frameTime);
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

//        g.setFill(Color.YELLOW);
//        g.fillText(ver1 + " | " + ver2, mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 10);

        double hpBar = 50.0;
        g.setFill(Color.DARKRED);
        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 20, hpBar, 10);

        g.setFill(Color.DARKGREEN);
        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 20, hpBar * mCurrHealth / mMaxHealth, 10);

        g.setStroke(Color.AQUA);
        g.setLineWidth(2);
        g.strokeLine(mViewFocus.getX1(), mViewFocus.getY1(), mViewFocus.getX2(), mViewFocus.getY2());

        g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
        g.setLineWidth(2);
        g.strokeLine(mViewClock.getX1(), mViewClock.getY1(), mViewClock.getX2(), mViewClock.getY2());

        g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
        g.setLineWidth(2);
        g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewClock.getX2(), mViewClock.getY2());

        g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
        g.setLineWidth(2);
        g.strokeLine(mViewCounter.getX1(), mViewCounter.getY1(), mViewCounter.getX2(), mViewCounter.getY2());

        g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
        g.setLineWidth(2);
        g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewCounter.getX2(), mViewCounter.getY2());

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
        Log.doLog(TAG, "EATEN");
        mCurrHealth = 0;
        mDead = true;

        for (GridCell cell : mContainers){
            cell.removeEntity(this);
        }

        mContainers.clear();
    }
}