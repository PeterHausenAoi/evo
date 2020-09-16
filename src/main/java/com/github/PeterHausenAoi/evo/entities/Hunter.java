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
import java.util.stream.Collectors;

public class Hunter extends Actor implements Edible{
    private static final String TAG = Hunter.class.getSimpleName();

    private static final Color BOX_COLOR = Color.YELLOW;
    private static final int[] DIRECTION_MAP = {5, 4, 3, 6, 2, 7, 0 ,1};

    private static final String IMG_CODE = "hunter.png";

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

    private static final String KEY_FIRING_RATE = "FIRING_RATE";
    private static final String KEY_FIRING_RANGE = "FIRING_RANGE";
    private static final String KEY_BULLET_SIZE = "KEY_BULLET_SIZE";
    private static final String KEY_BULLET_SPEED = "KEY_BULLET_SPEED";

    private static SpeciesDescriptor<Hunter> mSpeciesDescriptor;

    public static synchronized SpeciesDescriptor<Hunter> getSpeciesDescriptor(){
        if (mSpeciesDescriptor != null){
            return mSpeciesDescriptor;
        }

        Set<SpeciesParam> params = new HashSet<>();
        params.add(new SpeciesParam(KEY_X, 0.0, 1900.0, true));
        params.add(new SpeciesParam(KEY_Y, 0.0, 900.0, true));
        params.add(new SpeciesParam(KEY_WIDTH, 20.0, 60.0, false));
        params.add(new SpeciesParam(KEY_HEIGHT, 20.0, 110.0, false));
        params.add(new SpeciesParam(KEY_ANGLEPERSEC, 1.0, 400.0, false));
        params.add(new SpeciesParam(KEY_VIEWDISTANCE, 50.0, 400.0, false));
        params.add(new SpeciesParam(KEY_VIEWANGLE, 20.0, 170.0, false));
        params.add(new SpeciesParam(KEY_SPEED, 10.0, 400.0, false));
        params.add(new SpeciesParam(KEY_MAXFLEEDIST, 10.0, 1000.0, false));
        params.add(new SpeciesParam(KEY_MAXHEALTH, 10.0, 200.0, false));
        params.add(new SpeciesParam(KEY_AUDIORADIUS, 10.0, 250.0, false));
        params.add(new SpeciesParam(KEY_STARVATIONRATE, 20.0, 40.0, false));
        params.add(new SpeciesParam(KEY_FOODPRIORITY, 0.0, 1.0, false));
        params.add(new SpeciesParam(KEY_FOODWEIGHT, 0.0, 1.0, false));

        params.add(new SpeciesParam(KEY_FIRING_RATE, 1.0, 20.0, false));
        params.add(new SpeciesParam(KEY_FIRING_RANGE, 100.0, 450.0, false));
        params.add(new SpeciesParam(KEY_BULLET_SIZE, 10.0, 30.0, false));
        params.add(new SpeciesParam(KEY_BULLET_SPEED, 200.0, 1000.0, false));

        mSpeciesDescriptor = new SpeciesDescriptor<>(new HunterEntityBuilder(), params);

        return mSpeciesDescriptor;
    }

    private double mNutrient;

    private double mFiringRate;
    private double mBulletTime;
    private double mFiringRange;
    private double mBulletSpeed;
    private double mBulletsize;

    private long mCoolDownTime;

    private List<Bullet> mBullets;

    private SpriteSheet mSheet;

    public Hunter(Actor.ActorBuilder builder) {
        super(builder);
        mNutrient = Math.random() * 100 + 20;

        if (builder instanceof HunterBuilder){
            HunterBuilder hbuilder = (HunterBuilder)builder;
            mBulletsize = hbuilder.getBulletsize();
            mBulletSpeed = hbuilder.getBulletSpeed();
            mFiringRange = hbuilder.getFiringRange();
            mFiringRate = hbuilder.getFiringRate();

            mBulletTime = 1000 / mFiringRate;
        }

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

        mBullets = new ArrayList<>();
        loadSpriteSheet();
    }

