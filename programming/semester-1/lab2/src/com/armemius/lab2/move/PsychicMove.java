package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class PsychicMove extends SpecialMove {
    public PsychicMove() {
        super(Type.PSYCHIC, 90.0, 1.0);
    }

    @Override
    protected void applyOppEffects(Pokemon victim) {
        if (0.1 > Math.random()) {
            victim.addEffect(new Effect().turns(-1).stat(Stat.SPECIAL_DEFENSE, -1));
            System.out.println("Специальная защита " + victim + " снижена!");

        }
    }

    @Override
    protected String describe() {
        return "включает увлекательную передачу по рен-тв";
    }
}
