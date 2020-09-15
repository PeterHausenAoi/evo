package main.java.com.github.PeterHausenAoi.evo.evolution;

import main.java.com.github.PeterHausenAoi.evo.entities.Actor;

public interface EntityBuilder<T extends Actor>{
    T buildEntity(Specimen specimen);
}