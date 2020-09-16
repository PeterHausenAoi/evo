package main.java.com.github.PeterHausenAoi.evo.graphics;

import javafx.scene.image.Image;

public class Sprite {
    private static final String TAG = Sprite.class.getSimpleName();

    private Image[] mStates;

    private int mMaxTickTime;
    private int mCurrTickTime;
    private int mCurrInd;

    public Sprite(Image[] mStates, int maxTickTime) {
        this.mStates = mStates;
        this.mMaxTickTime = maxTickTime;
        this.mCurrTickTime = 0;
        this.mCurrInd = 0;
    }

    public Image getNext(){
        if (mMaxTickTime == mCurrTickTime){
            mCurrTickTime = 0;
            mCurrInd++;

            if (mCurrInd >= mStates.length){
                mCurrInd = 0;
            }
        }else{
            mCurrTickTime++;
        }

        return mStates[mCurrInd];
    }
}