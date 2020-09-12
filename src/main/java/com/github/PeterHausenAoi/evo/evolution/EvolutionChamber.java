package main.java.com.github.PeterHausenAoi.evo.evolution;

import java.util.ArrayList;
import java.util.List;

public class EvolutionChamber<T> {
    private static final String TAG = EvolutionChamber.class.getSimpleName();

    private List<Specimen> mSpecimens;
    private List<Generation<T>> mGenerations;

    public EvolutionChamber() {
        mSpecimens = new ArrayList<>();
        mGenerations = new ArrayList<>();
    }

    public List<Specimen> getSpecimens() {
        return mSpecimens;
    }

    public List<Generation<T>> getGenerations() {
        return mGenerations;
    }
}