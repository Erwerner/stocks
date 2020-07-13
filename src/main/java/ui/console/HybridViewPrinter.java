package ui.console;

import application.core.AssetBuy;
import application.core.Value;
import application.core.Wkn;
import application.core.exception.DateNotFound;
import application.mvc.ApplicationViewAccess;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HybridViewPrinter {

    public void printBuyLines(Graphics arg0, ApplicationViewAccess model, Integer maxRange, int width) {
        int col = 0;
        int size = width / maxRange;
        for (Boolean buy : model.getBuyLines(maxRange)) {
            if (buy)
                arg0.drawLine(size * col, 0, size * col, 900);
            col += 1;
        }
    }

    public void drawLines(Graphics arg0, ApplicationViewAccess model, Integer maxRange, int width, int showLines) {
        int col = 0;
        int size;
        int zero;
        Double scale;
        size = width / maxRange;
        List<Double[]> lines;
        Double minusTen = 0.0;
        Double plusTen = 0.0;
        List<Value[]> relativeLines = model.getRelativeLines(maxRange);
        lines = new ArrayList<>();
        for (Value[] relativeLine : relativeLines) {
            Double[] line;
            if (showLines == 1) {
                line = new Double[]{relativeLine[0].getPercentage(), relativeLine[1].getPercentage()};
            } else {
                line = new Double[]{relativeLine[0].getValue(), relativeLine[1].getValue()};
            }
            lines.add(line);
        }
        if (showLines == 1) {
            zero = 300;
            scale = -800.0;
        } else {
            zero = 300;
            scale = -0.06;
        }
        for (Double[] printLine : lines) {
            plusTen = 0.1;
            minusTen = -0.1;
            printLine[0] *= scale;
            printLine[1] *= scale;
            printLine[0] += zero;
            printLine[1] += zero;
            minusTen *= scale;
            minusTen += zero;
            plusTen *= scale;
            plusTen += zero;
        }
        for (Double[] printLine : lines) {
            arg0.drawLine(size * col, printLine[0].intValue(), size + size * col, printLine[1].intValue());
            arg0.drawLine(size * col, zero, size + size * col, zero);
            arg0.drawLine(size * col, plusTen.intValue(), size + size * col, plusTen.intValue());
            arg0.drawLine(size * col, minusTen.intValue(), size + size * col, minusTen.intValue());
            col += 1;
        }
    }

    public void printToday(ApplicationViewAccess model) {
        HashMap<String, Value> todayStats = model.getTodayStats();
        System.out.println("\n- Yesterday: --");
        todayStats.forEach((key, value) -> System.out.println(key + ": " + value));
        Value winValue = todayStats.get("win ");
        System.out.println("win%: " + getPercentageValue(winValue));
    }

    private double getPercentageValue(Value winValue) {
        return convToPercentage(winValue.getPercentage());
    }

    public void printBuys(ApplicationViewAccess model) {
        System.out.println("\n- Buys: --");
        int count = 0;
        int runningYear = 0;
        for (AssetBuy buy : model.getAllBuys()) {
            if (runningYear < buy.getDate().getYear()) {
                System.out.println();
                runningYear = buy.getDate().getYear();
            }
            String active = " ";
            if (buy.isActive())
                active = "X";
            LocalDate lastDate = model.getLastDate();
            double buyWin = getPercentageValue(model.getBuyWin(buy));
            double wknChangeToday = model.getWknChangeAtDate(buy.getWkn(), lastDate);
            double buyDayWin = convToPercentage(wknChangeToday);
            double winDay = 0;
            try {
                winDay = (wknChangeToday * buy.getAmount() * model.getWknPointAtDate(buy.getWkn(), lastDate));
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }

            String dayPositive = "+";
            if (winDay < 0)
                dayPositive = "-";
            if (winDay == 0)
                dayPositive = " ";
            Wkn wkn = model.getWkn(buy.getWkn());
            long daysRunning = Duration.between(buy.getDate().atStartOfDay(), model.getLastDate().atStartOfDay()).toDays();
            double yearsRunning = daysRunning / 365.0;
            double winEachYeahr = convToPercentage(Math.pow(1 + model.getBuyWin(buy).getPercentage(), 1 / yearsRunning) - 1);
            System.out.println(active +
                    "\t" + " [" + count + "] " +
                    "\t" + buy.getWkn() + " " +
                    "\t" + buy.getDate() +
                    "\t (" + buyWin + "%) " +
                    "\t (" + winEachYeahr + " %Y ) " +
                    "\t" + dayPositive +
                    "\t " + convWknType(wkn.getWknType()) +
                    "\t (" + buyDayWin + "% ) " +
                    "\t " + (int) winDay + "€ " +
                    "\t" + wkn.getWknName());
            count++;
        }
    }


    private String convWknType(String wknType1) {
        StringBuilder wknType = new StringBuilder(wknType1);
        while (wknType.length() < 8)
            wknType.append(" ");
        return wknType.toString();
    }


    public void printAssetSize(ApplicationViewAccess model) {
        System.out.println("\n- Asset Size: --");
        HashMap<String, Double> assetSize = model.getAssetSize();
        assetSize.forEach((wkn, value) -> System.out.println(value + " K \t" + wkn));
    }

    public void printUrls(ApplicationViewAccess model) {
        System.out.println("\n- URLs: --");
        HashSet<String> urls = new HashSet<>();
        for (Wkn wkn : model.getWkns()) {
            urls.add(wkn.getWknUrl());
        }
        try {
            for (String wkn : model.getBuyWatch().keySet()) {
                urls.add(model.getWkn(wkn).getWknUrl());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        urls.forEach(System.out::println);
    }

    public void printBuyWatch(ApplicationViewAccess model) {
        System.out.println("\n- Watch: --");
        try {
            watch(model, model.getBuyWatch());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void watch(ApplicationViewAccess model, HashMap<String, List<Double>> watchChange1) {
        HashMap<String, List<Double>> watchChange = watchChange1;
        watchChange.forEach((wkn, todays) -> {
            StringBuilder values = new StringBuilder();
            Wkn wkn1 = model.getWkn(wkn);
            double sum = 0.0;
            StringBuilder sums = new StringBuilder();
            for (Double today : todays) {
                sum += today;
                if (sum < 0)
                    sums.append("[");
                if (today < 0)
                    values.append("[");
                values.append(convToPercentage(today));
                sums.append(convToPercentage(sum));
                if (sum < 0)
                    sums.append("]");
                if (today < 0)
                    values.append("]");
                values.append("\t");
                sums.append("\t");
            }
            System.out.println(wkn1.getWknUrl() + " " + convWknType(wkn1.getWknType()));
            System.out.println(values);
            System.out.println(sums);
        });
    }

    private double convToPercentage(Double today) {
        return ((Double) (today * 10000)).intValue() / 100.0;
    }

    public void printWknTypeSum(ApplicationViewAccess model) {
        System.out.println("\n- Types --");
        HashMap<String, Value> sums = model.getWknTypeSums();
        sums.forEach((wknType, value) -> {
            if (!wknType.startsWith(">"))
                System.out.println(convWknType(wknType) + " \t" + getPercentageValue(value) + "%" + " \t" + value.getValue().intValue());
        });
        System.out.println();
        sums.forEach((wknType, value) -> {
            if (wknType.startsWith(">"))
                System.out.println(convWknType(wknType) + " \t" + getPercentageValue(value) + "%" + " \t" + value.getValue().intValue());
        });
    }

    public void printWatchAll(ApplicationViewAccess model) {
        System.out.println("\n- Watch All: --");
        try {
            watch(model, model.getWatchAll());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printBuyCash(ApplicationViewAccess model) {
        System.out.println("\n- Buy Cash --");
        System.out.println(new Double(model.getBuyCash()).intValue());
    }

    public void printChangeDate(Graphics arg0, ApplicationViewAccess model, Integer maxRange, int width) {
        HashMap<LocalDate, Value> changeDate = model.getChangeDate();
        if (changeDate != null) {
            System.out.println("\n- Change Date --");
            changeDate.forEach((localDate, value) -> {
                System.out.println(localDate);
                System.out.println(value.getValue().intValue());
                System.out.println(getPercentageValue(value));
            int col = 0;
            int size = width / maxRange;
            for (LocalDate date : model.getDates(maxRange)) {
                if (date.equals(localDate))
                    arg0.drawLine(size * col, 0, size * col, 900);
                col += 1;
            }
            });
        }
    }

    public void printConfig(ApplicationViewAccess model) {
        System.out.println("SOLD: " + AssetBuy.showSold);
    }
}
