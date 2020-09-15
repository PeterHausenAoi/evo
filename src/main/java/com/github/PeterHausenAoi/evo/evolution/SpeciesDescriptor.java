package main.java.com.github.PeterHausenAoi.evo.evolution;

import main.java.com.github.PeterHausenAoi.evo.entities.Actor;

import java.util.Set;

public class SpeciesDescriptor<T extends Actor> {
    private static final String TAG = SpeciesDescriptor.class.getSimpleName();

    private final EntityBuilder<T> mEntityBuilder;
    private final Set<SpeciesParam> mParams;

    public SpeciesDescriptor(EntityBuilder<T> entityBuilder, Set<SpeciesParam> params) {
        this.mEntityBuilder = entityBuilder;
        this.mParams = params;
    }

    public Set<SpeciesParam> getParams() {
        return mParams;
    }

    public EntityBuilder<T> getActorBuilder() {
        return mEntityBuilder;
    }
}