package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import main.java.com.github.PeterHausenAoi.evo.entities.BaseEntity;
import main.java.com.github.PeterHausenAoi.evo.entities.Point;
import main.java.com.github.PeterHausenAoi.evo.graphics.Drawable;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grid implements Drawable {
    private static final String TAG = Grid.class.getSimpleName();

    private int mWidth;
    private int mHeight;

    private int mColcount;
    private int mRowCount;

    private int mCellWidth;

    private GridCell[][] mGrid;

    public Grid(int width, int height, int cellWidth) {
        this.mWidth = width;
        this.mHeight = height;
        this.mColcount = mWidth / cellWidth;
        this.mRowCount = mHeight / cellWidth;
        this.mCellWidth = cellWidth;

        mGrid = new GridCell[mColcount][mRowCount];

        for (int x = 0; x < mColcount; x++){
            for (int y = 0; y < mRowCount; y++){
                mGrid[x][y] = new GridCell(x * mCellWidth, y * mCellWidth, mCellWidth);
            }
        }
    }

    public void placeEntity(BaseEntity entity){
        int startX = (entity.getTopLeft().getX().intValue() / mCellWidth) - 1;
        if(startX < 0){
            startX = 0;
        }

        int endX = (entity.getBotRight().getX().intValue() / mCellWidth) + 1;
        if(endX >= mColcount){
            endX = mColcount - 1;
        }

        int startY = (entity.getTopLeft().getY().intValue() / mCellWidth) - 1;
        if(startY < 0){
            startY = 0;
        }

        int endY = (entity.getBotRight().getY().intValue() / mCellWidth) + 1;
        if(endY >= mRowCount){
            endY = mRowCount - 1;
        }

        for (int x = startX; x <= endX; x++){
            for (int y = startY; y <= endY; y++){
                 GridCell cell = mGrid[x][y];

                 if (cell.isColliding(entity)){
                    cell.addEntity(entity);
                 }
            }
        }
    }

    public List<BaseEntity> getConeEntities(Point p1, Point p2, Point p3){
        int topLeftX;
        int topLeftY;
        int botRightX;
        int botRightY;

        topLeftX = Math.min(p1.getX().intValue(), p2.getX().intValue());
        topLeftX = Math.min(p3.getX().intValue(), topLeftX);

        topLeftY = Math.min(p1.getY().intValue(), p2.getY().intValue());
        topLeftY = Math.min(p3.getY().intValue(), topLeftY);

        botRightX = Math.max(p1.getX().intValue(), p2.getX().intValue());
        botRightX = Math.max(p3.getX().intValue(), botRightX);

        botRightY = Math.max(p1.getY().intValue(), p2.getY().intValue());
        botRightY = Math.max(p3.getY().intValue(), botRightY);

        int startX = (topLeftX / mCellWidth) - 1;
        if(startX < 0){
            startX = 0;
        }

        int endX = (botRightX / mCellWidth) + 1;
        if(endX >= mColcount){
            endX = mColcount - 1;
        }

        int startY = (topLeftY / mCellWidth) - 1;
        if(startY < 0){
            startY = 0;
        }

        int endY = (botRightY / mCellWidth) + 1;
        if(endY >= mRowCount){
            endY = mRowCount - 1;
        }

        Set<BaseEntity> ents = new HashSet<>();

        for (int x = startX; x <= endX; x++){
            for (int y = startY; y <= endY; y++){
                GridCell cell = mGrid[x][y];

                ents.addAll(cell.getEntities());
            }
        }

        List<BaseEntity> coneEnts = new ArrayList<>();

        List<Line2D> coneBorders = new ArrayList<>();
        coneBorders.add(new Line2D.Double(p1.getX().doubleValue(), p1.getY().doubleValue(), p2.getX().doubleValue(), p2.getY().doubleValue()));
        coneBorders.add(new Line2D.Double(p1.getX().doubleValue(), p1.getY().doubleValue(), p3.getX().doubleValue(), p3.getY().doubleValue()));
        coneBorders.add(new Line2D.Double(p2.getX().doubleValue(), p2.getY().doubleValue(), p3.getX().doubleValue(), p3.getY().doubleValue()));

        for (BaseEntity ent : ents){
            List<Point> entPoints = ent.getPoints();

            for (Point p : entPoints){
                if(containsCone(p, p1, p2, p3)){
                    coneEnts.add(ent);
                    break;
                }
            }

            if (coneEnts.contains(ent)){
                continue;
            }

            List<Line2D> entBorders = ent.getBorders();

            for (Line2D entBorder : entBorders){
                for (Line2D coneBorder : coneBorders){
                    if(entBorder.intersectsLine(coneBorder)){
                        coneEnts.add(ent);
                        break;
                    }
                }

                if (coneEnts.contains(ent)){
                    break;
                }
            }
        }

        return coneEnts;
    }

    private boolean containsCone(Point p, Point p1, Point p2, Point p3){
        float alpha = ((p2.getY().floatValue() - p3.getY().floatValue())*(p.getX().floatValue() - p3.getX().floatValue()) + (p3.getX().floatValue() - p2.getX().floatValue())*(p.getY().floatValue() - p3.getY().floatValue())) /
                ((p2.getY().floatValue() - p3.getY().floatValue())*(p1.getX().floatValue() - p3.getX().floatValue()) + (p3.getX().floatValue() - p2.getX().floatValue())*(p1.getY().floatValue() - p3.getY().floatValue()));
        float beta = ((p3.getY().floatValue() - p1.getY().floatValue())*(p.getX().floatValue() - p3.getX().floatValue()) + (p1.getX().floatValue() - p3.getX().floatValue())*(p.getY().floatValue() - p3.getY().floatValue())) /
                ((p2.getY().floatValue() - p3.getY().floatValue())*(p1.getX().floatValue() - p3.getX().floatValue()) + (p3.getX().floatValue() - p2.getX().floatValue())*(p1.getY().floatValue() - p3.getY().floatValue()));
        float gamma = 1.0f - alpha - beta;

        return alpha > 0 && beta > 0 && gamma > 0;
    }

    @Override
    public void draw(GraphicsContext g) {
        for (int x = 0; x < mGrid.length; x++){
            for (int y = 0; y < mGrid[x].length; y++){
                mGrid[x][y].draw(g);
            }
        }
    }

    public Number getWidth() {
        return mWidth;
    }

    public Number getHeight() {
        return mHeight;
    }
}