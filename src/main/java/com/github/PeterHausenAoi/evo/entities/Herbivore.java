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
import main.java.com.github.PeterHausenAoi.evo.flow.Tickable;
import main.java.com.github.PeterHausenAoi.evo.graphics.ImageFactory;
import main.java.com.github.PeterHausenAoi.evo.graphics.Resizer;
import main.java.com.github.PeterHausenAoi.evo.graphics.SpriteSheet;

import java.awt.geom.Line2D;
import java.util.*;

public class Herbivore extends Actor implements Tickable, Edible {
    private static final String TAG = Herbivore.class.getSimpleName();

    private static final Color BOX_COLOR = Color.PURPLE;

    public static final List<Class<? extends BaseEntity>> FOOD_CLAZZEZ = Arrays.asList(Food.class);
    private static final String IMG_CODE = "critter.png";
    private static final int[] DIRECTION_MAP = {4,3,2,1,0,7,6,5};

    private static final String KEY_X = "KEY_X";
    private static final String KEY_Y = "KEY_Y";
    private static final String KEY_WIDTH = "KEY_WIDTH";
    private static final String KEY_HEIGHT = "KEY_HEIGHT";
    private static final String KEY_ANGLEPERSEC = "KEY_ANGLEPERSEC";
    private static final String KEY_VIEWDISTANCE = "KEY_VIEWDISTANCE";
    private static final String KEY_VIEWANGLE = "KEY_VIEWANGLE";
    private static final String KEY_SPEED = "KEY_SPEED";
    private static final String KEY_FLEEDIST = "KEY_FLEEDIST";
    private static final String KEY_MAXFLEEDIST = "KEY_MAXFLEEDIST";
    private static final String KEY_MAXHEALTH = "KEY_MAXHEALTH";
    private static final String KEY_AUDIORADIUS = "KEY_AUDIORADIUS";
    private static final String KEY_STARVATIONRATE = "KEY_STARVATIONRATE";
    private static final String KEY_FOODPRIORITY = "KEY_FOODPRIORITY";
    private static final String KEY_FOODWEIGHT = "KEY_FOODWEIGHT";

    private static SpeciesDescriptor<Herbivore> mSpeciesDescriptor;

    public static synchronized SpeciesDescriptor<Herbivore> getSpeciesDescriptor(){
        if (mSpeciesDescriptor != null){
            return mSpeciesDescriptor;
        }

        Set<SpeciesParam> params = new HashSet<>();
        params.add(new SpeciesParam(KEY_X, 0.0, 1900.0, true));
        params.add(new SpeciesParam(KEY_Y, 0.0, 900.0, true));
        params.add(new SpeciesParam(KEY_WIDTH, 10.0, 80.0, false));
        params.add(new SpeciesParam(KEY_HEIGHT, 10.0, 80.0, false));
        params.add(new SpeciesParam(KEY_ANGLEPERSEC, 1.0, 500.0, false));
        params.add(new SpeciesParam(KEY_VIEWDISTANCE, 50.0, 500.0, false));
        params.add(new SpeciesParam(KEY_VIEWANGLE, 20.0, 170.0, false));
        params.add(new SpeciesParam(KEY_SPEED, 50.0, 700.0, false));
        params.add(new SpeciesParam(KEY_MAXFLEEDIST, 10.0, 1000.0, false));
        params.add(new SpeciesParam(KEY_MAXHEALTH, 10.0, 150.0, false));
        params.add(new SpeciesParam(KEY_AUDIORADIUS, 10.0, 400.0, false));
        params.add(new SpeciesParam(KEY_STARVATIONRATE, 5.0, 50.0, false));
        params.add(new SpeciesParam(KEY_FOODPRIORITY, 0.0, 1.0, false));
        params.add(new SpeciesParam(KEY_FOODWEIGHT, 0.0, 1.0, false));

        mSpeciesDescriptor = new SpeciesDescriptor<>(new HerbivoreEntityBuilder(), params, Herbivore.class);

        return mSpeciesDescriptor;
    }

    private double mNutrient;

    private Set<BaseEntity> mConePredators = new HashSet<>();

    private Set<BaseEntity> mPrevRadiusPredatorBatch = new HashSet<>();
    private Set<BaseEntity> mRadiusPredators = new HashSet<>();

    private SpriteSheet mSheet;

    public Herbivore(ActorBuilder builder) {
        super(builder);
        mNutrient = Math.random() * 20 + 20;

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

        mDead = false;

        loadSpriteSheet();
    }

