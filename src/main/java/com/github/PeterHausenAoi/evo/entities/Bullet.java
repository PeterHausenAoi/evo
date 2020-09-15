package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.geom.Line2D;
import java.util.ArrayList;


public class Bullet extends MovingEntity {
    private static final String TAG = Bullet.class.getSimpleName();

    private static final Color BOX_COLOR = Color.BLANCHEDALMOND;

    public Bullet(int x, int y, int width, int height, Point target, double speed) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;
        mSpeed = speed;

        double targetX = (target.getX().doubleValue() - mCenter.getX().doubleValue());
        double targetY = (target.getY().doubleValue() - mCenter.getY().doubleValue());

        double dist = Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2));

        double travelDist = 3000;

        double ratio = dist / travelDist;

        double ratX = targetX / ratio;
        double ratY = targetY / ratio;

        mVect = new Point(ratX, ratY);

        mTarget = new Point(target.getX().intValue() + mVect.getX().intValue(), target.getY().intValue() + mVect.getY().intValue());
    }

    @Override
    public void draw(GraphicsContext g) {
        g.setFill(BOX_COLOR);
        double width = mTopRight.getX().doubleValue() - mTopLeft.getX().doubleValue();
        double height = mBotLeft.getY().doubleValue() - mTopLeft.getY().doubleValue();
        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue(), width, height);
    }


}