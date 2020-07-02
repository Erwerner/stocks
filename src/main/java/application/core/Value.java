package application.core;

public class Value {
    private Double value;
    private Double total;

    public Value(Double value) {
        this.value = value;
    }

    public static Value calcFromAmount(Double value, Integer amount) {
        return new Value(value * amount);
    }

    public Double getValue() {
        return value;
    }

    public Double getPercentage() {
        return value / total;
    }

    public void addValue(Double add){
        value+= add;
    }
    public void addValue(Value add){
        value+= add.getValue();
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