    public Herbivore(int x, int y, int width, int height) {
        super(x, y, width, height);

        mAnglePerSec = Math.random() * 500 + 1;
//        mAnglePerSec = 100;

        mSpeed = Math.random() * 500 + 50;
//        mSpeed = 100;

        mCurrangle = 0.0;
        mViewDistance = Math.random() * 450 + 50;
//        mViewDistance = 200;
        mViewAngle = Math.random() * 160 + 10;

        mMaxHealth = Math.random() * 120 + 10;
//        mMaxHealth = 200;
        mCurrHealth = mMaxHealth;

        mFoodPriority = Math.random();
        mFoodWeight = Math.random();

        mStarvationRate = Math.random() * 10 + 5;
//        mStarvationRate = 1;
        mNutrient = Math.random() * 10 + 10;


        mAudioRadius = Math.random() * 200 + 10;
//        mAudioRadius = 150;
        mMaxFleeDist = Math.random() * 900 + 100;
//        mMaxFleeDist = 1000;

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
        mSheet = new SpriteSheet(img, 8, 3, DIRECTION_MAP);
    }

    protected Point getInvertedVector(BaseEntity newPredatorEntity){
        double predatorX = newPredatorEntity.getCenter().getX().doubleValue() - mCenter.getX().doubleValue();
        double predatorY = newPredatorEntity.getCenter().getY().doubleValue() - mCenter.getY().doubleValue();

        double fleeX = predatorX * -1;
        double fleeY = predatorY * -1;

        double dist = Math.sqrt(Math.pow(fleeX, 2) + Math.pow(fleeY, 2));
        double ratio = mMaxFleeDist / dist;

        double vectX = fleeX * ratio;
        double vectY = fleeY * ratio;

        return new Point(vectX, vectY);
    }

