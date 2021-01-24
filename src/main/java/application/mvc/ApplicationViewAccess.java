package application.mvc;

import application.core.model.Value;
import application.core.model.Wkn;
import application.core.output.BuyOutput;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ApplicationViewAccess {

    List<Value[]> getLines(Integer maxRange);

    HashMap<String, Value> getTodayStats();

    Wkn getWkn(String wkn);

    HashMap<String, Double> getAssetSize();

    HashMap<String, List<Double>> getBuyWatch();

    HashMap<String, Value> getWknTypeSums();

    HashMap<String, List<Double>> getWatchAll();

    double getBuyCash();

    HashMap<LocalDate, Value> getChangeDate();

    List<Double> getRoisWithSold();

    Double getRoiTodayWithoutSold();

    Map<String, List<String>> getGroups();

    List<BuyOutput> getBuyOutputs();
}
