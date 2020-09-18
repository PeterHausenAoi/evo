package com.github.PeterHausenAoi.evo.evolution;

import com.github.PeterHausenAoi.evo.entities.Actor;

import java.util.Set;

public class SpeciesDescriptor<T extends Actor> {
    private static final String TAG = SpeciesDescriptor.class.getSimpleName();

    private final Class<T> mClazz;
    private final EntityBuilder<T> mEntityBuilder;
    private final Set<SpeciesParam> mParams;

    public SpeciesDescriptor(EntityBuilder<T> entityBuilder, Set<SpeciesParam> params, Class<T> clazz) {
        this.mEntityBuilder = entityBuilder;
        this.mParams = params;
        this.mClazz = clazz;
    }

    public Set<SpeciesParam> getParams() {
        return mParams;
    }

    public EntityBuilder<T> getActorBuilder() {
        return mEntityBuilder;
    }

    public Class<T> getClazz() {
        return mClazz;
    }
}