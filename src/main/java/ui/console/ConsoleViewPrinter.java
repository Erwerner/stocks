package ui.console;

import application.core.model.AssetBuy;
import application.core.model.Value;
import application.core.model.Wkn;
import application.core.output.BuyOutput;
import application.mvc.ApplicationModel;
import application.mvc.ApplicationViewAccess;

import java.time.LocalDate;
import java.util.*;

public class ConsoleViewPrinter {

    public void printToday(ApplicationViewAccess model) {
        HashMap<String, Value> todayStats = model.getTodayStats();
        System.out.println("\n- Yesterday: --");
        todayStats.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("Today Roi last " + ApplicationModel.minimumDaysForRoi + " wo Sold: " + convToPercentageString(model.getRoiTodayWithoutSold()));
        System.out.println("Today Roi last " + ApplicationModel.minimumDaysForRoi + " w  Sold: " + convToPercentageString(model.getRoisWithSold().get(model.getRoisWithSold().size() - 1)));
    }

    private String getPercentageValue(Value winValue) {
        return convToPercentageString(winValue.getPercentage());
    }

    public void printBuys(ApplicationViewAccess model) {

        System.out.println("\n- Buys: --");
        int runningYear = 0;
        int count = 0;
        for (BuyOutput buyOutput : model.getBuyOutputs()) {
            if (buyOutput.isPendingRoi()) {
                System.out.println("................");
            }
            if (runningYear < buyOutput.getBuyDate().getYear()) {
                System.out.println();
                runningYear = buyOutput.getBuyDate().getYear();
            }
            double winDay = buyOutput.getWinDay();
            System.out.println((buyOutput.isActive() ? "X" : " ") +
                    "\t" + " [" + count + "] " +
                    "\t" + buyOutput.getBuyWkn() + " " +
                    "\t" + buyOutput.getBuyDate() +
                    "\t (" + getPercentageValue(buyOutput.getBuyWin()) + ") " +
                    "\t (" + convToPercentageString(buyOutput.getRoiFromRange()) + ") " +
                    "\t" + (winDay < 0 ? "-" : "+") +
                    "\t " + convWknType(buyOutput.getWknType()) +
                    "\t (" + convToPercentageString(buyOutput.getWknChangeToday()) + ") " +
                    "\t " + (int) winDay + "€ " +
                    "\t" + buyOutput.getWknName());
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

    public void printBuyWatch(ApplicationViewAccess model) {
        System.out.println("\n- Watch: --");
        watch(model, model.getBuyWatch());
    }

    private void watch(ApplicationViewAccess model, HashMap<String, List<Double>> watchChange) {
        watchChange.forEach((wkn, todays) -> {
            double sum = 0.0;
            StringBuilder sums = new StringBuilder();
            for (Double today : todays) {
                sum += today;
                if (sum < 0) {
                    sums.append(" [");
                }
                sums.append(convToPercentageString(sum));
                if (sum < 0) {
                    sums.append("] ");
                }
                sums.append("\t");
            }
            Wkn wkn1 = model.getWkn(wkn);
            System.out.println(convWknType(wkn1.getWknType() + " " + wkn1.getWknUrl()));
            System.out.println(sums);
        });
    }

    private String convToPercentageString(Double value) {
        return ((Double) (value * 1000)).intValue() / 10.0 + "%";
    }

    public void printWknTypeSum(ApplicationViewAccess model) {
        System.out.println("\n- Types --");
        HashMap<String, Value> sums = model.getWknTypeSums();
        sums.forEach((wknType, value) -> {
            if (!wknType.startsWith(">"))
                System.out.println(convWknType(wknType) + " \t" + getPercentageValue(value) + " \t" + value.getValue().intValue());
        });
        System.out.println();
        sums.forEach((wknType, value) -> {
            if (wknType.startsWith(">"))
                System.out.println(convWknType(wknType) + " \t" + getPercentageValue(value) + " \t" + value.getValue().intValue());
        });
    }

    public void printWatchAll(ApplicationViewAccess model) {
        System.out.println("\n- Watch All: --");
        watch(model, model.getWatchAll());
    }

    public void printBuyCash(ApplicationViewAccess model) {
        System.out.println("\n- Buy Cash --");
        System.out.println(new Double(model.getBuyCash()).intValue());
    }

    public void printChangeDate(ApplicationViewAccess model) {
        HashMap<LocalDate, Value> changeDate = model.getChangeDate();
        if (changeDate != null) {
            System.out.println("\n- Change Date --");
            changeDate.forEach((localDate, value) -> {
                System.out.println(localDate);
                System.out.println(value.getValue().intValue());
                System.out.println(getPercentageValue(value));
            });
        }
    }

    public void printConfig() {
        System.out.println("SOLD: " + AssetBuy.showSold);
    }

    public void printGroups(ApplicationViewAccess model) {
        System.out.println("GROUPS:");
        Map<String, List<String>> groups = model.getGroups();
        groups.forEach((group, wkns) -> {
            System.out.println(group);
            wkns.forEach((wkn) -> System.out.println("\t" + wkn));
        });
    }
}
