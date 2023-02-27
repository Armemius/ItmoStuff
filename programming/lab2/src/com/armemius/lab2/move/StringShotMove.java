package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class StringShotMove extends StatusMove {
    public StringShotMove() {
        super(Type.BUG, 0, 0.95);
    }

    @Override
    protected void applyOppEffects(Pokemon victim) {
        victim.addEffect(new Effect().turns(-1).stat(Stat.SPEED, 2));
        System.out.println("Скорость " + victim + " снижена");
    }

    @Override
    protected String describe() {
        return "выстреливает паутинкой";
    }
}
