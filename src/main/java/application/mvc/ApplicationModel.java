package application.mvc;

import application.core.StockAsset;
import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;
import ui.template.Model;
import application.core.ApplicationData;
import application.service.ApplicationInput;
import application.service.ApplicationService;

import java.io.IOException;
import java.time.LocalDate;

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
    public Double[] getLine(LocalDate date) {
        Double start = 0.0;
        Double end = 0.0;
        for (String wkn : data.getStockAssets().keySet()) {
            try {
                StockAsset stockAsset = data.getStockAssets().get(wkn);
                start += stockAsset.getValueAtDateWithBuy(date).getValue();
                end += stockAsset.getValueAtDateWithoutBuy(date.plusDays(1)).getValue();
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
        }
        return new Double[]{start, end};
    }

    @Override
    public LocalDate getLastDate() {
        return LocalDate.parse("2020-06-26");
    }

    @Override
    public LocalDate getFirstDate() throws NoBuys {
        LocalDate firstBuyDate = null;
        for (String wkn : data.getStockAssets().keySet()) {
            LocalDate wknFirstBuyDate = data.getStockAssets().get(wkn).getFirstBuyDate();
            if (firstBuyDate == null || wknFirstBuyDate.isBefore(firstBuyDate))
                firstBuyDate = LocalDate.parse(wknFirstBuyDate.toString());
        }

        return firstBuyDate;
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
