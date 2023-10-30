package com.armemius.lab2.pokemon;

import com.armemius.lab2.move.Moves;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class PetitilPokemon extends Pokemon {
    public PetitilPokemon(String name, int lvl ) {
        super(name, lvl);
        addType(Type.GRASS);
        setStats(45, 35, 50, 70, 50, 30);
        setMove(Moves.DREAM_EATER, Moves.REST, Moves.FACADE);
    }
}
