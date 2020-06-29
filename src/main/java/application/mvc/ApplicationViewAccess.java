package application.mvc;

import application.core.StockValue;
import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;

import java.time.LocalDate;
import java.util.Set;

public interface ApplicationViewAccess {

    // View
    Double[] getLine(LocalDate date);

    LocalDate getLastDate();

    LocalDate getFirstDate() throws NoBuys;

    Set<String> getWkns();

    StockValue getValue(String wkn, LocalDate date) throws DateNotFound;

    boolean dateWasBuy(LocalDate date);
}
