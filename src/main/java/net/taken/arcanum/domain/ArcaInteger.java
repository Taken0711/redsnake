package net.taken.arcanum.domain;

import java.util.Objects;

public class ArcaInteger extends ArcaObject {

    private int value;

    public ArcaInteger() {
        super(new ArcaMetaClass("Integer", "Object"));
    }

    public ArcaInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArcaInteger that = (ArcaInteger) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }
}
