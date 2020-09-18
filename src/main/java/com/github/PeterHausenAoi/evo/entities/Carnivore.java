package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.java.com.github.PeterHausenAoi.evo.evolution.EntityBuilder;
import main.java.com.github.PeterHausenAoi.evo.evolution.SpeciesDescriptor;
import main.java.com.github.PeterHausenAoi.evo.evolution.SpeciesParam;
import main.java.com.github.PeterHausenAoi.evo.evolution.Specimen;
import main.java.com.github.PeterHausenAoi.evo.flow.EvoManager;
import main.java.com.github.PeterHausenAoi.evo.flow.Grid;
import main.java.com.github.PeterHausenAoi.evo.flow.GridCell;
import main.java.com.github.PeterHausenAoi.evo.graphics.ImageFactory;
import main.java.com.github.PeterHausenAoi.evo.graphics.Resizer;
import main.java.com.github.PeterHausenAoi.evo.graphics.SpriteSheet;

import java.awt.geom.Line2D;
import java.util.*;

public class Carnivore extends Actor implements Edible{
    private static final String TAG = Carnivore.class.getSimpleName();

    public static final Color BOX_COLOR = Color.LIGHTBLUE;

    public static final List<Class<? extends BaseEntity>> FOOD_CLAZZEZ = Arrays.asList(Herbivore.class, Hunter.class);
    private static final String IMG_CODE = "stella_walk_1.png";
    private static final int[] DIRECTION_MAP = {4, 6, 2, 0, 3, 5, 7, 1};

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

    private static SpeciesDescriptor<Carnivore> mSpeciesDescriptor;

    public static synchronized SpeciesDescriptor<Carnivore> getSpeciesDescriptor(){
        if (mSpeciesDescriptor != null){
            return mSpeciesDescriptor;
        }

        Set<SpeciesParam> params = new HashSet<>();
        params.add(new SpeciesParam(KEY_X, 0.0, 1900.0, true));
        params.add(new SpeciesParam(KEY_Y, 0.0, 900.0, true));
        params.add(new SpeciesParam(KEY_WIDTH, 20.0, 100.0, false));
        params.add(new SpeciesParam(KEY_HEIGHT, 20.0, 200.0, false));
        params.add(new SpeciesParam(KEY_ANGLEPERSEC, 1.0, 600.0, false));
        params.add(new SpeciesParam(KEY_VIEWDISTANCE, 50.0, 400.0, false));
        params.add(new SpeciesParam(KEY_VIEWANGLE, 20.0, 170.0, false));
        params.add(new SpeciesParam(KEY_SPEED, 10.0, 700.0, false));
        params.add(new SpeciesParam(KEY_MAXFLEEDIST, 10.0, 1000.0, false));
        params.add(new SpeciesParam(KEY_MAXHEALTH, 10.0, 200.0, false));
        params.add(new SpeciesParam(KEY_AUDIORADIUS, 10.0, 300.0, false));
        params.add(new SpeciesParam(KEY_STARVATIONRATE, 20.0, 40.0, false));
        params.add(new SpeciesParam(KEY_FOODPRIORITY, 0.0, 1.0, false));
        params.add(new SpeciesParam(KEY_FOODWEIGHT, 0.0, 1.0, false));

        mSpeciesDescriptor = new SpeciesDescriptor<>(new CarnivoreEntityBuilder(), params, Carnivore.class);

        return mSpeciesDescriptor;
    }

    private double mNutrient;
    private SpriteSheet mSheet;

    public Carnivore(ActorBuilder builder) {
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

        loadSpriteSheet();
    }

    public Carnivore(int x, int y, int width, int height) {
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

        loadSpriteSheet();
    }

    protected void loadSpriteSheet(){
        Image img = ImageFactory.getImage(IMG_CODE, new Resizer(mWidth * 8, mHeight * 8, null));
        mSheet = new SpriteSheet(img, 4, 1, DIRECTION_MAP);
    }

    public boolean isDead() {
        return mDead;
    }

