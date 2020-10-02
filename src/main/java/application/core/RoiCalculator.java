package application.core;

import java.time.Duration;
import java.time.LocalDate;

public class RoiCalculator {
    public static double calcRoiFromRange(LocalDate startDate, LocalDate endDate, Double percentageDifference) {
        long daysOfRange = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
        double years = daysOfRange / 365.0;
        return Math.pow(1 + percentageDifference, 1 / years) - 1;
    }
}
