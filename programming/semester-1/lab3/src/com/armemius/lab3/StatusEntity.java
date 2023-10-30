package com.armemius.lab3;

public class StatusEntity extends Entity{
    public StatusEntity(String name, Status status) {
        super(name, status);
    }

    @Override
    public String getName() {
        return (status == Status.HAPPY ? "Счастливый " : "") + super.getName();
    }
}
