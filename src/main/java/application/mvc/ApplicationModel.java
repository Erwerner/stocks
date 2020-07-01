package application.mvc;

import application.core.ApplicationData;
import application.core.StockAsset;
import application.core.StockBuy;
import application.core.StockValue;
import application.core.exception.DateNotFound;
import application.service.ApplicationInput;
import application.service.ApplicationService;
import ui.template.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        return LocalDate.parse("2020-06-30");
    }

    @Override
    public Double getCostsAtDate(LocalDate date) {
        return service.getCostsAtDate(date, data);
    }

    @Override
    public ArrayList<StockBuy> getAllBuys() {
        ArrayList<StockBuy> stockBuys = new ArrayList<>();
        for (StockAsset stockAsset : data.getAssets().values()) {
            List<StockBuy> buys = stockAsset.getAllBuys();
            stockBuys.addAll(buys);
        }
        stockBuys.sort(Comparator.comparing(StockBuy::getDate));
        return stockBuys;
    }

    @Override
    public List<Double[]> getProfitLines(Integer maxRange) {
        List<Double[]> printLines = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            LocalDate date = getLastDate().minusDays(i);
            printLines.add(service.getProfitLine(date, data));
        }
        return printLines;
    }

    @Override
    public String getWknName(String wkn) {
        return data.getWknName(wkn);
    }

    @Override
    public List<Boolean> getBuyLines(Integer maxRange) {
        List<Boolean> buys = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            buys.add(hasBuyAtDate(getLastDate().minusDays(i)));
        }
        return buys;
    }

    @Override
    public List<Double[]> getRelativeLines(Integer maxRange) {
        List<Double[]> lines = new ArrayList<>();
        for (int i = maxRange; i >= 0; i--) {
            LocalDate date = getLastDate().minusDays(i);
            Double[] profitLine = service.getProfitLine(date, data);
            profitLine[0] = profitLine[0] / service.getCostsAtDate(date, data);
            profitLine[1] = profitLine[1] / service.getCostsAtDate(date, data);
            lines.add(profitLine);
        }
        return lines;
    }

    @Override
    public Double getBuyWin(StockBuy buy) {
        Double win = 0.0;
        try {
            String wkn = buy.getWkn();
            LocalDate lastDate = getLastDate();
            Double buyValue = data.getAssets().get(wkn).getWknPointForDate(lastDate).getValue() * buy.getAmount();
            win = (buyValue - buy.getCosts())/buy.getCosts();
        } catch (DateNotFound dateNotFound) {
            dateNotFound.printStackTrace();
        }
        return win;
    }

    @Override
    public Double getWknValueAtDate(String wkn, LocalDate date) throws DateNotFound {
        return data.getAssets().get(wkn).getWknValueAtDate(date);
    }


    private boolean hasBuyAtDate(LocalDate date) {
        for (StockAsset stockAsset : data.getAssets().values()) {
            for (StockBuy buy : stockAsset.getActiveBuys()) {
                if (buy.getDate().equals(date))
                    return true;
            }
        }
        return false;
    }

    // Controller
    @Override
    public void addWkn(String wkn) throws IOException {
        data.addWkn(wkn, service.getWknName(wkn), service.getStockRow(wkn));
        notifyViews();
    }

    @Override
    public void importBuys() throws IOException {
        for (StockBuy stockBuy : service.importBuys())
            data.addBuy(stockBuy);
        notifyViews();
    }

    @Override
    public void export() {
        service.export(data);
        notifyViews();
    }

    @Override
    public void togglBuy(Integer id) {
        StockBuy buy = getAllBuys().get(id);
        data.togglBuy(buy);
        notifyViews();
    }

    @Override
    public void togglAll() {
        getAllBuys().forEach(data::togglBuy);
        notifyViews();
    }

    @Override
    public void togglWin() {
        LocalDate lastDate = getLastDate();
        for (StockAsset stockAsset : data.getAssets().values()) {
            try {
                StockValue valueAtDateWithBuy = stockAsset.getValueAtDateWithBuy(lastDate);
                Double costAtDate = stockAsset.getCostAtDate(lastDate);
                for (StockBuy buy : stockAsset.getAllBuys())
                    buy.setActive(valueAtDateWithBuy.getValue() >= costAtDate);
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
        }
        notifyViews();
    }
}
