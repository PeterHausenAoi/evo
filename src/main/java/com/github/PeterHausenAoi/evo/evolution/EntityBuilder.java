package com.github.PeterHausenAoi.evo.evolution;

import com.github.PeterHausenAoi.evo.entities.Actor;

public interface EntityBuilder<T extends Actor>{
    T buildEntity(Specimen specimen);
}