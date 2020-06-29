package application.core;

import java.time.LocalDate;

public class StockPoint {
    private final LocalDate date;
    private final Double value;

    public StockPoint(LocalDate date, Double value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getValue() {
        return value;
    }
}
