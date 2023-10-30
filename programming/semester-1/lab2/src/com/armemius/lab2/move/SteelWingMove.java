package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class SteelWingMove extends PhysicalMove {
    public SteelWingMove() {
        super(Type.STEEL, 70, 0.9);
    }

    @Override
    protected void applySelfEffects(Pokemon user) {
        user.addEffect(new Effect().turns(-1).stat(Stat.DEFENSE, 1).chance(0.1));
    }

    @Override
    protected String describe() {
        return "наносит удар";
    }
}
