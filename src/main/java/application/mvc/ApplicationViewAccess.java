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

    LocalDate getLastDate();

    ArrayList<AssetBuy> getAllBuys();

    List<Boolean> getBuyLines(Integer maxRange);

    List<Value[]> getRelativeLines(Integer maxRange);

    Value getBuyWin(AssetBuy buy);

    Double getWknPointAtDate(String wkn, LocalDate minusDays) throws DateNotFound;

    Set<Wkn> getWkns();

    HashMap<String, Value> getTodayStats();

    Wkn getWkn(String wkn);

    HashMap<String, Double> getAssetSize();

    HashMap<String, List<Double>> getWatchChange() throws IOException;

    double getWknChangeAtDate(String wkn, LocalDate date);

    HashMap<String, Value> getWknTypeSums();

    double getTotalChangeAtDate(LocalDate date);
}
