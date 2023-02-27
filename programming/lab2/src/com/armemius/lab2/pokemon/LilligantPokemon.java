package com.armemius.lab2.pokemon;

import com.armemius.lab2.move.Moves;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class LilligantPokemon extends Pokemon {
    public LilligantPokemon(String name, int lvl ) {
        super(name, lvl);
        addType(Type.GRASS);
        setStats(70, 65, 75, 110, 75, 90);
        setMove(Moves.DREAM_EATER, Moves.REST, Moves.FACADE, Moves.SWORDS_DANCE);
    }
}