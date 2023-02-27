package com.armemius.lab2.pokemon;

import com.armemius.lab2.move.Moves;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class LeavannyPokemon extends Pokemon {
    public LeavannyPokemon(String name, int lvl) {
        super(name, lvl);
        addType(Type.BUG);
        addType(Type.GRASS);
        setStats(75, 103, 80, 70, 80, 92);
        setMove(Moves.FACADE, Moves.STRING_SHOT, Moves.GRASS_WHISTLE, Moves.STEEL_WING);
    }
}
