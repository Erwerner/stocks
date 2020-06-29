package application.service;

import application.core.ApplicationData;
import application.core.StockBuy;
import application.core.StockPoint;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

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
        data.addBuy(wkn, new StockBuy(date, amount,null,null));
    }
}
