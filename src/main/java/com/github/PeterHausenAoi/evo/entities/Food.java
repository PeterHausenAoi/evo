package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.flow.EvoManager;

import java.util.List;

public class Food extends BaseEntity {
    private static final String TAG = Food.class.getSimpleName();

    private static final Color BOX_COLOR = Color.LIGHTGREEN;

    private int mWidth;
    private int mHeight;

    private EvoManager.FoodHandler handler;

    private double mNutrient;

    public Food(Point topLeft, Point topRight, Point botLeft, Point botRight) {
        super(topLeft, topRight, botLeft, botRight);
    }

    public Food(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

        mNutrient = Math.random() * 10 + 10;
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

    public double getNutrient() {
        return mNutrient;
    }

    public EvoManager.FoodHandler getHandler() {
        return handler;
    }

    public void setHandler(EvoManager.FoodHandler handler) {
        this.handler = handler;
    }
}