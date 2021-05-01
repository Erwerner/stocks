package application.mvc;

import application.core.model.*;
import application.core.output.BuyOutput;
import application.service.*;
import helper.ResourceNotFound;
import template.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ApplicationModel extends Model implements
        ApplicationControllerAccess, ApplicationViewAccess {
    private final ApplicationData data;
    private final DataService dataService;
    private final ReaderService readerService;
    private final OutputService outputService;
    private final ExecuteService executeService;
    private final AssetService assetService;
    private final RoiService roiService;
    public static final int minimumDaysForRoi = 10;

    public ApplicationModel(ApplicationInput input) {
        data = new ApplicationData();
        dataService = new DataService();
        readerService = new ReaderService(input);
        executeService = new ExecuteService();
        roiService = new RoiService();
        assetService = new AssetService();
        outputService = new OutputService(dataService, assetService);
    }

    @Override
    public List<Value[]> getLines(Integer maxRange) {
        return outputService.createLines(maxRange, data);
    }

    @Override
    public HashMap<String, Value> getTodayStats() {
        return outputService.createTodayStats(data);
    }

    @Override
    public Wkn getWkn(String wkn) {
        return assetService.createWkn(wkn, data);
    }

    @Override
    public HashMap<String, Double> getAssetSize() {
        return outputService.createAssetSize(data);
    }

    @Override
    public HashMap<String, List<Double>> getBuyWatch() {
        List<String> watchWkns = readerService.getWatchWkns();
        return outputService.createBuyWatch(watchWkns, data);
    }

    @Override
    public HashMap<String, Value> getWknTypeSums() {
        return outputService.calcWknTypeSums(data);
    }

    @Override
    public HashMap<String, Value> getWknPlaceSums() {
        return outputService.calcWknPlaceSums(data);
    }

    @Override
    public double getBuyMoney() {
        return dataService.calcBuyMoney(data);
    }

    @Override
    public HashMap<LocalDate, Value> getChangeDate() {
        LocalDate markDate = data.getMarkDate();
        if (markDate != null) {
            return outputService.createChangeDate(data, markDate);
        } else {
            return null;
        }
    }

    @Override
    public List<Double> getRoisWithSold() {
        return roiService.getWeightedRois(data, dataService.calcFirstDate(data), dataService.calcLastDate(data), 10000, minimumDaysForRoi, false);
    }

    @Override
    public Map<String, List<String>> getGroups() {
        return data.getGroups();
    }

    @Override
    public List<BuyOutput> getBuyOutputs() {
        return outputService.getBuyOutputs(data, ApplicationModel.minimumDaysForRoi);
    }

    @Override
    public HashMap<String, List<Double>> getWknWatch(String wkn) {
        return outputService.createWatchChangeToday(Collections.singletonList(wkn), data, 90);
    }

    // Controller
    @Override
    public void importBuys() throws IOException {
        for (AssetBuy assetBuy : readerService.importBuys()) {
            if (assetBuy.getDate().isAfter(dataService.calcLastDate(data))) {
                continue;
            }
            data.addBuy(assetBuy);
        }
        notifyViews();
    }

    @Override
    public void togglBuy(Integer id) {
        data.togglBuy(dataService.getAllBuys(data).get(id));
        notifyViews();
    }

    @Override
    public void togglAll() {
        dataService.getAllBuys(data).forEach(data::togglBuy);
        notifyViews();
    }

    @Override
    public void togglWin() {
        dataService.togglWin(data);
        notifyViews();
    }

    @Override
    public void openBrowser() {
        HashSet<String> wkns = new HashSet<>();
        wkns.addAll(dataService.getActiveAssets(data).keySet());
        wkns.addAll(readerService.getWatchWkns());
        executeService.browseWkns(data, wkns);
    }


    @Override
    public void importMoney() throws IOException {
        data.setCash(readerService.readCash());
        data.setBank(readerService.readBank());
        notifyViews();
    }

    @Override
    public void browseWatch() {
        HashSet<String> wkns = new HashSet<>(readerService.getWatchWkns());
        executeService.browseWkns(data, wkns);
        notifyViews();
    }

    @Override
    public void changeDate(LocalDate date) {
        data.setMarkDate(date);
        notifyViews();
    }

    @Override
    public void togglSold() {
        AssetBuy.showSold = !AssetBuy.showSold;
        data.refreshAssets();
        notifyViews();
    }

    @Override
    public void importWkns() {
        try {
            String[] allWkns = readerService.getAllWkns();
            for (String wkn : allWkns) {
                if (!data.getAssets().containsKey(wkn))
                    data.addWkn(wkn, readerService.getWknUrl(wkn), readerService.getStockRow(wkn), readerService.getWknType(wkn), readerService.getWknName(wkn));
            }
        } catch (ResourceNotFound | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void group() {
        try {
            data.setGroups(readerService.getGroups());
        } catch (ResourceNotFound | IOException e) {
            throw new RuntimeException(e);
        }
        notifyViews();
    }

    @Override
    public void refreshViews() {
        notifyViews();
    }
}
