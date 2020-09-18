package com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import com.github.PeterHausenAoi.evo.flow.EvoManager;
import com.github.PeterHausenAoi.evo.graphics.ImageFactory;
import com.github.PeterHausenAoi.evo.graphics.Resizer;

import java.util.List;

public final class Food extends BaseEntity implements Edible{
    private static final String TAG = Food.class.getSimpleName();

    public static final Color BOX_COLOR = Color.LIGHTGREEN;
    private static final String IMG_CODE = "food.png";

    private int mWidth;
    private int mHeight;

    private EvoManager.FoodHandler handler;

    private double mNutrient;
    private Image mImage;

    public Food(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

        mNutrient = Math.random() * 50 + 10;

        mImage = ImageFactory.getImage(IMG_CODE, new Resizer(width, height, null));
    }

    @Override
    public void draw(GraphicsContext g) {
        if (EvoManager.DEBUG_DISPLAY) {
            g.setFill(BOX_COLOR);
            double width = mTopRight.getX().doubleValue() - mTopLeft.getX().doubleValue();
            double height = mBotLeft.getY().doubleValue() - mTopLeft.getY().doubleValue();

            g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue(), width, height);
        }

        g.drawImage(mImage, mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue());
    }

    @Override
    public List<Point> getPoints() {
        return mPoints;
    }

    public double getNutrient() {
        return mNutrient;
    }

    @Override
    public void digest() {
        if (handler != null){
            mContainers.forEach(gridCell ->gridCell.removeEntity(this));
            handler.handle();
        }
    }

    public void setHandler(EvoManager.FoodHandler handler) {
        this.handler = handler;
    }
}