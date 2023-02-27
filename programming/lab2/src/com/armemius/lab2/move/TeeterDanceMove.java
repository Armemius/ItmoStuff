package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class TeeterDanceMove extends StatusMove {
    public TeeterDanceMove() {
        super(Type.NORMAL, 0, 1.0);
    }

    @Override
    protected void applyOppEffects(Pokemon victim) {
        Effect.confuse(victim);
        System.out.println(victim + " сконфужен данным обстоятельством");
    }

    @Override
    protected String describe() {
        return "нереально флексит";
    }
}
