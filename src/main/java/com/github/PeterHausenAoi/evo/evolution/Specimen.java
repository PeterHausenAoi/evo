package main.java.com.github.PeterHausenAoi.evo.evolution;

import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.util.*;

public class Specimen {
    private static final String TAG = Specimen.class.getSimpleName();

    private final Integer mGen;
    private final Double mFitness;
    private final Map<String, Double> mProps;

    public Specimen(Integer gen, Double fitness, Map<String, Double> props) {
        this.mGen = gen;
        this.mFitness = fitness;
        this.mProps = Collections.unmodifiableMap(props);
    }

    public Integer getGen() {
        return mGen;
    }

    public Double getFitness() {
        return mFitness;
    }

    public Map<String, Double> getProps() {
        return mProps;
    }

    public Specimen crossover(Specimen mom, Specimen dad, Double mutationRate, Set<SpeciesParam> params){
        Map<String, Double> newProps = new HashMap<>();

        List<Specimen> parents = new ArrayList<>();
        parents.add(dad);
        parents.add(mom);

        for (SpeciesParam param : params){
            double mutation = Math.random();

            if (param.isRand() || mutation < mutationRate){
                newProps.put(param.getName(), Math.random() * (param.getMax() - param.getMin()) + param.getMin());
                Log.doLog(TAG, "Mutating");
            }else{
                Specimen parent = parents.get(((Long)Math.round(Math.random())).intValue());
                newProps.put(param.getName(), parent.getProps().get(param.getName()));
            }
        }

        return new Specimen(Math.max(mom.getGen(), dad.getGen()) + 1, 0.0, newProps);
    }
}