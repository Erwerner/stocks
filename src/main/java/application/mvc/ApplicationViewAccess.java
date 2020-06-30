package application.mvc;

import application.core.StockBuy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface ApplicationViewAccess {

    // View
    Double[] getTotalLine(LocalDate date);

    LocalDate getLastDate();

    Double getCostsAtDate(LocalDate date);

    ArrayList<StockBuy> getAllBuys();

    List<Double[]> getProfitLines(Integer maxRange);

    String getWknName(String wkn);

    List<Boolean> getBuyLines(Integer maxRange);

    List<Double[]> getRelativeLines(Integer maxRange);

    Double getBuyWin(StockBuy buy);


    //Todo
    //Toggle Win
    //Toggle Lose
}
