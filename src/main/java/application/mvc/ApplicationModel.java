package application.mvc;

import application.core.*;
import application.core.exception.DateNotFound;
import application.service.ApplicationInput;
import application.service.DataService;
import application.service.OutputService;
import application.service.ReaderService;
import ui.template.Model;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.*;

public class ApplicationModel extends Model implements
        ApplicationControllerAccess, ApplicationViewAccess {
    private final ApplicationData data;
    private final DataService dataService;
    private final ReaderService readerService;
    private final OutputService outputService;

    public ApplicationModel(ApplicationInput input) {
        data = new ApplicationData();
        dataService = new DataService();
        outputService = new OutputService(dataService);
        readerService = new ReaderService(input);
    }

    private void addWkn(String wkn) throws IOException {
        if (!data.getAssets().containsKey(wkn))
            data.addWkn(wkn, readerService.getWknUrl(wkn), readerService.getStockRow(wkn), readerService.getWknType(wkn), readerService.getWknName(wkn));
    }

    // View
    @Override
    public Double[] getTotalLine(LocalDate date) {
        return outputService.getTotalLine(date, data);
    }

    @Override
    public LocalDate getLastDate() {
        return dataService.calcLastDate(data);
    }

    @Override
    public ArrayList<AssetBuy> getAllBuys() {
        ArrayList<AssetBuy> assetBuys = new ArrayList<>();
        for (Asset asset : data.getAssets().values()) {
            List<AssetBuy> buys = asset.getAllBuys();
            assetBuys.addAll(buys);
        }
        assetBuys.sort(Comparator.comparing(AssetBuy::getDate));
        return assetBuys;
    }

    @Override
    public List<Double[]> getProfitLines(Integer maxRange) {
        return outputService.createProfitLines(maxRange, data);
    }

    @Override
    public List<Boolean> getBuyLines(Integer maxRange) {
        return outputService.createBuyLines(maxRange, data);
    }

    @Override
    public List<Double[]> getRelativeLines(Integer maxRange) {
        return outputService.createRelativeLines(maxRange, data);
    }

    @Override
    public Double getBuyWin(AssetBuy buy) {
        return dataService.calcBuyWin(buy, data);
    }


    @Override
    public Double getWknPointAtDate(String wkn, LocalDate date) throws DateNotFound {
        return data.getAssets().get(wkn).getWknPointAtDate(date);
    }

    @Override
    public Set<Wkn> getWkns() {
        Set<Wkn> wkns = new HashSet<>();
        data.getAssets().keySet().forEach(wkn -> wkns.add(getWkn(wkn)));
        return wkns;
    }

    @Override
    public HashMap<String, Double> getTodayStats() {
        return outputService.createTodayStats(data);
    }

    @Override
    public Wkn getWkn(String wkn) {
        return dataService.createWkn(wkn, data);
    }

    @Override
    public HashMap<String, Double> getFondValues() {
        return outputService.createFondValues(data);
    }

    @Override
    public HashMap<String, List<Double>> getWatchChange() throws IOException {
        String[] watchWkns = readerService.getWatchWkns();
        for (String watchWkn : watchWkns) {
            addWkn(watchWkn);
        }
        return outputService.createWatchChangeToday(watchWkns, data);
    }

    @Override
    public double getWknChangeAtDate(String wkn, LocalDate date) {
        return dataService.calcWknChangeToday(wkn, data, date);
    }

    // Controller
    @Override
    public void importBuys() throws IOException {
        for (AssetBuy assetBuy : readerService.importBuys()) {
            if (!data.getAssets().containsKey(assetBuy.getWkn()))
                addWkn(assetBuy.getWkn());
            data.addBuy(assetBuy);
        }
        notifyViews();
    }

    @Override
    public void togglBuy(Integer id) {
        AssetBuy buy = getAllBuys().get(id);
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
        for (Asset asset : data.getAssets().values()) {
            try {
                Value valueAtDateWithBuy = asset.getValueAtDateWithBuy(lastDate);
                Double costAtDate = asset.getCostAtDate(lastDate);
                for (AssetBuy buy : asset.getAllBuys())
                    buy.setActive(valueAtDateWithBuy.getValue() >= costAtDate);
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
        }
        notifyViews();
    }

    @Override
    public void openBrowser() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            HashSet<String> wkns = new HashSet<>();
            for (String wkn : data.getAssets().keySet()) {
                wkns.add(wkn);
            }
            for (String wkn : readerService.getWatchWkns()) {
                wkns.add(wkn);
            }

            for (String wkn : wkns) {
                try {
                    Desktop.getDesktop().browse(new URI(data.getWknUrl(wkn)));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
