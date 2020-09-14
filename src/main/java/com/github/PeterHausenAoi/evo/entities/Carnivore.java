package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.flow.Grid;

import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Carnivore extends Actor {
    private static final String TAG = Carnivore.class.getSimpleName();

    private static final Color BOX_COLOR = Color.BLUE;

    public Carnivore(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

        mAnglePerSec = Math.random() * 500 + 1;
        mSpeed = Math.random() * 500 + 50;
//        mSpeed = 10;

        mCurrangle = 0.0;
        mViewDistance = Math.random() * 450 + 50;
        mViewAngle = Math.random() * 160 + 10;

        mMaxHealth = Math.random() * 120 + 100;
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
    }

    public boolean isDead() {
        return mDead;
    }

    @Override
    public void tick(long frameTime, Grid grid) {
        eat();

        if(starve(frameTime)){
            return;
        }

        BaseEntity newTarget = getTarget(grid);
        boolean targetAcquired = false;

        if (newTarget != mTargetEntity){
            if (newTarget == null){
                mTarget = null;
                mTargetEntity = null;
            }else{
                targetAcquired = true;
                mTarget = new Point(newTarget.getCenter().getX(), newTarget.getCenter().getY());
                mTargetEntity = newTarget;
            }
        }else if (newTarget != null){
            double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(), mViewFocus.getP1(),
                    new java.awt.Point(mTargetEntity.getCenter().getX().intValue(),
                            mTargetEntity.getCenter().getY().intValue()));
            double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

            double targetAngle;

            if (Math.abs(angle1) < Math.abs(angle2)){
                targetAngle = Math.abs(round(angle1, 0));
            }else{
                targetAngle = Math.abs(round(angle2, 0));
            }

            if (Math.abs(targetAngle - round(mCurrangle,0)) > 1){
                mPositionState = ROTATION_STATE;
                targetAcquired = true;
                mTarget = new Point(newTarget.getCenter().getX(), newTarget.getCenter().getY());
            }else{
                mPositionState = MOVE_STATE;
                mTarget = new Point(newTarget.getCenter().getX(), newTarget.getCenter().getY());
                targetAcquired = false;
            }
        }

        if(mTarget == null || isTargetReached() || targetAcquired){
            if (newTarget == null || isTargetReached()){
                mTarget = new Point(Math.random() * grid.getWidth().doubleValue(), Math.random() * grid.getHeight().doubleValue());
                mTargetEntity = null;
            }

            mPositionState = ROTATION_STATE;
            mCurrangle = 0.0;

            double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(),
                    mViewFocus.getP1(), new java.awt.Point(mTarget.getX().intValue(),mTarget.getY().intValue()));
            double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

            if (Math.abs(angle1) < Math.abs(angle2)){
                mTargetAngle = round(angle1, 4);
            }else{
                mTargetAngle = round(angle2, 4);
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
        clazzes.add(Herbivore.class);
        mFoodClazzez = Collections.unmodifiableSet(clazzes);
    }

    @Override
    protected void initPredatorClazzez() {
        Set<Class<? extends BaseEntity>> clazzes = new HashSet<>();
        mPredatorClazzez = Collections.unmodifiableSet(clazzes);
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
}