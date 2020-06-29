package application.mvc;

import application.core.StockBuy;

import java.io.IOException;
import java.time.LocalDate;

public interface ApplicationControllerAccess {
    void addWkn(String wkn) throws IOException;

    void addBuy(String wkn, LocalDate date, Integer amount);
}
