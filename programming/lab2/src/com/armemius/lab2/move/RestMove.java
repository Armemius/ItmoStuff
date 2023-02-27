package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class RestMove extends StatusMove {
    public RestMove() {
        super(Type.PSYCHIC, 0, 1.0);
    }

    @Override
    protected void applySelfEffects(Pokemon user) {
        user.restore();
        user.setCondition(new Effect().condition(Status.SLEEP).attack(0.0).turns(2));
    }

    @Override
    protected String describe() {
        return "вкинулся и откинулся";
    }
}
