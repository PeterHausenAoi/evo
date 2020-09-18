package com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import com.github.PeterHausenAoi.evo.flow.GridCell;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

abstract public class MovingEntity extends BaseEntity implements Movable{
    private static final String TAG = MovingEntity.class.getSimpleName();

    protected int mWidth;
    protected int mHeight;

    protected Point mTarget;
    protected double mSpeed = 50.0;
    protected Point mVect;

    protected Line2D mViewFocus;
    protected Line2D mViewClock;
    protected Line2D mViewCounter;

    public MovingEntity(int x, int y, int width, int height) {
        super(x, y, width, height);
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

        if (mViewFocus != null){
            mViewFocus = new Line2D.Double(mViewFocus.getX1() + vectX, mViewFocus.getY1() + vectY,
                    mViewFocus.getX2() + vectX, mViewFocus.getY2() + vectY);

            mViewClock = new Line2D.Double(mViewClock.getX1() + vectX, mViewClock.getY1() + vectY,
                    mViewClock.getX2() + vectX, mViewClock.getY2() + vectY);

            mViewCounter= new Line2D.Double(mViewCounter.getX1() + vectX, mViewCounter.getY1() + vectY,
                    mViewCounter.getX2() + vectX, mViewCounter.getY2() + vectY);
        }


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

    abstract public void draw(GraphicsContext g);

    @Override
    public List<Point> getPoints() {
        return mPoints;
    }
}