    public Hunter(int x, int y, int width, int height) {
        super(x, y, width, height);

        mWidth = width;
        mHeight = height;

        mAnglePerSec = Math.random() * 500 + 1;
        mSpeed = Math.random() * 500 + 50;
        mSpeed = 100;

        mCurrangle = 0.0;
        mViewDistance = Math.random() * 450 + 50;
        mViewDistance = 450;
        mViewAngle = Math.random() * 160 + 10;

        mMaxHealth = Math.random() * 120 + 100;
        mCurrHealth = mMaxHealth;
        mStarvationRate = Math.random() * 10 + 5;
        mStarvationRate = 1;

        mAudioRadius = Math.random() * 200 + 10;
        mAudioRadius = 200 + 10;

        mFiringRate = 10.0;
        mFiringRange = 1000.0;
        mBulletsize = 20;
        mBulletSpeed = 1000;

        mBulletTime = 1000 / mFiringRate;

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

        mBullets = new ArrayList<>();

        loadSpriteSheet();
    }

    protected void loadSpriteSheet(){
        Image img = ImageFactory.getImage(IMG_CODE, new Resizer(mWidth * 8, mHeight * 8, null));
        mSheet = new SpriteSheet(img, 8, 1, DIRECTION_MAP);
    }

    public boolean isDead() {
        return mDead;
    }

    @Override
    protected void eat(Grid grid){
        Set<Bullet> delBullets = new HashSet<>();

        for (Bullet bullet : mBullets){
            for (GridCell cell : bullet.getContainers()){
                List<BaseEntity> foods = cell.getEntities().stream().filter(baseEntity -> !baseEntity.equals(this)
                        && isValidFood(baseEntity.getClass())
                        && baseEntity instanceof Edible
                        && (baseEntity.isColliding(bullet) || bullet.isColliding(baseEntity)))
                        .collect(Collectors.toList());

                for (BaseEntity f : foods){
                    if(f instanceof Actor){
                        Actor actor = (Actor)f;
                        if (actor.isDead()){
                            continue;
                        }
                    }

                    updatePosition(grid, f);

                    Edible food = (Edible)f;
                    food.digest();
                    f.clearContainers();

                    this.mCurrHealth += food.getNutrient();

                    if(this.mCurrHealth > mMaxHealth){
                        mCurrHealth = mMaxHealth;
                    }

                    if (mTargetEntity == food){
                        mTargetEntity = null;
                        mTarget = null;
                    }

                    delBullets.add(bullet);
                }
            }
        }

        delBullets.forEach(BaseEntity::clearContainers);
        mBullets.removeAll(delBullets);
    }

    @Override
    public void updatePosition(Grid grid){
        super.updatePosition(grid);

        mBullets.forEach(bullet -> bullet.updatePosition(grid));
    }

    @Override
    public boolean starve(long frameTime){
        mCurrHealth -= mStarvationRate / 1000 * frameTime;

        if(mCurrHealth <= 0){
//            Log.doLog(TAG, "STARVED");
            mDead = true;

            mBullets.forEach(BaseEntity::clearContainers);
            mBullets.clear();

            for (GridCell cell : mContainers){
                cell.removeEntity(this);
            }

            mContainers.clear();
            return true;
        }

        return false;
    }

    @Override
    public void tick(long frameTime, Grid grid) {
        updatePosition(grid);
        mLifeTime++;

        Set<Bullet> mDelBullets = new HashSet<>();

        for (Bullet bullet : mBullets){
            if (bullet.getCenter().getX().intValue() < 0 || bullet.getCenter().getX().intValue() > 1900
                    || bullet.getCenter().getY().intValue() < 0 || bullet.getCenter().getY().intValue() > 900){
                mDelBullets.add(bullet);
            }
        }

        mDelBullets.forEach(Bullet::clearContainers);
        mBullets.removeAll(mDelBullets);

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
            double dist = 0;

            if (mTargetEntity != null){
                dist = calcDist(mCenter.getX().doubleValue(),
                        mCenter.getY().doubleValue(),
                        mTargetEntity.getCenter().getX().doubleValue(),
                        mTargetEntity.getCenter().getY().doubleValue());
            }

            if ((mCoolDownTime >= mBulletTime) && mTargetEntity != null && dist < mFiringRange){
                Bullet bullet = new Bullet(mCenter.getX().intValue(),
                        mCenter.getY().intValue(),
                        (int)mBulletsize,
                        (int)mBulletsize,
                        new Point(mTargetEntity.getCenter().getX().doubleValue(), mTargetEntity.getCenter().getY().doubleValue()),
                        mBulletSpeed);

                mCoolDownTime = 0;
                mBullets.add(bullet);
            }else{
                mCoolDownTime += frameTime;
                move(frameTime);
            }
        }else{
            rotate(frameTime);
        }

