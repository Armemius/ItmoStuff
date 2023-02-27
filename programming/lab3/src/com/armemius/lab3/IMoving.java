package com.armemius.lab3;

public interface IMoving {
    void moveTo(String name, MoveAction action, IMoving... companions);
}

@FunctionalInterface
interface MoveAction {
    void action();
}
