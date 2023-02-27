package com.armemius.lab2.pokemon;

import com.armemius.lab2.move.Moves;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class SewaddlePokemon extends Pokemon {
    public SewaddlePokemon(String name, int lvl ) {
        super(name, lvl);
        addType(Type.BUG);
        addType(Type.GRASS);
        setStats(70, 65, 75, 110, 75, 90);
        setMove(Moves.FACADE, Moves.STRING_SHOT);
    }
}
