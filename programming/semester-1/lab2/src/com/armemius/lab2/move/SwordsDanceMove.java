package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class SwordsDanceMove extends StatusMove {
    public SwordsDanceMove() {
        super(Type.NORMAL, 0, 1.0);
    }

    @Override
    protected void applySelfEffects(Pokemon user) {
        user.addEffect(new Effect().stat(Stat.ATTACK, 2).turns(-1));
        System.out.println("Атака " + user + " повысилась!");
    }

    @Override
    protected String describe() {
        return "исполняет танец с мечами";
    }
}
