package application.service;

import application.core.ApplicationData;
import application.core.StockAsset;
import application.core.Wkn;
import application.core.exception.DateNotFound;

import java.time.LocalDate;
import java.util.*;

public class OutputService {
    private DataService dataService;

    public OutputService(DataService dataService) {
        this.dataService = dataService;
    }

    public HashMap<String, Double> createTodayStats(ApplicationData data) {
        HashMap<String, Double> todayStats = new HashMap<>();
        LocalDate lastDate = dataService.calcLastDate(data);
        Double old = getTotalLine(lastDate.minusDays(1), data)[0];
        Double neu = getTotalLine(lastDate, data)[0];
        Double todayCosts = dataService.calcCostsAtDate(lastDate, data);

        todayStats.put("old ", old);
        todayStats.put("new", neu);
        todayStats.put("diff", (neu - old));
        todayStats.put("win ", (neu - todayCosts));
        todayStats.put("win%", (100 * neu / todayCosts - 100));
        return todayStats;
    }

    public List<Boolean> createBuyLines(Integer maxRange, ApplicationData data) {
        List<Boolean> buys = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            buys.add(dataService.calcHasBuyAtDate(dataService.calcLastDate(data).minusDays(i), data));
        }
        return buys;
    }


    public List<Double[]> createProfitLines(Integer maxRange, ApplicationData data) {
        List<Double[]> printLines = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            LocalDate date = dataService.calcLastDate(data).minusDays(i);
            printLines.add(getProfitLine(date, data));
        }
        return printLines;
    }

    public List<Double[]> createRelativeLines(Integer maxRange, ApplicationData data) {
        List<Double[]> lines = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            LocalDate date = dataService.calcLastDate(data).minusDays(i);
            Double[] profitLine = getProfitLine(date, data);
            profitLine[0] = profitLine[0] / dataService.calcCostsAtDate(date, data);
            profitLine[1] = profitLine[1] / dataService.calcCostsAtDate(date, data);
            lines.add(profitLine);
        }
        return lines;
    }


    public Double[] getProfitLine(LocalDate date, ApplicationData data) {
        Double[] totalLine = getTotalLine(date, data);
        return new Double[]{totalLine[0] - dataService.calcCostsAtDate(date, data), totalLine[1] - dataService.calcCostsAtDate(date, data)};
    }

    public Double[] getTotalLine(LocalDate date, ApplicationData data) {
        Double start = 0.0;
        Double end = 0.0;
        for (String wkn : data.getAssets().keySet()) {
            try {
                StockAsset stockAsset = data.getAssets().get(wkn);
                start += stockAsset.getValueAtDateWithBuy(date).getValue();
                end += stockAsset.getValueAtDateWithoutBuy(date.plusDays(1)).getValue();
            } catch (DateNotFound dateNotFound) {
            }
        }
        return new Double[]{start, end};
    }

    public HashMap<String, Double> createFondValues(ApplicationData data) {
        HashMap<String, Double> fonds = new HashMap<>();
        Set<Wkn> wkns = new HashSet<>();
        data.getAssets().keySet().forEach(wkn1 -> wkns.add(dataService.createWkn(wkn1, data)));
        for (Wkn wkn : wkns) {
            if (!wkn.getWknType().equals("FOND"))
                continue;
            if (data.getAssets().get(wkn.getWkn()).getActiveBuys().isEmpty())
                continue;
            LocalDate date = dataService.calcLastDate(data);
            try {
                double totalK = new Double(data.getAssets().get(wkn.getWkn()).getValueAtDateWithBuy(date).getValue() / 100).intValue() / 10.0;

                fonds.put( wkn.getWknName(), totalK );
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
        }
        return fonds;
    }
}