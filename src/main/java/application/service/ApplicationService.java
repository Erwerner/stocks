package application.service;

import application.core.ApplicationData;
import application.core.StockAsset;
import application.core.StockBuy;
import application.core.WknPoint;
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

    public ArrayList<WknPoint> getStockRow(String wkn) throws IOException {
        return input.getWknPoints(wkn);
    }

    public List<StockBuy> importBuys() throws IOException {
        return input.readBuys();
    }

    public void export(ApplicationData data) {
        exportProfit(data);
        exportSum(data);
    }

    private void exportProfit(ApplicationData data) {
        String text = "";
        LocalDate last = LocalDate.parse("2020-06-29");
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
        LocalDate last = LocalDate.parse("2020-06-29");
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
        for (String wkn : data.getAssets().keySet()) {
            LocalDate wknFirstBuyDate = null;
            try {
                wknFirstBuyDate = data.getAssets().get(wkn).getFirstBuyDate();
                if (firstBuyDate == null || wknFirstBuyDate.isBefore(firstBuyDate))
                    firstBuyDate = LocalDate.parse(wknFirstBuyDate.toString());
            } catch (NoBuys noBuys) {
            }
        }

        return LocalDate.parse(firstBuyDate.toString());
    }

    public boolean getDateWasBuy(LocalDate date, ApplicationData data) {
        for (StockAsset stockAsset : data.getAssets().values()) {
            if (stockAsset.hasBuyAtDate(date))
                return true;
        }
        return false;
    }

    public Double getCostsAtDate(LocalDate date, ApplicationData data) {
        Double cost = 0.0;
        for (StockAsset stockAsset : data.getAssets().values()) {
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

    public String getWknName(String wkn) throws IOException {
        return input.getWknName(wkn);
    }
}