    @Override
    public void tick(long frameTime, Grid grid) {
        updatePosition(grid);
        mLifeTime++;

        eat(grid);

        if(starve(frameTime)){
            return;
        }

        BaseEntity newTargetEntity = getTarget(grid);

        Set<BaseEntity> newConePredatorEntities = getConePredators(grid);
        mRadiusPredators = getRadiusPredators(grid);

        if ((newConePredatorEntities.size() > 0 || mRadiusPredators.size() > 0) && !Objects.equals(mMode, FLEE_MODE)){
            mMode = FLEE_MODE;
        }

        if (isFoodPriority()){
//            Log.doLog(TAG, "FORCE FEED");
            mMode = EAT_MODE;
            mConePredators = new HashSet<>();
            mRadiusPredators = new HashSet<>();
            mPrevRadiusPredatorBatch = new HashSet<>();
            mFleeDist = 0;
        }

        if (mMode.equals(FLEE_MODE)){
            boolean targetAcquired = false;

            if (mCenter.getX().intValue() < 0 || mCenter.getX().intValue() > 1900
                    || mCenter.getY().intValue() < 0 || mCenter.getY().intValue() > 900){

                double x;
                double y;

                double xDiff;
                double yDiff;

                double vectX;
                double vectY;

                if (mCenter.getX().intValue() < 0 || mCenter.getX().intValue() > 1900){
                    x = mCenter.getX().doubleValue() < 0 ? 1 : 1900 - 1;
                    y = mCenter.getY().doubleValue();
                    vectX = 0;

                    xDiff = x - mCenter.getX().doubleValue();
                    yDiff = 0;

                    if(x == 1899 && y == 899){
                        vectY = (mMaxFleeDist - mFleeDist) * -1;
                    }else{
                        double sig = mVect == null ? 0 : Math.signum(mVect.getY().doubleValue());

                        if (sig == 0){
                            sig = mCenter.getY().doubleValue() < 1900 / 2 ? 1 : -1;
                        }

                        vectY = (mMaxFleeDist - mFleeDist) * sig;
                    }
                }else{
                    x = mCenter.getX().doubleValue();
                    y = mCenter.getY().doubleValue() < 0 ? 1 : 900 - 1;
                    vectY = 0;

                    xDiff = 0;
                    yDiff = y - mCenter.getY().doubleValue();

                    double sig = mVect == null ? 0 : Math.signum(mVect.getX().doubleValue());

                    if (sig == 0){
                        sig = mCenter.getX().doubleValue() < 900 / 2 ? 1 : -1;
                    }

                    vectX = (mMaxFleeDist - mFleeDist) * sig;
                }

                mTarget = new Point(x + vectX, y + vectY);
                mCenter = new Point(x, y);

                mViewFocus = new Line2D.Double(mViewFocus.getX1() + xDiff, mViewFocus.getY1() + yDiff,
                        mViewFocus.getX2() + xDiff, mViewFocus.getY2() + yDiff);

                mViewClock = new Line2D.Double(mViewClock.getX1() + xDiff, mViewClock.getY1() + yDiff,
                        mViewClock.getX2() + xDiff, mViewClock.getY2() + yDiff);

                mViewCounter= new Line2D.Double(mViewCounter.getX1() + xDiff, mViewCounter.getY1() + yDiff,
                        mViewCounter.getX2() + xDiff, mViewCounter.getY2() + yDiff);

                mTopLeft = new Point(mCenter.getX().intValue() - mWidth / 2,mCenter.getY().intValue() - mHeight / 2);
                mTopRight = new Point(mCenter.getX().intValue() + mWidth / 2, mCenter.getY().intValue() - mHeight / 2);
                mBotLeft = new Point(mCenter.getX().intValue() - mWidth / 2, mCenter.getY().intValue() + mHeight / 2);
                mBotRight = new Point(mCenter.getX().intValue() + mWidth / 2, mCenter.getY().intValue() + mHeight / 2);

                mPoints = new ArrayList<>();
                mPoints.add(mTopLeft);
                mPoints.add(mTopRight);
                mPoints.add(mBotLeft);
                mPoints.add(mBotRight);

                buildBorders();

                targetAcquired = true;

            }else if (newConePredatorEntities.size() > 0 || mRadiusPredators.size() > 0){
                int prevSize = mConePredators.size();
                mConePredators.addAll(newConePredatorEntities);

                Set<BaseEntity> sum = new HashSet<>();
                sum.addAll(mPrevRadiusPredatorBatch);
                sum.addAll(mRadiusPredators);

                boolean radiusDiff = sum.size() != mPrevRadiusPredatorBatch.size()
                        || sum.size() != mRadiusPredators.size()
                        || mPrevRadiusPredatorBatch.size() != mRadiusPredators.size();

                if (prevSize != mConePredators.size() || radiusDiff){
                    targetAcquired = true;

                    double x = 0;
                    double y = 0;

                    for (BaseEntity pred : mConePredators){
                        Point newVect = getInvertedVector(pred);
                        x += newVect.getX().doubleValue();
                        y += newVect.getY().doubleValue();
                    }

                    for (BaseEntity pred : mRadiusPredators){
                        Point newVect = getInvertedVector(pred);
                        x += newVect.getX().doubleValue();
                        y += newVect.getY().doubleValue();
                    }

                    double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                    double ratio = mMaxFleeDist / dist;

                    double vectX = x * ratio;
                    double vectY = y * ratio;

                    Point newVect = new Point(vectX, vectY);

                    mTarget = new Point(mCenter.getX().doubleValue() + newVect.getX().doubleValue(),
                            mCenter.getY().doubleValue() + newVect.getY().doubleValue());

                    mTargetEntity = null;

                    mPrevRadiusPredatorBatch = mRadiusPredators;
                }
            }

            if(mTarget == null || isTargetReached() || targetAcquired){
                mPositionState = ROTATION_STATE;
                mCurrangle = 0.0;

                double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(), mViewFocus.getP1(), new java.awt.Point(mTarget.getX().intValue(),mTarget.getY().intValue()));
                double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

                if (Math.abs(angle1) < Math.abs(angle2)){
                    mTargetAngle = angle1;
                }else{
                    mTargetAngle = angle2;
                }
            }

        }else{
            boolean targetAcquired = false;
            boolean outOfBounds = false;

            if (newTargetEntity != mTargetEntity){
                if (newTargetEntity == null){
                    mTarget = null;
                    mTargetEntity = null;
                }else{
                    targetAcquired = true;
                    mTarget = new Point(newTargetEntity.getCenter().getX(), newTargetEntity.getCenter().getY());
                    mTargetEntity = newTargetEntity;
                }
            }

            if (mTarget != null) {
                outOfBounds = mCenter.getX().intValue() < 0 || mCenter.getX().intValue() > 1900
                        || mCenter.getY().intValue() < 0 || mCenter.getY().intValue() > 900;

                if (outOfBounds){
                    int x = mCenter.getX().intValue();
                    int y = mCenter.getY().intValue();

                    if (x < 0){
                        x = 1;
                    }

                    if (x > 1900){
                        x = 1899;
                    }

                    if (y < 0){
                        y = 1;
                    }

                    if (y > 900){
                        y = 899;
                    }

                    mCenter = new Point(x, y);
                }
            }

            if(mTarget == null || isTargetReached() || targetAcquired || outOfBounds){
                if (newTargetEntity == null || isTargetReached()){
                    mTarget = new Point(Math.random() * grid.getWidth().doubleValue(), Math.random() * grid.getHeight().doubleValue());
                    mTargetEntity = null;
                }

                mPositionState = ROTATION_STATE;
                mCurrangle = 0.0;

                double angle1 = angleBetween2Lines(mViewFocus.getP1(), mViewFocus.getP2(), mViewFocus.getP1(), new java.awt.Point(mTarget.getX().intValue(),mTarget.getY().intValue()));
                double angle2 = (360 - Math.abs(angle1)) * (0 - Math.signum(angle1));

                if (Math.abs(angle1) < Math.abs(angle2)){
                    mTargetAngle = angle1;
                }else{
                    mTargetAngle = angle2;
                }
            }
        }

