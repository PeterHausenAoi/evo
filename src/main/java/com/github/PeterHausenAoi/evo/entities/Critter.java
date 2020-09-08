package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.flow.Grid;
import main.java.com.github.PeterHausenAoi.evo.flow.GridCell;
import main.java.com.github.PeterHausenAoi.evo.flow.Tickable;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Critter extends Actor implements Tickable {
    private static final String TAG = Critter.class.getSimpleName();

    private static final Color BOX_COLOR = Color.PURPLE;

    private static final String MOVE_STATE = "MOVE_STATE";
    private static final String ROTATION_STATE = "ROTATION_STATE";

    private Point mTarget;
    private int mWidth;

    private int mHeight;
    private double mAnglePerSec;
    private double mViewDistance = 100.0;

    private double mViewAngle = 90.0;
    private Line2D mViewFocus;
    private Line2D mViewClock;

    private Line2D mViewCounter;

    private double mSpeed = 50.0;

    private Double mCurrangle;
    private Double mTargetAngle;
    private String mPositionState;

    private BaseEntity mTargetEntity;

    public Critter(Point topLeft, Point topRight, Point botLeft, Point botRight) {
        super(topLeft, topRight, botLeft, botRight);
    }

    public Critter(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

        mAnglePerSec = Math.random() * 500 + 1;
        mSpeed = Math.random() * 500 + 50;
        mCurrangle = 0.0;
        mViewDistance = Math.random() * 450 + 50;
        mViewAngle = Math.random() * 160 + 10;


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

    private double calcDist(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private BaseEntity getTarget(Grid grid){
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
            if (ent.equals(this)){
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

    private Point rotatePoint(Point pointToRotate, Point centerPoint, double angle){
        double[] pt = {pointToRotate.getX().doubleValue(), pointToRotate.getY().doubleValue()};

        AffineTransform.getRotateInstance(Math.toRadians(angle), centerPoint.getX().doubleValue(), centerPoint.getY().doubleValue())
                .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
        double newX = pt[0];
        double newY = pt[1];

        return new Point(newX, newY);
    }

    public double angleBetween2Lines(Point2D A1, Point2D A2, Point2D B1, Point2D B2) {
        float angle1 = (float) Math.atan2(A2.getY() - A1.getY(), A1.getX() - A2.getX());
        float angle2 = (float) Math.atan2(B2.getY() - B1.getY(), B1.getX() - B2.getX());

        return (float) Math.toDegrees(angle1 - angle2);
    }

    public boolean isTargetReached(){
        return Objects.equals(mTarget.getX(), mCenter.getX()) && Objects.equals(mTarget.getY(), mCenter.getY());
    }

    double ver1;
    double ver2;

    private void rotate(long frameTime){
        double prevAngle = mCurrangle;
        double turnAngle = mAnglePerSec / 1000 * frameTime;

        mCurrangle += Math.abs(turnAngle);

        if (mCurrangle > Math.abs(mTargetAngle)){
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

        ver1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(), mViewFocus.getP1(), new java.awt.Point(mTarget.getX().intValue(),mTarget.getY().intValue()));
    }

    @Override
    public void tick(long frameTime, Grid grid) {
        if(mTarget == null || isTargetReached()){
            mTarget = new Point(Math.random() * grid.getWidth().doubleValue(), Math.random() * grid.getHeight().doubleValue());

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

        BaseEntity newTarget = getTarget(grid);

        if (mTargetEntity == null && newTarget != null){
            Log.doLog(TAG, "Target acquired");
        }else if(mTargetEntity != null && newTarget == null){
            Log.doLog(TAG, "Target lost");
        }

        mTargetEntity = newTarget;

        if(MOVE_STATE.equals(mPositionState)){
            move(frameTime);
        }else{
            rotate(frameTime);
        }

        grid.placeEntity(this);
        updateAbandonedCells();
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

    @Override
    public void move(long frameTime) {
        double targetX = (mTarget.getX().doubleValue() - mCenter.getX().doubleValue());
        double targetY = (mTarget.getY().doubleValue() - mCenter.getY().doubleValue());

        double dist = Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2));

        double travelDist = mSpeed / 1000.0 * frameTime;
        double ratio = dist / travelDist;

        double ratX = targetX / ratio;
        double ratY = targetY / ratio;

        Point mVect = new Point(ratX, ratY);

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

    @Override
    public void draw(GraphicsContext g) {
        g.setFill(BOX_COLOR);
        double width = mTopRight.getX().doubleValue() - mTopLeft.getX().doubleValue();
        double height = mBotLeft.getY().doubleValue() - mTopLeft.getY().doubleValue();

        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue(), width, height);

        g.setFill(Color.YELLOW);
        g.fillText(ver1 + " | " + ver2, mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 10);

        g.setStroke(Color.AQUA);
        g.setLineWidth(5);
        g.strokeLine(mViewFocus.getX1(), mViewFocus.getY1(), mViewFocus.getX2(), mViewFocus.getY2());

        g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
        g.setLineWidth(5);
        g.strokeLine(mViewClock.getX1(), mViewClock.getY1(), mViewClock.getX2(), mViewClock.getY2());

        g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
        g.setLineWidth(5);
        g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewClock.getX2(), mViewClock.getY2());

        g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
        g.setLineWidth(5);
        g.strokeLine(mViewCounter.getX1(), mViewCounter.getY1(), mViewCounter.getX2(), mViewCounter.getY2());

        g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
        g.setLineWidth(5);
        g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewCounter.getX2(), mViewCounter.getY2());

        g.setStroke(Color.PINK);
        g.setLineWidth(5);
        g.strokeLine(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(), mTarget.getX().doubleValue(), mTarget.getY().doubleValue());
    }

    @Override
    public List<Point> getPoints() {
        return mPoints;
    }
}