package ui.console;

import application.core.StockBuy;
import application.core.Wkn;
import application.core.exception.DateNotFound;
import application.mvc.ApplicationViewAccess;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
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
        for (StockBuy buy : model.getAllBuys()) {
            String active = " ";
            if (buy.isActive())
                active = "X";
            LocalDate lastDate = model.getLastDate();
            double buyWin = ((Double) (model.getBuyWin(buy) * 10000)).intValue() / 100.0;
            double wknChangeToday = model.getWknChangeAtDate(buy.getWkn(), lastDate);
            double buyDayWin = ((Double) (wknChangeToday * 10000)).intValue() / 100.0;
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
            double winDayPercentage = ((Double) (winDay / totalChange * 10000)).intValue() / 100.0;
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
                    "\t " + wkn.getWknType() +
                    "\t" + dayPositive +
                    "\t (" + buyDayWin + "% ) " +
                    "\t (" + winDayPercentage + "%) " +
                    "\t " + (int) winDay + "€ " +
                    "\t" + wkn.getWknName());
            count++;
        }
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
            model.getWatchChange().forEach((wkn, todays) -> {
                Wkn wkn1 = model.getWkn(wkn);
                System.out.println();
                for (Double today : todays) {
                    double outToday = ((Double) (today * 10000)).intValue() / 100.0;
                    System.out.println( wkn1.getWknName() + "\t" +  outToday);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
