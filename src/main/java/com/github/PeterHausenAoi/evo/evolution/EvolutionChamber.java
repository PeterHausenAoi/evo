package main.java.com.github.PeterHausenAoi.evo.evolution;

import main.java.com.github.PeterHausenAoi.evo.entities.Actor;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.util.*;

public class EvolutionChamber<T extends Actor> {
    private static final String TAG = EvolutionChamber.class.getSimpleName();

    private static final Integer DEF_POPULATION_SIZE = 50;
    private static final Double DEF_MUTATION_RATE = 0.02D;

    private Class<T> mClazz;

    private Integer mPopulationSize;
    private Double mMutationRate;

    private Integer mGenSeq;

    private List<Specimen> mSpecimens;
    private Generation<T> mGeneration;

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

        for (int i = 0; i < mPopulationSize; i++){
            Map<String, Double> params = new HashMap<>();

            for (SpeciesParam param : mSpeciesDescriptor.getParams()){
                params.put(param.getName(), Math.random() * (param.getMax() - param.getMin()) + param.getMin());
            }

            Specimen spec = new Specimen(mGenSeq, 0.0, params);
            mGeneration.addSpecimen(mSpeciesDescriptor.getActorBuilder().buildEntity(spec));
        }

        mGenSeq++;
    }

    public void addSpecimen(T actor){
        mSpecimens.add(actor.toSpecimen());

        if (mSpecimens.size() >= mPopulationSize){
            generateGen();
        }
    }

    private void generateGen(){
        List<Specimen> currSpecimens = new ArrayList<>(mSpecimens);
        mSpecimens = new ArrayList<>();

        List<List<Specimen>> matingPool = generateMatingPool(currSpecimens);
        List<T> newGenActors = reproduction(matingPool);

        mGeneration = new Generation<>(mGenSeq, newGenActors);
    }

    private List<T> reproduction(List<List<Specimen>> matingPool){
        Log.doLog(TAG, " Reproduction in progress for " + mSpeciesDescriptor.getClazz().getSimpleName() + " " + mGenSeq + "....");

        List<T> newPop = new ArrayList<>();

        for (List<Specimen> parents : matingPool){
            Specimen mom = parents.get(0);
            Specimen dad = parents.get(1);

            Specimen newSpec = mom.crossover(dad, mom, mMutationRate, mSpeciesDescriptor.getParams());
            newPop.add(mSpeciesDescriptor.getActorBuilder().buildEntity(newSpec));
        }

        mGenSeq++;

        return newPop;
    }

    private Specimen getRandomSpecimen(List<Specimen> currSpecimens, Specimen exclude){
        double sumWeight = currSpecimens.stream().filter(specimen -> !specimen.equals(exclude))
                .mapToDouble(Specimen::getFitness)
                .sum();

        double rnd = Math.random() * sumWeight;

        for (Specimen spec : currSpecimens){
            if (spec.equals(exclude)){
                continue;
            }

            if (rnd < spec.getFitness()){
                return spec;
            }

            rnd -= spec.getFitness();
        }

        return null;
    }

    private List<List<Specimen>> generateMatingPool(List<Specimen> currSpecimens){
        List<List<Specimen>> matingPool = new ArrayList<>();

        for (int i = 0; i < mPopulationSize; i++){
            Specimen mom = getRandomSpecimen(currSpecimens, null);
            Specimen dad = getRandomSpecimen(currSpecimens, mom);

            List<Specimen> parents = new ArrayList<>();
            parents.add(mom);
            parents.add(dad);

            matingPool.add(parents);
        }

        return matingPool;
    }

    public T getNextSpecimen(){
        if (mGeneration == null) {
            initSpecies();
        }

        return mGeneration.getNext();
    }
}