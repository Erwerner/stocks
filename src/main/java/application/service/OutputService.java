package application.service;

import application.core.ApplicationData;
import application.core.Asset;
import application.core.Value;
import application.core.Wkn;
import application.core.exception.DateNotFound;

import java.time.LocalDate;
import java.util.*;

import static java.lang.StrictMath.sqrt;

public class OutputService {
    private final DataService dataService;

    public OutputService(DataService dataService) {
        this.dataService = dataService;
    }

    public HashMap<String, Value> createTodayStats(ApplicationData data) {
        HashMap<String, Value> todayStats = new HashMap<>();
        LocalDate lastDate = dataService.calcLastDate(data);
        Value old = new Value(getTotalLineAtDate(lastDate.minusDays(1), data)[0]);
        Value neu = new Value(getTotalLineAtDate(lastDate, data)[0]);
        Double todayCosts = dataService.calcCostsAtDate(lastDate, data);

        todayStats.put("old ", old);
        todayStats.put("new", neu);
        todayStats.put("diff", neu.copy().sub(old));
        todayStats.put("win ", neu.copy().sub(todayCosts));
        todayStats.values().forEach(value -> value.setTotal(todayCosts));
        return todayStats;
    }

    public List<Boolean> createBuyLines(Integer maxRange, ApplicationData data) {
        List<Boolean> buys = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            buys.add(dataService.calcHasBuyAtDate(dataService.calcLastDate(data).minusDays(i), data));
        }
        return buys;
    }


    public List<Value[]> createLines(Integer maxRange, ApplicationData data) {
        List<Value[]> lines = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            LocalDate date = dataService.calcLastDate(data).minusDays(i);
            Value[] relativeLine = new Value[]{new Value(), new Value()};
            Double[] totalLine = getTotalLineAtDate(date, data);
            Double[] profitLine = new Double[]{totalLine[0] - dataService.calcCostsAtDate(date, data), totalLine[1] - dataService.calcCostsAtDate(date, data)};
            relativeLine[0] = new Value(profitLine[0]).setTotal(dataService.calcCostsAtDate(date, data));
            relativeLine[1] = new Value(profitLine[1]).setTotal(dataService.calcCostsAtDate(date, data));
            lines.add(relativeLine);
        }
        return lines;
    }

    private Double[] getTotalLineAtDate(LocalDate date, ApplicationData data) {
        Double start = 0.0;
        Double end = 0.0;
        for (String wkn : data.getAssets().keySet()) {
            try {
                Asset asset = data.getAssets().get(wkn);
                start += asset.getValueAtDateWithBuy(date).getValue();
                end += asset.getValueAtDateWithoutBuy(date.plusDays(1)).getValue();
            } catch (DateNotFound dateNotFound) {
            }
        }
        return new Double[]{start, end};
    }

    public HashMap<String, Double> createAssetSize(ApplicationData data) {
        HashMap<String, Double> fonds = new HashMap<>();
        Set<Wkn> wkns = new HashSet<>();
        data.getAssets().keySet().forEach(wkn1 -> wkns.add(dataService.createWkn(wkn1, data)));
        for (Wkn wkn : wkns) {
            if (data.getAssets().get(wkn.getWkn()).getActiveBuys().isEmpty())
                continue;
            LocalDate date = dataService.calcLastDate(data);
            try {
                double totalK = new Double(data.getAssets().get(wkn.getWkn()).getValueAtDateWithBuy(date).getValue() / 100).intValue() / 10.0;

                fonds.put(wkn.getWknName() + " " + wkn.getWknType(), totalK);
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
        }
        return fonds;
    }

    public HashMap<String, List<Double>> createWatchChangeToday(String[] watchWkns, ApplicationData data) {
        HashMap<String, List<Double>> watchToday = new HashMap<>();
        LocalDate lastDate = dataService.calcLastDate(data);
        for (String watchWkn : watchWkns) {
            List<Double> values = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                double today = dataService.calcWknChangeToday(watchWkn, data, lastDate.minusDays(i));
                if (today == 0.0)
                    continue;
                values.add(today);
            }
            watchToday.put(watchWkn, values);
        }
        return watchToday;
    }

    public HashMap<String, Value> calcWknTypeSums(ApplicationData data) {
        HashMap<String, Value> sums = new HashMap<>();
        double total = 0.0;
        data.getAssets().forEach((wkn, asset) -> {
            if (!asset.getActiveBuys().isEmpty()) {
                String wknType = data.getWknType(wkn);
                if (!sums.containsKey(wknType))
                    sums.put(wknType, new Value(0.0));
                try {
                    Value value = asset.getValueAtDateWithBuy(dataService.calcLastDate(data));
                    sums.get(wknType).addValue(value);
                } catch (DateNotFound dateNotFound) {
                    dateNotFound.printStackTrace();
                }
            }
        });
        sums.put("CASH", new Value(data.getCash()));

        for (Value value : sums.values()) {
            total += value.getValue();
        }

        addComb(sums, Arrays.asList("CASH"), "> CASH");
        addComb(sums, Arrays.asList("ETC GOLD", "GOLD"), "> GOLD");
        addComb(sums, Arrays.asList("ETC"), "> METAL");
        addComb(sums, Arrays.asList("FOND SW", "FOND DIV", "FOND", "FOND ROB"), "> FOND");
        addComb(sums, Arrays.asList("FOND SW", "FOND DIV", "FOND", "FOND ROB", "ETC", "ETC GOLD", "GOLD", "CASH"), "> Total");

        for (Value value : sums.values()) {
            value.setTotal(total);
        }
        return sums;
    }

    private void addComb(HashMap<String, Value> sums, List<String> combFond, String s) {
        sums.put(s, new Value(0.0));
        sums.forEach((wknType, value) -> {
            if (combFond.contains(wknType)) {
                sums.get(s).addValue(value);
            }
        });
    }

    public double calcTotalChangeAtDate(LocalDate date, ApplicationData data) {
        Double oldTotal = getTotalLineAtDate(date.minusDays(1), data)[0];
        Double neuTotal = getTotalLineAtDate(date, data)[0];
        return neuTotal - oldTotal;
    }

    public HashMap<String, List<Double>> createBuyWatch(String[] watchWkns, ApplicationData data) {
        HashMap<String, List<Double>> watchChangeToday = createWatchChangeToday(watchWkns, data);
        HashMap<String, List<Double>> relevantWatchs = new HashMap<>();
        watchChangeToday.forEach((s, doubles) -> {
            int countNegative = 0;
            double sumChange = 0;
            for (Double aDouble : doubles) {
                sumChange += aDouble;
                if (sumChange < 0)
                    countNegative++;
            }
            if (countNegative > 3)
                relevantWatchs.put(s, doubles);
        });
        return relevantWatchs;
    }

    public HashMap<LocalDate, Value> createChangeDate(ApplicationData data, LocalDate date) {
        LocalDate lastDate = dataService.calcLastDate(data);
        double todayProfit = dataService.calcTotalAtDate(data, lastDate) - dataService.calcCostsAtDate(lastDate, data);
        Value change = new Value(todayProfit);

        double dateProfit = dataService.calcTotalAtDate(data, date) - dataService.calcCostsAtDate(date, data);
        change.sub(dateProfit);

        double todayTotal = dataService.calcTotalAtDate(data, lastDate);
        change.setTotal(todayTotal + change.getValue() * -1);

        HashMap<LocalDate, Value> dateChange = new HashMap<>();
        dateChange.put(date, change);
        return dateChange;
    }
}
