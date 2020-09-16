package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.flow.EvoManager;
import main.java.com.github.PeterHausenAoi.evo.graphics.ImageFactory;
import main.java.com.github.PeterHausenAoi.evo.graphics.Resizer;


public class Bullet extends MovingEntity {
    private static final String TAG = Bullet.class.getSimpleName();

    private static final Color BOX_COLOR = Color.BLANCHEDALMOND;
    private static final String IMG_CODE = "fireball.png";

    private Image mImage;

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
        mImage = ImageFactory.getImage(IMG_CODE, new Resizer(width, height, null));
    }

    @Override
    public void draw(GraphicsContext g) {
        if (EvoManager.DEBUG_DISPLAY){
            g.setFill(BOX_COLOR);

            double width = mTopRight.getX().doubleValue() - mTopLeft.getX().doubleValue();
            double height = mBotLeft.getY().doubleValue() - mTopLeft.getY().doubleValue();
            g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue(), width, height);
        }

        g.drawImage(mImage, mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue());
    }


}