        mBullets.forEach(bullet -> {
            bullet.move(frameTime);
            grid.placeEntity(bullet);
            bullet.updateAbandonedCells();
        });

        updatePosition(grid);
    }

    @Override
    protected void initFoodClazzez() {
        Set<Class<? extends BaseEntity>> clazzes = new HashSet<>();

        clazzes.add(Herbivore.class);
        clazzes.add(Carnivore.class);
        // TODO cannibalism rocks
//        clazzes.add(Hunter.class);

        mFoodClazzez = Collections.unmodifiableSet(clazzes);
    }

    @Override
    protected void initPredatorClazzez() {
        Set<Class<? extends BaseEntity>> clazzes = new HashSet<>();
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
        props.put(KEY_FIRING_RATE, mFiringRate);
        props.put(KEY_FIRING_RANGE, mFiringRange);
        props.put(KEY_BULLET_SIZE, mBulletsize);
        props.put(KEY_BULLET_SPEED, mBulletSpeed);

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

        mBullets.forEach(bullet -> bullet.draw(g));

        double targetAngle = getViewAngle();
        g.drawImage(mSheet.getImage(targetAngle), mTopLeft.getX().doubleValue(), mTopLeft.getY().doubleValue());
    }

    @Override
    public double getNutrient() {
        return mNutrient;
    }

    @Override
    public void digest() {
        mCurrHealth = 0;
        mDead = true;


        mBullets.forEach(BaseEntity::clearContainers);

        for (GridCell cell : mContainers){
            cell.removeEntity(this);
        }

        mContainers.clear();
    }

    public static class HunterBuilder extends Actor.ActorBuilder {
        private double firingRate;
        private double firingRange;
        private double bulletSpeed;
        private double bulletsize;

        public HunterBuilder() {

        }

        public double getBulletSpeed() {
            return bulletSpeed;
        }

        public HunterBuilder setBulletSpeed(double bulletSpeed) {
            this.bulletSpeed = bulletSpeed;
            return this;
        }

        public double getBulletsize() {
            return bulletsize;
        }

        public HunterBuilder setBulletsize(double bulletsize) {
            this.bulletsize = bulletsize;
            return this;
        }

        public double getFiringRate() {
            return firingRate;
        }

        public HunterBuilder setFiringRate(double firingRate) {
            this.firingRate = firingRate;
            return this;
        }

        public double getFiringRange() {
            return firingRange;
        }

        public HunterBuilder setFiringRange(double firingRange) {
            this.firingRange = firingRange;
            return this;
        }
    }

    public static class HunterEntityBuilder implements EntityBuilder<Hunter> {

        @Override
        public Hunter buildEntity(Specimen specimen) {
            Hunter.HunterBuilder builder = new Hunter.HunterBuilder();

            builder.setBulletsize(specimen.getProps().get(KEY_BULLET_SIZE))
                    .setBulletSpeed(specimen.getProps().get(KEY_BULLET_SPEED))
                    .setFiringRange(specimen.getProps().get(KEY_FIRING_RANGE))
                    .setFiringRate(specimen.getProps().get(KEY_FIRING_RATE))
                    .setAnglePerSec(specimen.getProps().get(KEY_ANGLEPERSEC))
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

            return new Hunter(builder);
        }
    }
}