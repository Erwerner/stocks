package application.service;

import application.core.ApplicationData;
import application.core.StockBuy;
import application.core.StockPoint;

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

    public void addBuy(String wkn, ApplicationData data, LocalDate date, Integer amount) {
        data.addBuy(new StockBuy(wkn, date, amount, null, null));
    }

    public void importBuys(ApplicationData data) throws IOException {
        List<StockBuy> stockBuys = input.readBuys();
        for (StockBuy stockBuy : stockBuys) {
            data.addBuy(stockBuy);
        }
    }
}
