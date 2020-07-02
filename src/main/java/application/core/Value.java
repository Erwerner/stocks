package application.core;

public class Value {
    private final Double value;

    public Value(Double value) {
        this.value = value;
    }

    public static Value calc(Double value, Integer amount) {
        return new Value(value * amount);
    }

    public Double getValue() {
        return value;
    }
}
