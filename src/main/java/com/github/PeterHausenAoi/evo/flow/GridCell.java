package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.entities.BaseEntity;
import main.java.com.github.PeterHausenAoi.evo.entities.Collidable;
import main.java.com.github.PeterHausenAoi.evo.entities.Point;
import main.java.com.github.PeterHausenAoi.evo.graphics.Drawable;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class GridCell implements Drawable, Collidable {
    private static final String TAG = GridCell.class.getSimpleName();

    private static final Color OCCUPIED = Color.RED;
    private static final Color UNOCCUPIED = Color.BLACK;
    private static final Color BORDER = Color.GREEN;

    private static final double BORDER_WIDTH = 4.0;

    private int mX;
    private int mY;

    private int mWidth;

    protected Point mTopLeft;
    protected Point mTopRight;
    protected Point mBotLeft;
    protected Point mBotRight;

    protected Line2D mTop;
    protected Line2D mLeft;
    protected Line2D mRight;
    protected Line2D mBot;

    protected List<Line2D> mBorders;
    protected List<Point> mPoints;

    private List<BaseEntity> mEntities;

    public GridCell(int x, int y, int width) {
        this.mX = x;
        this.mY = y;
        this.mWidth = width;

        mTopLeft = new Point(x,y);
        mTopRight = new Point(x + width, y);
        mBotLeft = new Point(x,y + width);
        mBotRight = new Point(x + width,y + width);

        mPoints = new ArrayList<>();
        mPoints.add(mTopLeft);
        mPoints.add(mTopRight);
        mPoints.add(mBotLeft);
        mPoints.add(mBotRight);

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

        mEntities = new ArrayList<>();
    }

    public void removeEntity(BaseEntity entity){
        mEntities.remove(entity);
    }

    public void addEntity(BaseEntity entity){
        if (!mEntities.contains(entity)){
            mEntities.add(entity);
            entity.addContainer(this);
        }
    }

    @Override
    public void draw(GraphicsContext g) {
        g.setFill(mEntities.size() == 0 ? UNOCCUPIED : OCCUPIED);
        g.fillRect(mX, mY, mWidth, mWidth);

        g.setStroke(BORDER);
        g.setLineWidth(BORDER_WIDTH);
        g.strokeRect(mX, mY, mWidth, mWidth);
    }

    @Override
    public boolean isColliding(Collidable other) {
        if(other.isColliding(this)){
            return true;
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

    @Override
    public Point getTopLeft() {
        return this.mTopLeft;
    }

    @Override
    public Point getTopRight() {
        return mTopRight;
    }

    @Override
    public Point getBotLeft() {
        return mBotLeft;
    }

    @Override
    public Point getBotRight() {
        return mBotRight;
    }

    @Override
    public Line2D getTop() {
        return mTop;
    }

    @Override
    public Line2D getLeft() {
        return mLeft;
    }

    @Override
    public Line2D getRight() {
        return mRight;
    }

    @Override
    public Line2D getBot() {
        return mBot;
    }

    @Override
    public List<Point> getPoints() {
        return mPoints;
    }

    @Override
    public List<Line2D> getBorders() {
        return mBorders;
    }
}