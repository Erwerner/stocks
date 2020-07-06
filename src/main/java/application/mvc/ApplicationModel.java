package application.mvc;

import application.core.*;
import application.core.exception.DateNotFound;
import application.service.*;
import ui.template.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.*;

public class ApplicationModel extends Model implements
        ApplicationControllerAccess, ApplicationViewAccess {
    private final ApplicationData data;
    private final DataService dataService;
    private final ReaderService readerService;
    private final OutputService outputService;
    private final ExecuteService executeService;

    public ApplicationModel(ApplicationInput input) {
        data = new ApplicationData();
        dataService = new DataService();
        outputService = new OutputService(dataService);
        readerService = new ReaderService(input);
        executeService = new ExecuteService();
    }

    private void addWkn(String wkn) throws IOException {
        if (!data.getAssets().containsKey(wkn))
            data.addWkn(wkn, readerService.getWknUrl(wkn), readerService.getStockRow(wkn), readerService.getWknType(wkn), readerService.getWknName(wkn));
    }

    @Override
    public LocalDate getLastDate() {
        return dataService.calcLastDate(data);
    }

    @Override
    public ArrayList<AssetBuy> getAllBuys() {
        ArrayList<AssetBuy> assetBuys = new ArrayList<>();
        for (Asset asset : data.getAssets().values()) {
            assetBuys.addAll(asset.getAllBuys());
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
    public HashMap<String, List<Double>> getBuyWatch() throws IOException {
        String[] watchWkns = readerService.getWatchWkns();
        for (String watchWkn : watchWkns) {
            addWkn(watchWkn);
        }
        return outputService.createBuyWatch(watchWkns, data);
    }

    @Override
    public double getWknChangeAtDate(String wkn, LocalDate date) {
        return dataService.calcWknChangeToday(wkn, data, date);
    }

    @Override
    public HashMap<String, Value> getWknTypeSums() {
        return outputService.calcWknTypeSums(data);
    }

    public double getTotalChangeAtDate(LocalDate date) {
        return outputService.calcTotalChangeAtDate(date, data);
    }

    @Override
    public HashMap<String, List<Double>> getWatchAll() throws IOException {
        String[] watchWkns = data.getAssets().keySet().toArray(new String[0]);
        for (String watchWkn : watchWkns) {
            addWkn(watchWkn);
        }
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
            HashMap<LocalDate, Value> changeDate = outputService.createChangeDate(data, markDate);
            return changeDate;
        }
        return null;
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
        HashSet<String> wkns = new HashSet<>();
        wkns.addAll(data.getAssets().keySet());
        wkns.addAll(Arrays.asList(readerService.getWatchWkns()));
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
        HashSet<String> wkns = new HashSet<>();
        wkns.addAll(Arrays.asList(readerService.getWatchWkns()));
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
}
