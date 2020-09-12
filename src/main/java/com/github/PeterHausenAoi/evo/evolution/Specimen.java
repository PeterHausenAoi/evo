package main.java.com.github.PeterHausenAoi.evo.evolution;

import java.util.Collections;
import java.util.Map;

public class Specimen {
    private static final String TAG = Specimen.class.getSimpleName();

    private final Double mFitness;
    private final Map<String, Double> mProps;

    public Specimen(Double fitness, Map<String, Double> props) {
        this.mFitness = fitness;
        this.mProps = Collections.unmodifiableMap(props);
    }

    public Double getFitness() {
        return mFitness;
    }

    public Map<String, Double> getProps() {
        return mProps;
    }
}