package application.mvc;

import application.core.model.AssetBuy;
import application.core.model.Value;
import application.core.model.Wkn;
import application.core.model.exception.DateNotFound;

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

    HashMap<String, List<Double>> getBuyWatch() throws IOException;

    double getWknChangeAtDate(String wkn, LocalDate date);

    HashMap<String, Value> getWknTypeSums();

    HashMap<String, List<Double>> getWatchAll() throws IOException;

    double getBuyCash();

    HashMap<LocalDate, Value> getChangeDate();

    List<LocalDate> getDates(Integer maxRange);

    LocalDate getFirstDate();
}
