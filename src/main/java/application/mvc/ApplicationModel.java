package application.mvc;

import application.core.model.*;
import application.core.model.exception.DateNotFound;
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
    private final RoiService roiService;

    public ApplicationModel(ApplicationInput input) {
        data = new ApplicationData();
        dataService = new DataService();
        outputService = new OutputService(dataService);
        readerService = new ReaderService(input);
        executeService = new ExecuteService();
        roiService = new RoiService();
    }

    @Override
    public LocalDate getLastDate() {
        return dataService.calcLastDate(data);
    }

    @Override
    public ArrayList<AssetBuy> getAllBuys() {
        ArrayList<AssetBuy> assetBuys = new ArrayList<>();
        for (Asset asset : data.getAssets().values()) {
            assetBuys.addAll(asset.getShowBuys());
        }
        assetBuys.sort(Comparator.comparing(AssetBuy::getDate));
        return assetBuys;
    }

    @Override
    public List<Boolean> getBuyLines(Integer maxRange) {
        return outputService.createBuyLines(maxRange, data);
    }

    @Override
    public List<Value[]> getRelativeLines(Integer maxRange) {
        return outputService.createLines(maxRange, data);
    }

    @Override
    public Value getBuyWin(AssetBuy buy) {
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
    public HashMap<String, Value> getTodayStats() {
        return outputService.createTodayStats(data);
    }

    @Override
    public Wkn getWkn(String wkn) {
        return dataService.createWkn(wkn, data);
    }

    @Override
    public HashMap<String, Double> getAssetSize() {
        return outputService.createAssetSize(data);
    }

    @Override
    public HashMap<String, List<Double>> getBuyWatch() {
        List<String> watchWkns = getWatchWkns();
        return outputService.createBuyWatch(watchWkns, data);
    }

    private List<String> getWatchWkns() {
        List<String> watchWkns = new ArrayList<>();
        String[] types = readerService.getWatchTypes();
        for (String type : types)
            for (String wkn : data.getAssets().keySet())
                if (data.getWknType(wkn).equals(type)) {
                    //addWkn(wkn);
                    watchWkns.add(wkn);
                }
        return watchWkns;
    }

    @Override
    public double getWknChangeAtDate(String wkn, LocalDate date) {
        return dataService.calcWknChangeToday(wkn, data, date);
    }

    @Override
    public HashMap<String, Value> getWknTypeSums() {
        return outputService.calcWknTypeSums(data);
    }

    @Override
    public HashMap<String, List<Double>> getWatchAll() {
        Collection<String> watchWkns = data.getAssets().keySet();
        return outputService.createWatchChangeToday(watchWkns, data);
    }

    @Override
    public double getBuyCash() {
        return dataService.calcBuyCash(data);
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
    public List<LocalDate> getDates(Integer maxRange) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate localDate = dataService.calcLastDate(data);
        for (int i = maxRange; i >= 0; i--) {
            dates.add(localDate.minusDays(i));
        }
        return dates;
    }

    @Override
    public List<Double> getRoisWithSold() {
        return roiService.getWeightedRois(data, dataService.calcFirstDate(data), dataService.calcLastDate(data),10000, 89, false);
    }

    @Override
    public Double getRoiTodayWithoutSold() {
        return roiService.getTotalRoiForDateRange(data, 100000, dataService.calcLastDate(data), 35, true);
    }

    // Controller
    @Override
    public void importBuys() throws IOException {
        for (AssetBuy assetBuy : readerService.importBuys()) {
            if (assetBuy.getDate().isAfter(dataService.calcLastDate(data)))
                continue;
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
                for (AssetBuy buy : asset.getShowBuys())
                    buy.setActive(valueAtDateWithBuy.getValue() >= costAtDate);
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
        }
        data.refreshAssets();
        notifyViews();
    }

    @Override
    public void openBrowser() {
        HashSet<String> wkns = new HashSet<>();
        wkns.addAll(data.getAssets().keySet());
        wkns.addAll(getWatchWkns());
        executeService.browseWkns(data, wkns);
    }


    @Override
    public void setCash(double cash) {
        data.setCash(cash);
        notifyViews();
    }

    @Override
    public void importCash() throws IOException {
        Integer cash = readerService.readCash();
        data.setCash(cash);
        notifyViews();
    }

    @Override
    public void browseWatch() {
        HashSet<String> wkns = new HashSet<>(getWatchWkns());
        executeService.browseWkns(data, wkns);
        notifyViews();
    }

    @Override
    public void addCash(Double cash) {
        data.setCash(data.getCash() + cash);
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
        } catch (ResourceNotFound | IOException resourceNotFound) {
            resourceNotFound.printStackTrace();
        }
    }
}
