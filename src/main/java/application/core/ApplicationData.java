package application.core;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationData {

    private final HashMap<String, StockAsset> stockAssets;

    public ApplicationData() {
        stockAssets = new HashMap<>();
    }

    public void addStockRow(String wkn, ArrayList<StockPoint> stockPoints) {
        stockAssets.put(wkn, new StockAsset(new StockRow(stockPoints)));
    }

    public void addBuy(String wkn, StockBuy stockBuy) {
        stockAssets.get(wkn).addBuy(stockBuy);
    }

    public HashMap<String, StockAsset> getStockAssets() {
        return stockAssets;
    }
}
