package application.core;

public class Value {
    private Double value;
    private Double total;

    public Value(Double value) {
        this.value = value;
    }
    public Value() {
        this.value = 0.0;
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

    public Value addValue(Double add){
        value+= add;
        return this;
    }
    public Value addValue(Value add){
        addValue(add.getValue());
        return this;
    }

    public Value setTotal(Double total) {
        this.total = total;
        return this;
    }

    public Value copy(){
        Value value = new Value(this.value);
        value.setTotal(total);
        return value;
    }

    public Value sub(Value old) {
        value-=old.getValue();
        return this;
    }

    public Value sub(Double todayCosts) {
        value-=todayCosts;
        return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
