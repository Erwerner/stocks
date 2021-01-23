package application.mvc;

import application.core.model.AssetBuy;
import application.core.model.Value;
import application.core.model.Wkn;
import application.core.model.exception.DateNotFound;

import java.time.LocalDate;
import java.util.*;

public interface ApplicationViewAccess {

    LocalDate getLastDate();

    ArrayList<AssetBuy> getAllBuys();

    List<Value[]> getLines(Integer maxRange);

    Value getBuyWin(AssetBuy buy);

    Double getWknPointAtDate(String wkn, LocalDate minusDays) throws DateNotFound;

    HashMap<String, Value> getTodayStats();

    Wkn getWkn(String wkn);

    HashMap<String, Double> getAssetSize();

    HashMap<String, List<Double>> getBuyWatch()  ;

    double getWknChangeAtDate(String wkn, LocalDate date);

    HashMap<String, Value> getWknTypeSums();

    HashMap<String, List<Double>> getWatchAll()  ;

    double getBuyCash();

    HashMap<LocalDate, Value> getChangeDate();

    List<Double> getRoisWithSold();

    Double getRoiTodayWithoutSold();

    Map<String, List<String>> getGroups();
}