    @Override
    public void tick(long frameTime, Grid grid) {
        updatePosition(grid);
        mLifeTime++;

        eat(grid);

        if(starve(frameTime)){
            return;
        }

        BaseEntity newTarget = getTarget(grid);
        boolean targetAcquired = false;

        if (newTarget != mTargetEntity){
            if (newTarget == null){
                mTarget = null;
                mTargetEntity = null;
            }else{
                targetAcquired = true;
                mTarget = new Point(newTarget.getCenter().getX(), newTarget.getCenter().getY());
                mTargetEntity = newTarget;
            }
        }else if (newTarget != null){
            double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(), mViewFocus.getP1(),
                    new java.awt.Point(mTargetEntity.getCenter().getX().intValue(),
                            mTargetEntity.getCenter().getY().intValue()));
            double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

            double targetAngle;

            if (Math.abs(angle1) < Math.abs(angle2)){
                targetAngle = Math.abs(round(angle1, 0));
            }else{
                targetAngle = Math.abs(round(angle2, 0));
            }

            if (Math.abs(targetAngle - round(mCurrangle,0)) > 1){
                mPositionState = ROTATION_STATE;
                targetAcquired = true;
                mTarget = new Point(newTarget.getCenter().getX(), newTarget.getCenter().getY());
            }else{
                mPositionState = MOVE_STATE;
                mTarget = new Point(newTarget.getCenter().getX(), newTarget.getCenter().getY());
                targetAcquired = false;
            }
        }

        if(mTarget == null || isTargetReached() || targetAcquired){
            if (newTarget == null || isTargetReached()){
                mTarget = new Point(Math.random() * grid.getWidth().doubleValue(), Math.random() * grid.getHeight().doubleValue());
                mTargetEntity = null;
            }

            mPositionState = ROTATION_STATE;
            mCurrangle = 0.0;

            double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(),
                    mViewFocus.getP1(), new java.awt.Point(mTarget.getX().intValue(),mTarget.getY().intValue()));
            double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

