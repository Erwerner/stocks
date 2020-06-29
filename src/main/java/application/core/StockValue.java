package application.core;

public class StockValue {
    private final Double value;

    public StockValue(Double value) {
        this.value = value;
    }

    public static StockValue calc(Double value, Integer amount) {
        return new StockValue(value * amount);
    }

    public Double getValue() {
        return value;
    }
}
