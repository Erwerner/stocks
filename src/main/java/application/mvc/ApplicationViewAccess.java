package application.mvc;

import application.core.AssetBuy;
import application.core.Value;
import application.core.Wkn;
import application.core.exception.DateNotFound;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface ApplicationViewAccess {

    // View
    Double[] getTotalLine(LocalDate date);

    LocalDate getLastDate();

    ArrayList<AssetBuy> getAllBuys();

    List<Double[]> getProfitLines(Integer maxRange);

    List<Boolean> getBuyLines(Integer maxRange);

    List<Double[]> getRelativeLines(Integer maxRange);

    Double getBuyWin(AssetBuy buy);

    Double getWknPointAtDate(String wkn, LocalDate minusDays) throws DateNotFound;

    Set<Wkn> getWkns();

    HashMap<String, Double> getTodayStats();

    Wkn getWkn(String wkn);

    HashMap<String, Double> getAssetSize();

    HashMap<String, List<Double>> getWatchChange() throws IOException;

    double getWknChangeAtDate(String wkn, LocalDate date);

    HashMap<String, Value> getWknTypeSums();

    double getCash();
}
