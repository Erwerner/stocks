package application.core.exception;

import java.time.LocalDate;

public class DateNotFound extends Exception {
    public DateNotFound(LocalDate date) {
        super(date.toString());
    }
}
