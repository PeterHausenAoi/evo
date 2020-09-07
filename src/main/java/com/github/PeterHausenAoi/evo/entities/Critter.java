package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.flow.Grid;
import main.java.com.github.PeterHausenAoi.evo.flow.GridCell;
import main.java.com.github.PeterHausenAoi.evo.flow.Tickable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Critter extends Actor implements Tickable {
    private static final String TAG = Critter.class.getSimpleName();

    private static final Color BOX_COLOR = Color.PURPLE;

    private Point mTarget;

    private int mWidth;
    private int mHeight;

    double mSpeed = 100.0;

    public Critter(Point topLeft, Point topRight, Point botLeft, Point botRight) {
        super(topLeft, topRight, botLeft, botRight);
    }

    public Critter(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;
    }

    public boolean isTargetReached(){
        return Objects.equals(mTarget.getX(), mCenter.getX()) && Objects.equals(mTarget.getY(), mCenter.getY());
    }

    @Override
    public void tick(long frameTime, Grid grid) {
        if(mTarget == null || isTargetReached()){
            mTarget = new Point(Math.random() * grid.getWidth().doubleValue(), Math.random() * grid.getHeight().doubleValue());
        }

        move(frameTime);

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

        double travelDist = dist * (mSpeed / 1000);

        double ratX = targetX / travelDist;
        double ratY = targetY / travelDist;

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

        mCenter = new Point(newX, newY);

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
    }

    @Override
    public List<Point> getPoints() {
        return mPoints;
    }
}