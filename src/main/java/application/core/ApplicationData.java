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

    public void addBuy(StockBuy stockBuy) {
        stockAssets.get(stockBuy.getWkn()).addBuy(stockBuy);
    }

    public HashMap<String, StockAsset> getStockAssets() {
        return stockAssets;
    }
}
