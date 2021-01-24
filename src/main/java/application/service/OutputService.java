package application.service;

import application.core.RoiCalculator;
import application.core.model.*;
import application.core.model.exception.DateNotFound;
import application.core.output.BuyOutput;

import java.time.LocalDate;
import java.util.*;

public class OutputService {
    private final DataService dataService;
    private final AssetService assetService;

    public OutputService(DataService dataService, AssetService assetService) {
        this.dataService = dataService;
        this.assetService = assetService;
    }

    public HashMap<String, Value> createTodayStats(ApplicationData data) {
        HashMap<String, Value> todayStats = new HashMap<>();
        Value diff = getDiffToday(data);
        todayStats.put("diff", diff);
        return todayStats;
    }

    public Value getDiffToday(ApplicationData data) {
        LocalDate lastDate = dataService.calcLastDate(data);
        Value old = new Value(getTotalLineAtDate(lastDate.minusDays(1), data)[0]);
        Value neu = new Value(getTotalLineAtDate(lastDate, data)[0]);
        Double todayCosts = calcCostsAtDate(lastDate, data);

        Value diff = neu.copy().sub(old);
        diff.setTotal(todayCosts);
        return diff;
    }

    private Double calcCostsAtDate(LocalDate date, ApplicationData data) {
        Double cost = 0.0;
        for (Asset asset : data.getAssets().values()) {
            cost += asset.getCostAtDate(date);
        }
        return cost;
    }

    public List<Value[]> createLines(Integer maxRange, ApplicationData data) {
        List<Value[]> lines = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            LocalDate date = dataService.calcLastDate(data).minusDays(i);
            Value[] relativeLine = new Value[]{new Value(), new Value()};
            Double[] totalLine = getTotalLineAtDate(date, data);
            Double[] profitLine = new Double[]{totalLine[0] - calcCostsAtDate(date, data), totalLine[1] - calcCostsAtDate(date, data)};
            relativeLine[0] = new Value(profitLine[0]).setTotal(calcCostsAtDate(date, data));
            relativeLine[1] = new Value(profitLine[1]).setTotal(calcCostsAtDate(date, data));
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
                //ignore
            }
        }
        return new Double[]{start, end};
    }

    public HashMap<String, Double> createAssetSize(ApplicationData data) {
        HashMap<String, Double> fonds = new HashMap<>();
        Set<Wkn> wkns = new HashSet<>();
        data.getAssets().keySet().forEach(wkn1 -> wkns.add(assetService.createWkn(wkn1, data)));
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

    public HashMap<String, List<Double>> createWatchChangeToday(Collection<String> watchWkns, ApplicationData data) {
        HashMap<String, List<Double>> watchToday = new HashMap<>();
        LocalDate lastDate = dataService.calcLastDate(data);
        for (String watchWkn : watchWkns) {
            List<Double> values = new ArrayList<>();
            for (int i = 0; i < 35; i++) {
                double today = assetService.calcAssetChangeToday(data.getAssets().get(watchWkn), lastDate.minusDays(i));
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

        Double totalFinal = total;
        sums.values().forEach(value -> value.setTotal(totalFinal));
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

    public Value calcBuyWin(AssetBuy buy, ApplicationData data) {
        Value value = new Value();
        try {
            String wkn = buy.getWkn();
            LocalDate lastDate = dataService.calcLastDate(data);
            Double buyValue = data.getAssets().get(wkn).getWknPointForDate(lastDate).getValue() * buy.getAmount();
            value.addValue(buyValue).sub(buy.getCosts()).setTotal(buy.getCosts());
        } catch (DateNotFound dateNotFound) {
            dateNotFound.printStackTrace();
        }
        return value;
    }

    public HashMap<String, List<Double>> createBuyWatch(List<String> watchWkns, ApplicationData data) {
        HashMap<String, List<Double>> watchChangeToday = createWatchChangeToday(watchWkns, data);
        HashMap<String, List<Double>> relevantWatchs = new HashMap<>();
        watchChangeToday.forEach((s, doubles) -> {
            boolean maxNeg = false;
            int countNegative = 0;
            double sumChange = 0;
            for (Double aDouble : doubles) {
                sumChange += aDouble;
                if (sumChange < -0.02)
                    maxNeg = true;
                if (sumChange < 0)
                    countNegative++;
            }
            if (countNegative > 4 && maxNeg)
                relevantWatchs.put(s, doubles);
        });
        return relevantWatchs;
    }

    public HashMap<LocalDate, Value> createChangeDate(ApplicationData data, LocalDate date) {
        LocalDate lastDate = dataService.calcLastDate(data);
        double todayProfit = dataService.calcTotalAtDate(data, lastDate) - calcCostsAtDate(lastDate, data);
        Value change = new Value(todayProfit);

        double dateProfit = dataService.calcTotalAtDate(data, date) - calcCostsAtDate(date, data);
        change.sub(dateProfit);

        double todayTotal = dataService.calcTotalAtDate(data, lastDate);
        change.setTotal(todayTotal + change.getValue() * -1);

        HashMap<LocalDate, Value> dateChange = new HashMap<>();
        dateChange.put(date, change);
        return dateChange;
    }

    public List<BuyOutput> getBuyOutputs(ApplicationData data, int minimumDaysForRoi) {
        List<BuyOutput> buyOutputs = new ArrayList<>();
        LocalDate lastDate = dataService.calcLastDate(data);
        for (AssetBuy buy : dataService.getAllBuys(data)) {
            String buyWkn = buy.getWkn();

            double wknChangeToday = assetService.calcAssetChangeToday(data.getAssets().get(buyWkn), lastDate);
            double winDay = 0;
            try {
                winDay = (wknChangeToday * buy.getAmount() * data.getAssets().get(buyWkn).getWknPointAtDate(lastDate));
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }

            Value buyWin = calcBuyWin(buy, data);
            Wkn wkn = assetService.createWkn(buyWkn, data);
            LocalDate buyDate = buy.getDate();
            LocalDate soldDate = buy.getSoldDate();
            buyOutputs.add(new BuyOutput(
                    buyDate,
                    buyWkn,
                    wknChangeToday,
                    winDay,
                    buyWin,
                    buy.isActive(),
                    wkn.getWknType(),
                    wkn.getWknName(),
                    RoiCalculator.calcRoiFromRange(buyDate, soldDate !=null ? soldDate : lastDate, buyWin.getPercentage()),
                    lastDate.minusDays(minimumDaysForRoi).isBefore(buyDate))
            );
        }
        return buyOutputs;
    }
}
