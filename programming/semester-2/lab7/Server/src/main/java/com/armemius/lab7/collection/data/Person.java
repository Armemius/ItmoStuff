package com.armemius.lab7.collection.data;


import com.armemius.lab7.collection.exceptions.CollectionRuntimeException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
public class Person implements Serializable {
    public Person() {}

    public final static int MIN_HEIGHT = 0;

    public Person(String name, float height, EyeColor eyeColor, HairColor hairColor, Country nationality, Location location) {
        setName(name);
        setHeight(height);
        setEyeColor(eyeColor);
        setHairColor(hairColor);
        setNationality(nationality);
        setLocation(location);
    }

    @Serial
    private static final long serialVersionUID = 1559997845;
    private String name;
    private float height;
    private EyeColor eyeColor;
    private HairColor hairColor;
    private Country nationality;
    private Location location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new CollectionRuntimeException("Incorrect parameters for setter");
        }
        this.name = name;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        if (height <= MIN_HEIGHT) {
            throw new CollectionRuntimeException("Incorrect parameters for setter");
        }
        this.height = height;
    }

    public EyeColor getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(EyeColor eyeColor) {
        if (eyeColor == null) {
            throw new CollectionRuntimeException("Incorrect parameters for setter");
        }
        this.eyeColor = eyeColor;
    }

    public HairColor getHairColor() {
        return hairColor;
    }

    public void setHairColor(HairColor hairColor) {
        if (hairColor == null) {
            throw new CollectionRuntimeException("Incorrect parameters for setter");
        }
        this.hairColor = hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        if (nationality == null) {
            throw new CollectionRuntimeException("Incorrect parameters for setter");
        }
        this.nationality = nationality;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (location == null) {
            throw new CollectionRuntimeException("Incorrect parameters for setter");
        }
        this.location = location;
    }

    @Override
    public String toString() {
        return "Person(name: " + name
                + ", height: " + height
                + ", eyeColor:" + eyeColor
                + ", hairColor: " + hairColor
                + ", nationality:" + nationality
                + ", location:" + location + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Person person)) {
            return false;
        }
        return Float.compare(person.height, height) == 0
                && name.equals(person.name)
                && eyeColor == person.eyeColor
                && hairColor == person.hairColor
                && nationality == person.nationality
                && location.equals(person.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,
                height,
                eyeColor,
                hairColor,
                nationality,
                location);
    }
}
