package ui.console;

import application.core.AssetBuy;
import application.core.Value;
import application.core.Wkn;
import application.core.exception.DateNotFound;
import application.mvc.ApplicationViewAccess;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.lang.StrictMath.sqrt;

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
            zero = 200;
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
        for (AssetBuy buy : model.getAllBuys()) {
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

            double totalChange = model.getTotalChangeAtDate(lastDate);
            totalChange = sqrt(totalChange * totalChange);
            double winDayPercentage = convToPercentage(winDay / totalChange);
            String dayPositive = "+";
            if (winDay < 0)
                dayPositive = "-";
            if (winDay == 0)
                dayPositive = " ";
            Wkn wkn = model.getWkn(buy.getWkn());
            System.out.println(active +
                    "\t" + " [" + count + "] " +
                    "\t" + buy.getWkn() + " " +
                    "\t" + buy.getDate() +
                    "\t (" + buyWin + "%) " +
                    "\t " + convWknType(wkn.getWknType()) +
                    "\t" + dayPositive +
                    "\t (" + buyDayWin + "% ) " +
                    "\t (" + winDayPercentage + "%) " +
                    "\t " + (int) winDay + "€ " +
                    "\t" + wkn.getWknName());
            count++;
        }
    }


    private String convWknType(String wknType1) {
        String wknType = wknType1;
        while (wknType.length() < 8)
            wknType += " ";
        return wknType;
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
            for (String wkn : model.getWatchChange().keySet()) {
                urls.add(model.getWkn(wkn).getWknUrl());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        urls.forEach(System.out::println);
    }

    public void printWatch(ApplicationViewAccess model) {
        System.out.println("\n- Watch: --");
        try {
            HashMap<String, List<Double>> watchChange = model.getWatchChange();
            watchChange.forEach((wkn, todays) -> {
                String values = "";
                Wkn wkn1 = model.getWkn(wkn);
                double sum = 0.0;
                String sums = "";
                for (Double today : todays) {
                    sum += today;
                    sums += (convToPercentage(sum) + "\t");
                    values += (convToPercentage(today) + "\t");
                }
                System.out.println(wkn1.getWknUrl() + " " + convWknType(wkn1.getWknType()));
                System.out.println(values);
                System.out.println(sums);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double convToPercentage(Double today) {
        return ((Double) (today * 10000)).intValue() / 100.0;
    }

    public void printWknTypeSum(ApplicationViewAccess model) {
        System.out.println("\n- Types --");
        HashMap<String, Value> sums = model.getWknTypeSums();
        sums.forEach((wknType, value) -> {
            if (!wknType.startsWith(">"))
                System.out.println(convWknType(wknType) + " \t" + getPercentageValue(value) + "%" + " \t" + value.getValue());
        });
        System.out.println();
        sums.forEach((wknType, value) -> {
            if (wknType.startsWith(">"))
                System.out.println(convWknType(wknType) + " \t" + getPercentageValue(value) + "%" + " \t" + value.getValue());
        });
    }

}
