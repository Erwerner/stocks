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
        return service.getTotalLine(date, data);
    }

    @Override
    public LocalDate getLastDate() {
        return service.getLastDate(data);
    }

    @Override
    public LocalDate getFirstDate() {
        return service.getFirstDate(data);
    }

    @Override
    public boolean getDateWasBuy(LocalDate date) {
        return service.getDateWasBuy(date, data);
    }

    @Override
    public Double[] getProfitLine(LocalDate date) {
        return service.getProfitLine(date, data);
    }

    @Override
    public Double getCostsAtDate(LocalDate date) {
        return service.getCostsAtDate(date, data);
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
        notifyViews();
    }

    @Override
    public void export() {
        service.export(data);
        notifyViews();
    }
}
