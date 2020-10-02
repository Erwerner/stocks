package application.core.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class WknPoint {
    LocalDate date;
    Double value;

    public Double calcPercentageDifferenceTo(WknPoint other) {
        return other.getValue() / this.getValue() - this.getValue();
    }
}
