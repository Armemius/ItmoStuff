package com.armemius.lab3;

public class FriendEntity extends Entity implements ISinger {
    public FriendEntity(String name, Status status) {
        super(name, status);
    }

    @Override
    public void sing(String name) {
        if (super.status == Status.BAD_VOICE) {
            System.out.println(this + " тирлимбомбомкает");
        } else {
            System.out.println(this + " поёт " + name);
        }
    }

    @Override
    public String toString() {
        return "Друг " + super.toString();
    }

    @Override
    public String getName() {
        return "Друг " + super.getName();
    }
}
