package com.github.PeterHausenAoi.evo.evolution;

import com.github.PeterHausenAoi.evo.entities.Actor;

import java.util.ArrayList;
import java.util.List;

public class Generation<T extends Actor> {
    private static final String TAG = Generation.class.getSimpleName();

    private int mCurrInd;
    private final Integer mGen;
    private List<T> mSpecimens;

    public Generation(Integer gen) {
        this.mGen = gen;
        this.mSpecimens = new ArrayList<>();
        mCurrInd = 0;
    }

    public Generation(Integer gen, List<T> specimens) {
        this.mGen = gen;
        this.mSpecimens = specimens;
    }

    public Integer getGen() {
        return mGen;
    }

    public List<T> getSpecimens() {
        return mSpecimens;
    }

    public void addSpecimen(T specimen){
        mSpecimens.add(specimen);
    }

    public T getNext(){
        if (mCurrInd == mSpecimens.size()){
            mCurrInd = 0;
        }

        T specimen = mSpecimens.get(mCurrInd);
        mCurrInd++;

        return specimen;
    }
}