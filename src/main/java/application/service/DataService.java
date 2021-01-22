package application.service;

import application.core.model.*;
import application.core.model.exception.DateNotFound;

import java.time.LocalDate;
import java.util.HashMap;

public class DataService {
    public LocalDate calcLastDate(ApplicationData data) {
        LocalDate date = null;
        for (Asset asset : data.getAssets().values()) {
            LocalDate wknDate = asset.getLastDate();
            if (date == null || date.isBefore(wknDate))
                date = wknDate;
        }
        return date;
    }

    public double calcBuyCash(ApplicationData data) {
        double buyCash;
        double cash = data.getCash();
        double total = calcTotalAtDate(data, calcLastDate(data));
        buyCash = total * ((cash / total) - 0.15);
        return buyCash;
    }

    public double calcTotalAtDate(ApplicationData data, LocalDate date) {
        double total = data.getCash();
        for (Asset asset : data.getAssets().values()) {
            try {
                total += asset.getValueAtDateWithBuy(date).getValue();
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
        }
        return total;
    }

    public LocalDate calcFirstDate(ApplicationData data) {
        LocalDate date = null;
        for (Asset asset : data.getAssets().values()) {
            for (AssetBuy activeBuy : asset.getActiveBuys()) {
                LocalDate buyDate = activeBuy.getDate();
                if (date == null || date.isAfter(buyDate)) {
                    date = buyDate;
                }
            }
        }
        return date;
    }

    public HashMap<String, Asset> getActiveAssets(ApplicationData data) {
        HashMap<String, Asset> activeAssets = new HashMap<>();
        data.getAssets().values().forEach(asset -> {
            if (asset.getActiveBuys().size() != 0)
                activeAssets.put(asset.getWkn(), asset);
        });
        return activeAssets;
    }
}
