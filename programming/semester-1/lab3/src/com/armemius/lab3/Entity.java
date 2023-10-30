package com.armemius.lab3;

import java.util.Objects;

public abstract class Entity implements ITalker, IMoving {
    public Entity(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    private final String name;
    protected Status status;

    public String getName() {
        return this.name;
    }

    @Override
    public void moveTo(String name, MoveAction action, IMoving... companions) {
        if (companions.length == 0) {
            System.out.println(this + " идёт в: " + name);
            action.action();
            System.out.println(this + " пришёл");
        } else {
            String names = this.toString();
            for (int it = 0; it < companions.length; ++it) {
                if (it + 1 == companions.length) {
                    names += " и " + companions[it].toString();
                } else {
                    names += ", " + companions[it].toString();
                }
            }
            System.out.println(names + " идут в: " + name);
            action.action();
            System.out.println(names + " пришли");
        }

    }

    @Override
    public void tell(ITalker name, String text, ITalker... companions) {
        if (companions.length == 0) {
            System.out.println(this + " говорит " + name + ": " + text);
        } else {
            String names = this.toString();
            for (int it = 0; it < companions.length; ++it) {
                if (it + 1 == companions.length) {
                    names += " и " + companions[it].toString();
                } else {
                    names += ", " + companions[it].toString();
                }
            }
            System.out.println(names + " говорят " + name + ": " + text);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(name, entity.name) && status == entity.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, status);
    }
}
