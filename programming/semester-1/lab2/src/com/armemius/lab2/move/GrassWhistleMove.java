package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class GrassWhistleMove extends StatusMove {
    GrassWhistleMove() {
        super(Type.GRASS, 0 , 0.55);
    }

    @Override
    protected void applyOppEffects(Pokemon victim) {
        Effect.sleep(victim);
    }

    @Override
    protected String describe() {
        return "говорит про пользу сна";
    }
}
