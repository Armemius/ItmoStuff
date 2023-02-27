package com.armemius.lab3;

public class House {
    static void enter(IMoving mover) {
        System.out.println(mover.toString() + " зашёл в дом");
    }

    static void leave(IMoving mover) {
        System.out.println(mover.toString() + " вышел из дома");
    }
}
