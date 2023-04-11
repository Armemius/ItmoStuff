package com.armemius.lab6.collection.data;

import com.armemius.lab6.collection.exceptions.CollectionRuntimeException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
public class Coordinates implements Serializable {
    public Coordinates() {}

    public static final int MIN_Y = -266;

    public Coordinates(int x, Long y) {
        setX(x);
        setY(y);
    }

    @Serial
    private static final long serialVersionUID = -736627690;
    private int x;
    private Long y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        if (y <= MIN_Y) {
            throw new CollectionRuntimeException("Incorrect y parameter Coordinates");
        } else {
            this.y = y;
        }
    }

    @Override
    public String toString() {
        return "Coordinates(x: " + x
                + ", y: " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Coordinates that)) {
            return false;
        }
        return x == that.x && y.equals(that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
