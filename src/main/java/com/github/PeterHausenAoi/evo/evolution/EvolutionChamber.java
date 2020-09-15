package main.java.com.github.PeterHausenAoi.evo.evolution;

import main.java.com.github.PeterHausenAoi.evo.entities.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvolutionChamber<T extends Actor> {
    private static final String TAG = EvolutionChamber.class.getSimpleName();

    private static final Integer DEF_POPULATION_SIZE = 150;
    private static final Double DEF_MUTATION_RATE = 0.05D;

    private Integer mPopulationSize;
    private Double mMutationRate;

    private Integer mGenSeq;

    private List<Specimen> mSpecimens;
    private Generation<T> mGeneration;

    private List<Integer> mMatingPool = new ArrayList<>();

    private final SpeciesDescriptor<T> mSpeciesDescriptor;

    public EvolutionChamber(SpeciesDescriptor<T> speciesDescriptor) {
        mSpecimens = new ArrayList<>();
        mPopulationSize = DEF_POPULATION_SIZE;
        mMutationRate = DEF_MUTATION_RATE;

        mSpeciesDescriptor = speciesDescriptor;
        mGenSeq = 1;
    }

    private void initSpecies(){
        mGeneration = new Generation<>(mGenSeq);
        mGenSeq++;

        for (int i = 0; i < mPopulationSize; i++){
            Map<String, Double> params = new HashMap<>();

            for (SpeciesParam param : mSpeciesDescriptor.getParams()){
                params.put(param.getName(), Math.random() * (param.getMax() - param.getMin()) + param.getMin());
            }

            Specimen spec = new Specimen(0.0, params);
            mGeneration.addSpecimen(mSpeciesDescriptor.getActorBuilder().buildEntity(spec));
        }
    }

    private void addSpecimen(T actor){
        mSpecimens.add(actor.toSpecimen());

        if (mSpecimens.size() >= mPopulationSize){
            generateGen();
        }
    }

    private void generateGen(){

    }

    public T getNextSpecimen(){
        if (mGeneration == null) {
            initSpecies();
        }

        return mGeneration.getNext();
    }
}