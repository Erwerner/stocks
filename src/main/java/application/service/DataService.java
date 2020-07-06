package application.service;

import application.core.*;
import application.core.exception.DateNotFound;

import java.time.LocalDate;

public class DataService {

    public Double calcCostsAtDate(LocalDate date, ApplicationData data) {
        Double cost = 0.0;
        for (Asset asset : data.getAssets().values()) {
            cost += asset.getCostAtDate(date);
        }
        return cost;
    }

    public LocalDate calcLastDate(ApplicationData data) {
        LocalDate date = null;
        for (Asset asset : data.getAssets().values()) {
            LocalDate wknDate = asset.getLastDate();
            if (date == null || date.isBefore(wknDate))
                date = wknDate;
        }
        return date;
    }

    public boolean calcHasBuyAtDate(LocalDate date, ApplicationData data) {
        for (Asset asset : data.getAssets().values()) {
            for (AssetBuy buy : asset.getActiveBuys()) {
                if (buy.getDate().equals(date))
                    return true;
            }
        }
        return false;
    }


    public Value calcBuyWin(AssetBuy buy, ApplicationData data) {
        Value value = new Value();
        try {
            String wkn = buy.getWkn();
            LocalDate lastDate = calcLastDate(data);
            Double buyValue = data.getAssets().get(wkn).getWknPointForDate(lastDate).getValue() * buy.getAmount();
            value.addValue(buyValue).sub(buy.getCosts()).setTotal(buy.getCosts());
        } catch (DateNotFound dateNotFound) {
            dateNotFound.printStackTrace();
        }
        return value;
    }

    public Wkn createWkn(String wkn, ApplicationData data) {
        return new Wkn(wkn, data.getWknName(wkn), data.getWknType(wkn), data.getWknUrl(wkn));
    }


    public double calcWknChangeToday(String wkn, ApplicationData data, LocalDate date) {
        Double old = 1.0;
        Double neu = 1.0;
        try {
            old = data.getAssets().get(wkn).getWknPointAtDate(date.minusDays(1));
            neu = data.getAssets().get(wkn).getWknPointAtDate(date);
        } catch (DateNotFound dateNotFound) {
            dateNotFound.printStackTrace();
        }
        return neu / old - 1;
    }

    public double calcBuyCash(ApplicationData data) {
        double buyCash;
        double cash = data.getCash();
        double total = calcTotalAtDate(data, calcLastDate(data));
        buyCash = total * ((cash / total) - 0.20);
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
}
