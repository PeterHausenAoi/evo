package main.java.com.github.PeterHausenAoi.evo.evolution;

public class SpeciesParam {
    private static final String TAG = SpeciesParam.class.getSimpleName();

    private final String mName;
    private final Double mMin;
    private final Double mMax;

    private final boolean mRand;

    public SpeciesParam(String name, Double min, Double max, boolean mRand) {
        this.mName = name;
        this.mMin = min;
        this.mMax = max;
        this.mRand = mRand;
    }

    public String getName() {
        return mName;
    }

    public Double getMin() {
        return mMin;
    }

    public Double getMax() {
        return mMax;
    }

    public boolean isRand() {
        return mRand;
    }
}