package application.mvc;

import application.core.StockBuy;
import application.core.Wkn;
import application.core.exception.DateNotFound;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface ApplicationViewAccess {

    // View
    Double[] getTotalLine(LocalDate date);

    LocalDate getLastDate();

    ArrayList<StockBuy> getAllBuys();

    List<Double[]> getProfitLines(Integer maxRange);

    List<Boolean> getBuyLines(Integer maxRange);

    List<Double[]> getRelativeLines(Integer maxRange);

    Double getBuyWin(StockBuy buy);

    Double getWknPointAtDate(String wkn, LocalDate minusDays) throws DateNotFound;

    Set<Wkn> getWkns();

    HashMap<String, Double> getTodayStats();

    Wkn getWkn(String wkn);

    HashMap<String, Double> getFondValues();
}
