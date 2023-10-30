package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class ShadowClawMove extends PhysicalMove {
    public ShadowClawMove() {
        super(Type.GHOST, 70, 1.0);
    }

    @Override
    protected double calcCriticalHit(Pokemon att, Pokemon def) {
        if (0.125 > Math.random()) {
            System.out.println(def + " получил критический урон!");
            return 2.0;
        } else {
            return 1.0;
        }
    }

    @Override
    protected String describe() {
        return "использует теневой клык";
    }
}
