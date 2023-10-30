package com.armemius.lab2;

import com.armemius.lab2.pokemon.*;
import ru.ifmo.se.pokemon.*;

public class Main {
    public static void main(String[] args) {
        Battle b = new Battle();
        Pokemon p1 = new MeloettaPokemon("Арбуз", 1);
        Pokemon p2 = new PetitilPokemon("Капуста", 1);
        Pokemon p3 = new LilligantPokemon("Дыня", 1);
        Pokemon p4 = new SewaddlePokemon("Кабачок", 1);
        Pokemon p5 = new SwadloonPokemon("Тыква", 1);
        Pokemon p6 = new LeavannyPokemon("Патиссон", 1);


        b.addAlly(p1);
        b.addAlly(p2);
        b.addAlly(p3);
        b.addFoe(p4);
        b.addFoe(p5);
        b.addFoe(p6);

        b.go();
    }
}