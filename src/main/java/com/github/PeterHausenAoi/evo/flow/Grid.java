package main.java.com.github.PeterHausenAoi.evo.flow;

import javafx.scene.canvas.GraphicsContext;
import main.java.com.github.PeterHausenAoi.evo.entities.BaseEntity;
import main.java.com.github.PeterHausenAoi.evo.graphics.Drawable;

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
        for (int x = 0; x < mColcount; x++){
            for (int y = 0; y < mRowCount; y++){
                 GridCell cell = mGrid[x][y];

                 if (cell.isColliding(entity)){
                    cell.addEntity(entity);
                 }
            }
        }
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