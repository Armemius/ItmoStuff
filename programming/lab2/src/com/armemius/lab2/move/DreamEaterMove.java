package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class DreamEaterMove extends SpecialMove {
    public DreamEaterMove() {
        super(Type.PSYCHIC, 100, 1.0);
    }

    private long healAmount = 0;

    @Override
    protected void applyOppDamage(Pokemon victim, double v) {
        boolean isSleeping = victim.getCondition().equals(Status.SLEEP);
        if (!isSleeping) {
            System.out.println("Но " + victim + " не спит");
            v = 0;
        }
        healAmount = Math.round(v);
        super.applyOppDamage(victim, v);
    }

    @Override
    protected void applySelfDamage(Pokemon user, double v) {
        super.applySelfDamage(user, -healAmount);
    }

    @Override
    protected String describe() {
        return "насылает кошмары";
    }
}
