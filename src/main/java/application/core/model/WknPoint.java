package application.core.model;

import java.time.LocalDate;

public class WknPoint {
    private final LocalDate date;
    private final Double value;

    public WknPoint(LocalDate date, Double value) {
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
