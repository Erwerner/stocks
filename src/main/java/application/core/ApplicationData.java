package application.core;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationData {

    private final HashMap<String, StockAsset> assets;
    private final HashMap<String, String> wknNames;

    public ApplicationData() {
        assets = new HashMap<>();
        wknNames = new HashMap<>();
    }

    public void addWkn(String wkn, String name, ArrayList<WknPoint> wknPoints) {
        assets.put(wkn, new StockAsset(new WknkRow(wknPoints)));
        wknNames.put(wkn, name);
    }

    public void addBuy(StockBuy stockBuy) {
        assets.get(stockBuy.getWkn()).addBuy(stockBuy);
    }

    public HashMap<String, StockAsset> getAssets() {
        return assets;
    }

    public String getWknName(String wkn){
        return wknNames.get(wkn);
    }

    public void togglBuy(StockBuy buy) {
        assets.get(buy.getWkn()).togglBuy(buy);
    }
}
