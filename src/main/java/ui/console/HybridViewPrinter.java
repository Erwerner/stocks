package ui.console;

import application.core.AssetBuy;
import application.core.Wkn;
import application.core.exception.DateNotFound;
import application.mvc.ApplicationViewAccess;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
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
        if (showLines == 1) {
            lines = model.getRelativeLines(maxRange);
            zero = 300;
            scale = -800.0;
        } else {
            lines = model.getProfitLines(maxRange);
            zero = 200;
            scale = -0.06;
        }
        for (Double[] printLine : lines) {
            printLine[0] *= scale;
            printLine[1] *= scale;
            printLine[0] += zero;
            printLine[1] += zero;
        }
        for (Double[] printLine : lines) {
            arg0.drawLine(size * col, printLine[0].intValue(), size + size * col, printLine[1].intValue());
            arg0.drawLine(size * col, zero, size + size * col, zero);
            col += 1;
        }
    }

    public void printToday(ApplicationViewAccess model) {
        HashMap<String, Double> todayStats = model.getTodayStats();
        System.out.println("\n- Yesterday: --");
        todayStats.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    public void printBuys(ApplicationViewAccess model) {
        System.out.println("\n- Buys: --");
        int count = 0;
        for (AssetBuy buy : model.getAllBuys()) {
            String active = " ";
            if (buy.isActive())
                active = "X";
            LocalDate lastDate = model.getLastDate();
            double buyWin = convToPercentage(model.getBuyWin(buy));
            double wknChangeToday = model.getWknChangeAtDate(buy.getWkn(), lastDate);
            double buyDayWin = convToPercentage(wknChangeToday);
            double winDay = 0;
            try {
                winDay = (wknChangeToday * buy.getAmount() * model.getWknPointAtDate(buy.getWkn(), lastDate));
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }

            Double oldTotal = model.getTotalLine(lastDate.minusDays(1))[0];
            Double neuTotal = model.getTotalLine(lastDate)[0];
            double totalChange = (neuTotal - oldTotal);
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


    public void printFonds(ApplicationViewAccess model) {
        System.out.println("\n- FONDs: --");
        HashMap<String, Double> fonds = model.getFondValues();
        fonds.forEach((wkn, value) -> System.out.println(value + " K \t" + wkn));
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
                    sums += ("\t  " + convToPercentage(sum));
                    values += ("\t  " + convToPercentage(today));
                }
                System.out.println();
                System.out.println(wkn1.getWknUrl() + " " + convWknType(wkn1.getWknType()) + values);
                System.out.println(wkn1.getWknUrl() + " " + convWknType(wkn1.getWknType()) + sums);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double convToPercentage(Double today) {
        return ((Double) (today * 10000)).intValue() / 100.0;
    }

    public void printWknTypeSum(ApplicationViewAccess model) {
        List<String> combEtc = Arrays.asList("ETC", "GOLD");
        List<String> combFond = Arrays.asList("FOND SW", "FOND DIV", "FOND");
        System.out.println("\n- Types --");
        HashMap<String, Double> sums = model.getWknTypeSums();
        double total = 0.0;
        for (Double value : sums.values()) {
            total += value;
        }
        double finalTotal = total;
        sums.forEach((wknType, value) -> {
            System.out.println(convWknType(wknType) + " \t" + convToPercentage(value / finalTotal) + "%" + " \t" + value);
        });
        printComb(combEtc, sums, finalTotal, "Comb ETC");
        printComb(combFond, sums, finalTotal, "Comb FOND");
    }

    private void printComb(List<String> combEtc, HashMap<String, Double> sums, double finalTotal, String comb_etc) {
        final double[] sum = {0.0};
        sums.forEach((wknType, value) -> {
            if (combEtc.contains(wknType))
                sum[0] += value;
        });
        System.out.println(convWknType(comb_etc) + " \t" + convToPercentage(sum[0] / finalTotal) + "%" + " \t" + sum[0]);
    }
}