            if (Math.abs(angle1) < Math.abs(angle2)){
                mTargetAngle = round(angle1, 4);
            }else{
                mTargetAngle = round(angle2, 4);
            }
        }

        if(MOVE_STATE.equals(mPositionState)){
            move(frameTime);
        }else{
            rotate(frameTime);
        }

        updatePosition(grid);
    }

    @Override
    protected void initFoodClazzez() {
        mFoodClazzez = Collections.unmodifiableSet(new HashSet<>(FOOD_CLAZZEZ));
    }

    @Override
    protected void initPredatorClazzez() {
        Set<Class<? extends BaseEntity>> clazzes = new HashSet<>();
        clazzes.add(Hunter.class);
        mPredatorClazzez = Collections.unmodifiableSet(clazzes);
    }

    @Override
    public Specimen toSpecimen() {
        Map<String, Double> props = new HashMap<>();

        props.put(KEY_WIDTH, (double)mWidth);
        props.put(KEY_HEIGHT, (double)mHeight);
        props.put(KEY_ANGLEPERSEC, mAnglePerSec);
        props.put(KEY_VIEWDISTANCE, mViewDistance);
        props.put(KEY_VIEWANGLE, mViewAngle);
        props.put(KEY_SPEED, mSpeed);
        props.put(KEY_MAXFLEEDIST, mMaxFleeDist);
        props.put(KEY_MAXHEALTH, mMaxHealth);
        props.put(KEY_AUDIORADIUS, mAudioRadius);
        props.put(KEY_STARVATIONRATE, mStarvationRate);
        props.put(KEY_FOODPRIORITY, mFoodPriority);
        props.put(KEY_FOODWEIGHT, mFoodWeight);

        return new Specimen(mGen, mLifeTime.doubleValue(), props);
    }

    @Override
    public void draw(GraphicsContext g) {
        double hpBar = 50.0;
        g.setFill(Color.DARKRED);
        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 20, hpBar, 10);

        g.setFill(Color.DARKGREEN);
        g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 20, hpBar * mCurrHealth / mMaxHealth, 10);

        g.setFill(Color.YELLOW);
        g.fillText(String.valueOf(mGen), mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 10);

        if (EvoManager.DEBUG_DISPLAY) {
            g.setFill(BOX_COLOR);

            double width = mTopRight.getX().doubleValue() - mTopLeft.getX().doubleValue();
            double height = mBotLeft.getY().doubleValue() - mTopLeft.getY().doubleValue();
            g.fillRect(mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue(), width, height);

            g.setStroke(Color.AQUA);
            g.setLineWidth(2);
            g.strokeLine(mViewFocus.getX1(), mViewFocus.getY1(), mViewFocus.getX2(), mViewFocus.getY2());

            g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
            g.setLineWidth(2);
            g.strokeLine(mViewClock.getX1(), mViewClock.getY1(), mViewClock.getX2(), mViewClock.getY2());

            g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
            g.setLineWidth(2);
            g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewClock.getX2(), mViewClock.getY2());

            g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
            g.setLineWidth(2);
            g.strokeLine(mViewCounter.getX1(), mViewCounter.getY1(), mViewCounter.getX2(), mViewCounter.getY2());

            g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
            g.setLineWidth(2);
            g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewCounter.getX2(), mViewCounter.getY2());

            g.setStroke(mTargetEntity == null ? Color.GREY : Color.RED);
            g.setLineWidth(2);
            g.strokeOval(mCenter.getX().doubleValue() - mAudioRadius, mCenter.getY().doubleValue() - mAudioRadius, mAudioRadius * 2, mAudioRadius * 2);

            g.setStroke(Color.PINK);
            g.setLineWidth(2);
            g.strokeLine(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(), mTarget.getX().doubleValue(), mTarget.getY().doubleValue());
        }
        double targetAngle = getViewAngle();
        g.drawImage(mSheet.getImage(targetAngle), mTopLeft.getX().doubleValue() - mWidth / 2, mTopLeft.getY().doubleValue());
    }

    @Override
    public double getNutrient() {
        return mNutrient;
    }

    @Override
    public void digest() {
        mCurrHealth = 0;
        mDead = true;

        for (GridCell cell : mContainers){
            cell.removeEntity(this);
        }

        mContainers.clear();
    }

    public static class CarnivoreBuilder extends Actor.ActorBuilder {
        public CarnivoreBuilder() {

        }
    }

    public static class CarnivoreEntityBuilder implements EntityBuilder<Carnivore> {

        @Override
        public Carnivore buildEntity(Specimen specimen) {
            CarnivoreBuilder builder = new CarnivoreBuilder();

            builder.setAnglePerSec(specimen.getProps().get(KEY_ANGLEPERSEC))
                    .setX(specimen.getProps().get(KEY_X).intValue())
                    .setY(specimen.getProps().get(KEY_Y).intValue())
                    .setHeight(specimen.getProps().get(KEY_HEIGHT).intValue())
                    .setWidth(specimen.getProps().get(KEY_WIDTH).intValue())
                    .setAudioRadius(specimen.getProps().get(KEY_AUDIORADIUS))
                    .setFoodPriority(specimen.getProps().get(KEY_FOODPRIORITY))
                    .setFoodWeight(specimen.getProps().get(KEY_FOODWEIGHT))
                    .setMaxFleeDist(specimen.getProps().get(KEY_MAXFLEEDIST))
                    .setStarvationRate(specimen.getProps().get(KEY_STARVATIONRATE))
                    .setViewAngle(specimen.getProps().get(KEY_VIEWANGLE))
                    .setViewDistance(specimen.getProps().get(KEY_VIEWDISTANCE))
                    .setSpeed(specimen.getProps().get(KEY_SPEED))
                    .setMaxHealth(specimen.getProps().get(KEY_MAXHEALTH))
                    .setGen(specimen.getGen());

            return new Carnivore(builder);
        }
    }
}