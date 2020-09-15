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

    public BaseEntity(int x, int y, int width, int height) {
        mCenter = new Point(x, y);

        mTopLeft = new Point(x - width / 2, y - height / 2);
        mTopRight = new Point(x + width / 2, y - height / 2);
        mBotLeft = new Point(x - width / 2, y + height / 2);
        mBotRight = new Point(x + width / 2, y + height / 2);

        mPoints = new ArrayList<>();
        mPoints.add(mTopLeft);
        mPoints.add(mTopRight);
        mPoints.add(mBotLeft);
        mPoints.add(mBotRight);

        mContainers = new ArrayList<>();
        buildBorders();
    }

    protected double round(double value, int prec){
        return (double)Math.round(value * (Math.pow(10, prec))) / Math.pow(10, prec);
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

    public void clearContainers(){
        mContainers.stream().forEach(gridCell -> gridCell.removeEntity(this));
        mContainers.clear();
    }

    public boolean isColliding(Collidable other){
        for (Line2D line : mBorders){
            for (Line2D otherLine : other.getBorders()){
                if (line.intersectsLine(otherLine)){
                    return true;
                }
            }
        }

        List<Point> points = other.getPoints();

        for (Point p : points){
            if (mTopLeft.getX().doubleValue() <= p.getX().doubleValue()
                    && p.getX().doubleValue() <= mBotRight.getX().doubleValue()
                    && mTopLeft.getY().doubleValue() <= p.getY().doubleValue()
                    && p.getY().doubleValue() <= mBotRight.getY().doubleValue()){
                return true;
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

    public Point getCenter() {
        return mCenter;
    }
}