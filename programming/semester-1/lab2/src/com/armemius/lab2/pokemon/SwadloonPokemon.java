package com.armemius.lab2.pokemon;

import com.armemius.lab2.move.Moves;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class SwadloonPokemon extends Pokemon {
    public SwadloonPokemon(String name, int lvl) {
        super(name, lvl);
        addType(Type.BUG);
        addType(Type.GRASS);
        setStats(55, 63, 90, 50, 80, 42);
        setMove(Moves.FACADE, Moves.STRING_SHOT, Moves.GRASS_WHISTLE);
    }
}
