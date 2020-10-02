package application.core.model.exception;

import java.time.LocalDate;

public class DateNotFound extends Exception {

    private final String date;

    public DateNotFound(LocalDate date) {
        this.date = date.toString();
    }

    @Override
    public void printStackTrace() {
        System.out.println("Date not found: " + date);
    }
}
