package ui.console;

import application.core.StockBuy;
import application.core.exception.DateNotFound;
import application.mvc.ApplicationViewAccess;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class HybridViewPrinter {

    public void printBuyLines(Graphics arg0, ApplicationViewAccess model, Integer maxRange, int width) {
        java.util.List<Boolean> buys = model.getBuyLines(maxRange);
        int col = 0;
        int size = width / maxRange;
        for (Boolean buy : buys) {
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
        LocalDate lastDate = model.getLastDate();
        Double old = model.getTotalLine(lastDate.minusDays(1))[0];
        Double neu = model.getTotalLine(lastDate)[0];
        System.out.println("old:  " + old);
        System.out.println("new:  " + neu);
        System.out.println("diff: " + (neu - old));
        System.out.println("win:  " + (neu - model.getCostsAtDate(lastDate)));
        System.out.println((100 * neu / model.getCostsAtDate(lastDate) - 100) + " %");
    }

    public void printWkns(ApplicationViewAccess model) {
        int count = 0;
        for (StockBuy buy : model.getAllBuys()) {
            String active = " ";
            if (buy.isActive())
                active = "X";
            LocalDate lastDate = model.getLastDate();
            Double old = 1.0;
            Double neu = 1.0;
            try {
                old = model.getWknValueAtDate(buy.getWkn(), lastDate.minusDays(1));
                neu = model.getWknValueAtDate(buy.getWkn(), lastDate);
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
            Double buyWin = ((Double) (model.getBuyWin(buy) * 10000)).intValue() / 100.0;
            Double buyDayWin = ((Double) ((neu / old - 1) * 10000)).intValue() / 100.0;
            Double winDay = ((neu / old - 1) * buy.getAmount() * neu);

            Double oldTotal = model.getTotalLine(lastDate.minusDays(1))[0];
            Double neuTotal = model.getTotalLine(lastDate)[0];
            Double winDayPercentage = ((Double) (winDay / (neuTotal - oldTotal) * 10000)).intValue() / 100.0;

            System.out.println(active +
                    " [" + count + "] " +
                    buy.getWkn() + " " +
                    buy.getDate() +
                    " (" + buyWin + "%) " +
                    " (" + buyDayWin + "% ) " +
                    " (" + winDayPercentage + "%) " +
                     " = " +
                    winDay.intValue() + "€ " +
                    model.getWknName(buy.getWkn()));
            count++;
        }
    }
}
