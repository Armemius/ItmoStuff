package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class FacadeMove extends PhysicalMove {
    public FacadeMove() {
        super(Type.NORMAL, 70, 1.0);
    }

    @Override
    protected double calcBaseDamage(Pokemon att, Pokemon def) {
        Status condition = att.getCondition();
        return super.calcBaseDamage(att, def)
                * (condition.equals(Status.POISON) || condition.equals(Status.BURN) || condition.equals(Status.PARALYZE) ? 2.0 : 1.0);
    }

    @Override
    protected String describe() {
        return "наносит удар";
    }
}
