package application.service;

import application.core.ApplicationData;
import application.core.StockAsset;
import application.core.StockBuy;
import application.core.StockPoint;
import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;
import helper.FilePersister;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApplicationService {
    private final ApplicationInput input;

    public ApplicationService(ApplicationInput input) {
        this.input = input;
    }

    public void addStockRow(String wkn, ApplicationData data) throws IOException {
        ArrayList<StockPoint> stockPoints = input.getStockPoints(wkn);
        data.addStockRow(wkn, stockPoints);
    }

    public void importBuys(ApplicationData data) throws IOException {
        List<StockBuy> stockBuys = input.readBuys();
        for (StockBuy stockBuy : stockBuys) {
            data.addBuy(stockBuy);
        }
    }

    public void export(ApplicationData data) {
        exportProfit(data);
        exportSum(data);
    }

    private void exportProfit(ApplicationData data) {
        String text = "";
        LocalDate last = getLastDate(data);
        LocalDate date = getFirstDate(data);
        while (!date.isAfter(last)) {
            Double[] profitLine = getProfitLine(date, data);
            Double costsAtDate = getCostsAtDate(date, data);
            Double minusCosts = new Double(getCostsAtDate(date, data) * -1);

            text += "\n" + new Double(profitLine[0] / costsAtDate).toString().replace(".", ",");
            text += ";" + new Double(profitLine[0] / getCostsAtDate(last, data)).toString().replace(".", ",");
            text += ";0";
            text += ";" + costsAtDate.toString().replace(".", ",");
            text += ";-" + minusCosts.toString().replace(".", ",");
            text += ";" + date.toString();

            date = date.plusDays(1);
        }
        new FilePersister().persistString("out", "profit.csv", text);
    }

    private void exportSum(ApplicationData data) {
        String text = "";
        LocalDate last = getLastDate(data);
        LocalDate date = getFirstDate(data);
        while (!date.isAfter(last)) {
            if (getDateWasBuy(date, data))
                text += "\n";
            text += "\n" + getTotalLine(date, data)[0].toString().replace(".", ",");
            date = date.plusDays(1);
        }
        new FilePersister().persistString("out", "sum.csv", text);
    }

    public LocalDate getFirstDate(ApplicationData data) {
        LocalDate firstBuyDate = null;
        for (String wkn : data.getStockAssets().keySet()) {
            LocalDate wknFirstBuyDate = null;
            try {
                wknFirstBuyDate = data.getStockAssets().get(wkn).getFirstBuyDate();
                if (firstBuyDate == null || wknFirstBuyDate.isBefore(firstBuyDate))
                    firstBuyDate = LocalDate.parse(wknFirstBuyDate.toString());
            } catch (NoBuys noBuys) {
            }
        }

        return LocalDate.parse(firstBuyDate.toString());
    }

    public LocalDate getLastDate(ApplicationData data) {
        return LocalDate.parse("2020-06-26");
    }

    public boolean getDateWasBuy(LocalDate date, ApplicationData data) {
        for (StockAsset stockAsset : data.getStockAssets().values()) {
            if (stockAsset.hasBuyAtDate(date))
                return true;
        }
        return false;
    }

    public Double getCostsAtDate(LocalDate date, ApplicationData data) {
        Double cost = 0.0;
        for (StockAsset stockAsset : data.getStockAssets().values()) {
            cost += stockAsset.getCostAtDate(date);
        }
        return cost;
    }

    public Double[] getProfitLine(LocalDate date, ApplicationData data) {
        Double[] totalLine = getTotalLine(date, data);
        return new Double[]{totalLine[0] - getCostsAtDate(date, data), totalLine[1] - getCostsAtDate(date, data)};
    }

    public Double[] getTotalLine(LocalDate date, ApplicationData data) {

        Double start = 0.0;
        Double end = 0.0;
        for (String wkn : data.getStockAssets().keySet()) {
            try {
                StockAsset stockAsset = data.getStockAssets().get(wkn);
                start += stockAsset.getValueAtDateWithBuy(date).getValue();
                end += stockAsset.getValueAtDateWithoutBuy(date.plusDays(1)).getValue();
            } catch (DateNotFound dateNotFound) {
            }
        }
        return new Double[]{start, end};
    }
}
