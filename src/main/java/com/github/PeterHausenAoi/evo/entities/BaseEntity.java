package main.java.com.github.PeterHausenAoi.evo.entities;

import main.java.com.github.PeterHausenAoi.evo.flow.GridCell;
import main.java.com.github.PeterHausenAoi.evo.graphics.Drawable;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

abstract public class BaseEntity implements Collidable, Drawable {
    private static final String TAG = BaseEntity.class.getSimpleName();

    protected Point mTopLeft;
    protected Point mTopRight;
    protected Point mBotLeft;
    protected Point mBotRight;
    protected Point mCenter;

    protected Line2D mTop;
    protected Line2D mLeft;
    protected Line2D mRight;
    protected Line2D mBot;

    protected List<Line2D> mBorders;
    protected List<Point> mPoints;

    protected List<GridCell> mContainers;

    public BaseEntity(Point topLeft, Point topRight, Point botLeft, Point botRight) {
        this.mTopLeft = topLeft;
        this.mTopRight = topRight;
        this.mBotLeft = botLeft;
        this.mBotRight = botRight;

        mPoints = new ArrayList<>();
        mPoints.add(mTopLeft);
        mPoints.add(mTopRight);
        mPoints.add(mBotLeft);
        mPoints.add(mBotRight);

        mContainers = new ArrayList<>();
        buildBorders();
    }

    public BaseEntity(int x, int y, int width, int height) {
        mTopLeft = new Point(x,y);
        mTopRight = new Point(x + width, y);
        mBotLeft = new Point(x,y + height);
        mBotRight = new Point(x + width,y + height);
        mCenter = new Point(x + width / 2, y + height / 2);

        mPoints = new ArrayList<>();
        mPoints.add(mTopLeft);
        mPoints.add(mTopRight);
        mPoints.add(mBotLeft);
        mPoints.add(mBotRight);

        mContainers = new ArrayList<>();
        buildBorders();
    }

    protected void buildBorders(){
        mTop = new Line2D.Double(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue(),
                mTopRight.getX().doubleValue(), mTopRight.getY().doubleValue());

        mLeft = new Line2D.Double(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue(),
                mBotLeft.getX().doubleValue(), mBotLeft.getY().doubleValue());

        mBot = new Line2D.Double(mBotLeft.getX().doubleValue(), mBotLeft.getY().doubleValue(),
                mBotRight.getX().doubleValue(), mBotRight.getY().doubleValue());

        mRight = new Line2D.Double(mBotRight.getX().doubleValue(), mBotRight.getY().doubleValue(),
                mTopRight.getX().doubleValue(), mTopRight.getY().doubleValue());

        mBorders = new ArrayList<>();
        mBorders.add(mTop);
        mBorders.add(mLeft);
        mBorders.add(mRight);
        mBorders.add(mBot);
    }

    public void addContainer(GridCell cell){
        mContainers.add(cell);
    }

    public boolean isColliding(Collidable other){
        for (Line2D line : mBorders){
            for (Line2D otherLine : other.getBorders()){
                if (line.intersectsLine(otherLine)){
                    return true;
                }
            }
        }

        return false;
    }

    public List<Line2D> getBorders(){
        return mBorders;
    }

    public Point getTopLeft() {
        return mTopLeft;
    }

    public Point getTopRight() {
        return mTopRight;
    }

    public Point getBotLeft() {
        return mBotLeft;
    }

    public Point getBotRight() {
        return mBotRight;
    }

    public Line2D getTop() {
        return mTop;
    }

    public Line2D getLeft() {
        return mLeft;
    }

    public Line2D getRight() {
        return mRight;
    }

    public Line2D getBot() {
        return mBot;
    }
}