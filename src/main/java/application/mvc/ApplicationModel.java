package application.mvc;

import application.core.StockAsset;
import application.core.StockValue;
import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;
import ui.template.Model;
import application.core.ApplicationData;
import application.service.ApplicationInput;
import application.service.ApplicationService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

public class ApplicationModel extends Model implements
        ApplicationControllerAccess, ApplicationViewAccess {
    private final ApplicationData data;
    private final ApplicationService service;

    public ApplicationModel(ApplicationInput input) {
        data = new ApplicationData();
        service = new ApplicationService(input);
    }

    // View
    @Override
    public Double[] getTotalLine(LocalDate date) {
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

    @Override
    public LocalDate getLastDate() {
        return LocalDate.parse("2020-06-26");
    }

    @Override
    public LocalDate getFirstDate() {
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

    @Override
    public Set<String> getWkns() {
        return data.getStockAssets().keySet();
    }

    @Override
    public StockValue getValue(String wkn, LocalDate date) throws DateNotFound {
        return data.getStockAssets().get(wkn).getValueAtDateWithBuy(date);
    }

    @Override
    public boolean dateWasBuy(LocalDate date) {
        for (StockAsset stockAsset : data.getStockAssets().values()) {
            if (stockAsset.hasBuyAtDate(date))
                return true;
        }
        return false;
    }

    @Override
    public Double[] getProfitLine(LocalDate date) {
        Double[] totalLine = getTotalLine(date);
        return new Double[]{totalLine[0] - getCostsAtDate(date), totalLine[1] - getCostsAtDate(date)};
    }

    @Override
    public Double getCostsAtDate(LocalDate date) {
        Double cost = 0.0;
        for (StockAsset stockAsset : data.getStockAssets().values()) {
            cost += stockAsset.getCostAtDate(date);
        }
        return cost;
    }

    // Controller
    @Override
    public void addWkn(String wkn) throws IOException {
        service.addStockRow(wkn, data);
        notifyViews();
    }

    @Override
    public void importBuys() throws IOException {
        service.importBuys(data);
    }
}
