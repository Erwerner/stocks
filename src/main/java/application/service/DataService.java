package application.service;

import application.core.ApplicationData;
import application.core.StockAsset;
import application.core.StockBuy;
import application.core.Wkn;
import application.core.exception.DateNotFound;

import java.time.LocalDate;

public class DataService {

    public Double calcCostsAtDate(LocalDate date, ApplicationData data) {
        Double cost = 0.0;
        for (StockAsset stockAsset : data.getAssets().values()) {
            cost += stockAsset.getCostAtDate(date);
        }
        return cost;
    }

    public LocalDate calcLastDate(ApplicationData data) {
        LocalDate date = null;
        for (StockAsset stockAsset : data.getAssets().values()) {
            LocalDate wknDate = stockAsset.getLastDate();
            if (date == null || date.isBefore(wknDate))
                date = wknDate;
        }
        return date;
    }

    public boolean calcHasBuyAtDate(LocalDate date, ApplicationData data) {
        for (StockAsset stockAsset : data.getAssets().values()) {
            for (StockBuy buy : stockAsset.getActiveBuys()) {
                if (buy.getDate().equals(date))
                    return true;
            }
        }
        return false;
    }


    public Double calcBuyWin(StockBuy buy, ApplicationData data) {
        double win = 0.0;
        try {
            String wkn = buy.getWkn();
            LocalDate lastDate = calcLastDate(data);
            Double buyValue = data.getAssets().get(wkn).getWknPointForDate(lastDate).getValue() * buy.getAmount();
            win = (buyValue - buy.getCosts()) / buy.getCosts();
        } catch (DateNotFound dateNotFound) {
            dateNotFound.printStackTrace();
        }
        return win;
    }
    public Wkn createWkn(String wkn, ApplicationData data) {
        return new Wkn(wkn, data.getWknName(wkn), data.getWknType(wkn), data.getWknUrl(wkn));
    }
}