        if(MOVE_STATE.equals(mPositionState)){
            Point prevPos = mCenter;

            move(frameTime);

            if (mMode.equals(FLEE_MODE)){
                mFleeDist += calcDist(prevPos.getX().doubleValue(), prevPos.getY().doubleValue(),
                        mCenter.getX().doubleValue(), mCenter.getY().doubleValue());

                if (mFleeDist > mMaxFleeDist - 10){
                    mMode = EAT_MODE;
                    mConePredators = new HashSet<>();
                    mRadiusPredators = new HashSet<>();
                    mPrevRadiusPredatorBatch = new HashSet<>();
                    mFleeDist = 0;
                }
            }
        }else{
            rotate(frameTime);

            if (mPositionState.equals(MOVE_STATE)){
                mConePredators = new HashSet<>();
                mRadiusPredators = new HashSet<>();
            }
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

        clazzes.add(Carnivore.class);
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
    public Set<Class<? extends BaseEntity>> getFoodClazzez() {
        return mFoodClazzez;
    }

    @Override
    public Set<Class<? extends BaseEntity>> getPredatorClazzez() {
        return mPredatorClazzez;
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



//        g.fillText(String.valueOf(targetAngle),
//                mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue() - 10);

            g.setStroke(Color.AQUA);
            g.setLineWidth(2);
            g.strokeLine(mViewFocus.getX1(), mViewFocus.getY1(), mViewFocus.getX2(), mViewFocus.getY2());

            g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
            g.setLineWidth(2);
            g.strokeLine(mViewClock.getX1(), mViewClock.getY1(), mViewClock.getX2(), mViewClock.getY2());

            g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
            g.setLineWidth(2);
            g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewClock.getX2(), mViewClock.getY2());

            g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
            g.setLineWidth(2);
            g.strokeLine(mViewCounter.getX1(), mViewCounter.getY1(), mViewCounter.getX2(), mViewCounter.getY2());

            g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
            g.setLineWidth(2);
            g.strokeLine(mViewFocus.getX2(), mViewFocus.getY2(), mViewCounter.getX2(), mViewCounter.getY2());

            g.setStroke(mMode.equals(FLEE_MODE) ? Color.BLUE : (mTargetEntity == null ? Color.GREY : Color.RED));
            g.setLineWidth(2);
            g.strokeOval(mCenter.getX().doubleValue() - mAudioRadius, mCenter.getY().doubleValue() - mAudioRadius, mAudioRadius * 2, mAudioRadius * 2);

            g.setStroke(Color.PINK);
            g.setLineWidth(2);
            g.strokeLine(mCenter.getX().doubleValue(), mCenter.getY().doubleValue(), mTarget.getX().doubleValue(), mTarget.getY().doubleValue());
        }

        double targetAngle = getViewAngle();
        g.drawImage(mSheet.getImage(targetAngle), mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue());
    }

    @Override
    public List<Point> getPoints() {
        return mPoints;
    }

    public boolean isDead() {
        return mDead;
    }

    @Override
    public double getNutrient() {
        return mNutrient;
    }

    @Override
    public void digest() {
//        Log.doLog(TAG, "EATEN");
        mCurrHealth = 0;
        mDead = true;

        for (GridCell cell : mContainers){
            cell.removeEntity(this);
        }

        mContainers.clear();
    }

    public static class HerbivoreBuilder extends Actor.ActorBuilder{
        public HerbivoreBuilder() {

        }
    }

    public static class HerbivoreEntityBuilder implements EntityBuilder<Herbivore> {

        @Override
        public Herbivore buildEntity(Specimen specimen) {
            HerbivoreBuilder builder = new HerbivoreBuilder();

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

            return new Herbivore(builder);
        }
    }
}