package com.armemius.lab2.pokemon;

import com.armemius.lab2.move.Moves;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class MeloettaPokemon extends Pokemon {
    public MeloettaPokemon(String name, int lvl ) {
        super(name, lvl);
        addType(Type.NORMAL);
        addType(Type.PSYCHIC);
        setStats(100, 77, 77, 128, 128, 90);
        setMove(Moves.CALM_MIND, Moves.PSYCHIC, Moves.TEETER_DANCE, Moves.SHADOW_CLAW);
    }
}
