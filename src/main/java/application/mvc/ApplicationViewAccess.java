package application.mvc;

import application.core.exception.NoBuys;

import java.time.LocalDate;

public interface ApplicationViewAccess {

    // View
    Double[] getTotalLine(LocalDate date);

    LocalDate getLastDate();

    LocalDate getFirstDate() throws NoBuys;

    boolean getDateWasBuy(LocalDate date);

    Double[] getProfitLine(LocalDate date);

    Double getCostsAtDate(LocalDate date);
}
