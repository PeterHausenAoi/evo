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

    private Point mTarget;

    private int mWidth;
    private int mHeight;

    private double mAnglePerSec = 10.0;
    private double mViewDistance = 100.0;
    private double mViewAngle = 90.0;

    private Line2D mViewFocus;
    private Line2D mViewClock;
    private Line2D mViewCounter;

    private double mSpeed = 50.0;

    public Critter(Point topLeft, Point topRight, Point botLeft, Point botRight) {
        super(topLeft, topRight, botLeft, botRight);
    }

    public Critter(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

//        mSpeed = Math.random() * 500 + 50;

        double viewX = Math.random() * 1900;
        double viewY = Math.random() * 900;

        double targetX = (viewX - mCenter.getX().doubleValue());
        double targetY = (viewY - mCenter.getY().doubleValue());

        double dist = Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2));
        double ratio = dist / mViewDistance;

        double testX = targetX / ratio;
        double testY = targetY / ratio;

//        double testX = 100;
//        double testY = 100;

        mViewFocus = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                mCenter.getX().doubleValue() + testX, mCenter.getY().doubleValue() + testY);

        Point p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), mViewAngle / 2);

        mViewClock = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());

        p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), 360 - mViewAngle / 2 );

        mViewCounter= new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());

//        double length = Math.sqrt(Math.pow(mViewFocus.getX2() - mViewFocus.getX1(), 2)+Math.pow(mViewFocus.getY2() - mViewFocus.getY1(), 2));
//        double xChange = length * Math.cos(Math.toRadians(mViewAngle / 2));
//        double yChange = length * Math.sin(Math.toRadians(mViewAngle / 2));
//
//        mViewClock= new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
//                mViewFocus.getX2() + xChange, mViewFocus.getY2() + yChange);
//
//        length = Math.sqrt(Math.pow(mViewFocus.getX2() - mViewFocus.getX1(), 2)+Math.pow(mViewFocus.getY2() - mViewFocus.getY1(), 2));
//        xChange = length * Math.cos(Math.toRadians(mViewAngle / 2 * -1));
//        yChange = length * Math.sin(Math.toRadians(mViewAngle / 2 * -1));
//
//        mViewCounter= new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
//                mViewFocus.getX2() + xChange, mViewFocus.getY2() + yChange);
    }

    Point rotatePoint(Point pointToRotate, Point centerPoint, double angle){
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
        float calculatedAngle = (float) Math.toDegrees(angle1 - angle2);

        return calculatedAngle;
    }

    public boolean isTargetReached(){
        return Objects.equals(mTarget.getX(), mCenter.getX()) && Objects.equals(mTarget.getY(), mCenter.getY());
    }

    public boolean checkAngle(){
//        double slope1 = (mTarget.getY().doubleValue() - mCenter.getY().doubleValue()) / (mTarget.getX().doubleValue() - mCenter.getX().doubleValue());
//        double ang1 = Math.atan(slope1) * 180.0 / Math.PI;
//
//        double slope2 = (mViewFocus.getY2() - mViewFocus.getY1()) / (mViewFocus.getX2() - mViewFocus.getX1());
//        double ang2 = Math.atan(slope2) * 180.0 / Math.PI;
//
//        ver1 = ang2 - ang1;
//        ver2 = ang1 - ang2;
//
//        Log.doLog(TAG, ver1 + " | " + ver2);
//
//        double angle1 = Math.atan2((mCenter.getY().doubleValue() - mTarget.getY().doubleValue() ), (mCenter.getX().doubleValue() - mTarget.getX().doubleValue() ));
//        double angle2 = Math.atan2((mViewFocus.getY1() - mViewFocus.getY2()), (mViewFocus.getX1() - mViewFocus.getX2()));
//        double ang = angle1 - angle2;
////        if(k < 0){
////            k = k * -1;
////        }
//
//        ver1 = 180 / Math.PI *  Math.atan(ang);

        return false;
    }

    double ver1;
    double ver2;

    @Override
    public void tick(long frameTime, Grid grid) {
        if(mTarget == null || isTargetReached()){
            mTarget = new Point(Math.random() * grid.getWidth().doubleValue(), Math.random() * grid.getHeight().doubleValue());
        }

        move(frameTime);

        ver1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(), mViewFocus.getP1(), new java.awt.Point(mTarget.getX().intValue(),mTarget.getY().intValue()));
//        ver2 = 360 - ver1;

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

        g.setStroke(Color.GREY);
        g.setLineWidth(5);
        g.strokeLine(mViewClock.getX1(), mViewClock.getY1(), mViewClock.getX2(), mViewClock.getY2());

        g.setStroke(Color.GREY);
        g.setLineWidth(5);
        g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewClock.getX2(), mViewClock.getY2());

        g.setStroke(Color.GREY);
        g.setLineWidth(5);
        g.strokeLine(mViewCounter.getX1(), mViewCounter.getY1(), mViewCounter.getX2(), mViewCounter.getY2());

        g.setStroke(Color.GREY);
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