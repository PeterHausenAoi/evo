package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.evolution.SpeciesDescriptor;
import main.java.com.github.PeterHausenAoi.evo.evolution.SpeciesParam;
import main.java.com.github.PeterHausenAoi.evo.evolution.Specimen;
import main.java.com.github.PeterHausenAoi.evo.flow.Grid;

import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Hunter extends Actor {
    private static final String TAG = Hunter.class.getSimpleName();

    private static final Color BOX_COLOR = Color.YELLOW;

    private static final String KEY_X = "KEY_X";
    private static final String KEY_Y = "KEY_Y";
    private static final String KEY_WIDTH = "KEY_WIDTH";
    private static final String KEY_HEIGHT = "KEY_HEIGHT";
    private static final String KEY_ANGLEPERSEC = "KEY_ANGLEPERSEC";
    private static final String KEY_VIEWDISTANCE = "KEY_VIEWDISTANCE";
    private static final String KEY_VIEWANGLE = "KEY_VIEWANGLE";
    private static final String KEY_SPEED = "KEY_SPEED";
    private static final String KEY_MAXFLEEDIST = "KEY_MAXFLEEDIST";
    private static final String KEY_MAXHEALTH = "KEY_MAXHEALTH";
    private static final String KEY_AUDIORADIUS = "KEY_AUDIORADIUS";
    private static final String KEY_STARVATIONRATE = "KEY_STARVATIONRATE";
    private static final String KEY_FOODPRIORITY = "KEY_FOODPRIORITY";
    private static final String KEY_FOODWEIGHT = "KEY_FOODWEIGHT";

    private static final String FIRING_RATE = "FIRING_RATE";
    private static final String FIRING_RANGE = "FIRING_RANGE";

    private static SpeciesDescriptor<Carnivore> mSpeciesDescriptor;

    public static synchronized SpeciesDescriptor<Carnivore> getSpeciesDescriptor(){
        if (mSpeciesDescriptor != null){
            return mSpeciesDescriptor;
        }

        Set<SpeciesParam> params = new HashSet<>();
        params.add(new SpeciesParam(KEY_X, 0.0, 1900.0, true));
        params.add(new SpeciesParam(KEY_Y, 0.0, 900.0, true));
        params.add(new SpeciesParam(KEY_WIDTH, 20.0, 60.0, false));
        params.add(new SpeciesParam(KEY_HEIGHT, 20.0, 110.0, false));
        params.add(new SpeciesParam(KEY_ANGLEPERSEC, 1.0, 500.0, false));
        params.add(new SpeciesParam(KEY_VIEWDISTANCE, 50.0, 500.0, false));
        params.add(new SpeciesParam(KEY_VIEWANGLE, 20.0, 170.0, false));
        params.add(new SpeciesParam(KEY_SPEED, 10.0, 600.0, false));
        params.add(new SpeciesParam(KEY_MAXFLEEDIST, 10.0, 1000.0, false));
        params.add(new SpeciesParam(KEY_MAXHEALTH, 10.0, 200.0, false));
        params.add(new SpeciesParam(KEY_AUDIORADIUS, 10.0, 500.0, false));
        params.add(new SpeciesParam(KEY_STARVATIONRATE, 20.0, 50.0, false));
        params.add(new SpeciesParam(KEY_FOODPRIORITY, 0.0, 1.0, false));
        params.add(new SpeciesParam(KEY_FOODWEIGHT, 0.0, 1.0, false));

        mSpeciesDescriptor = new SpeciesDescriptor<>(new Carnivore.CarnivoreEntityBuilder(), params);

        return mSpeciesDescriptor;
    }

    private double mNutrient;

    private Integer mFiringRate;
    private Integer mFiringRange;

    public Hunter(Actor.ActorBuilder builder) {
        super(builder);
        mNutrient = Math.random() * 100 + 20;

        double viewX = Math.random() * 1900;
        double viewY = Math.random() * 900;

        double targetX = (viewX - mCenter.getX().doubleValue());
        double targetY = (viewY - mCenter.getY().doubleValue());

        double dist = Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2));
        double ratio = dist / mViewDistance;

        double testX = targetX / ratio;
        double testY = targetY / ratio;

        mViewFocus = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                mCenter.getX().doubleValue() + testX, mCenter.getY().doubleValue() + testY);

        Point p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), mViewAngle / 2);

        mViewClock = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());

        p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), 360 - mViewAngle / 2 );

        mViewCounter= new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());
    }

    public Hunter(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

        mAnglePerSec = Math.random() * 500 + 1;
        mSpeed = Math.random() * 500 + 50;
        mSpeed = 10;

        mCurrangle = 0.0;
        mViewDistance = Math.random() * 450 + 50;
        mViewAngle = Math.random() * 160 + 10;

        mMaxHealth = Math.random() * 120 + 100;
        mCurrHealth = mMaxHealth;
        mStarvationRate = Math.random() * 10 + 5;
        mStarvationRate = 1;

        mAudioRadius = Math.random() * 200 + 10;

        double viewX = Math.random() * 1900;
        double viewY = Math.random() * 900;

        double targetX = (viewX - mCenter.getX().doubleValue());
        double targetY = (viewY - mCenter.getY().doubleValue());

        double dist = Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2));
        double ratio = dist / mViewDistance;

        double testX = targetX / ratio;
        double testY = targetY / ratio;

        mViewFocus = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                mCenter.getX().doubleValue() + testX, mCenter.getY().doubleValue() + testY);

        Point p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), mViewAngle / 2);

        mViewClock = new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());

        p = rotatePoint(new Point(mViewFocus.getX2(), mViewFocus.getY2()), new Point(mViewFocus.getX1(), mViewFocus.getY1()), 360 - mViewAngle / 2 );

        mViewCounter= new Line2D.Double(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(),
                p.getX().doubleValue(), p.getY().doubleValue());
    }

    public boolean isDead() {
        return mDead;
    }

    @Override
    public void tick(long frameTime, Grid grid) {
        mLifeTime++;

        eat();

        if(starve(frameTime)){
            return;
        }
    }

    @Override
    protected void initFoodClazzez() {

    }

    @Override
    protected void initPredatorClazzez() {

    }

    @Override
    public Specimen toSpecimen() {
        return null;
    }

    @Override
    public void draw(GraphicsContext g) {

    }

    @Override
    public List<Point> getPoints() {
        return null;
    }
}