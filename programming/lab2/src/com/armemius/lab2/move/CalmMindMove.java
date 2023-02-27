package com.armemius.lab2.move;

import ru.ifmo.se.pokemon.*;

public class CalmMindMove extends StatusMove {
    public CalmMindMove() {
        super(Type.PSYCHIC, 0, 1.0);
    }

    @Override
    protected void applySelfEffects(Pokemon user) {
        user.addEffect(new Effect().turns(-1).stat(Stat.SPECIAL_ATTACK, 1));
        user.addEffect(new Effect().turns(-1).stat(Stat.SPECIAL_DEFENSE, 1));
        System.out.println(user + " повышает свою специальную атаку и защиту!");
    }

    @Override
    protected String describe() {
        return "успокаивает свой разум";
    }
}
