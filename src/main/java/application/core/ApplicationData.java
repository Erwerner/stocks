package application.core;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationData {

    private final HashMap<String, Asset> assets;
    private final HashMap<String, String> wknNames;
    private final HashMap<String, String> wknTypes;
    private final HashMap<String, String> wknUrls;

    public ApplicationData() {
        assets = new HashMap<>();
        wknNames = new HashMap<>();
        wknTypes = new HashMap<>();
        wknUrls = new HashMap<>();
    }

    public void addWkn(String wkn, String url, ArrayList<WknPoint> wknPoints, String type, String name) {
        assets.put(wkn, new Asset(new WknkRow(wknPoints)));
        wknNames.put(wkn, name);
        wknTypes.put(wkn, type);
        wknUrls.put(wkn, url);
    }

    public void addBuy(AssetBuy assetBuy) {
        assets.get(assetBuy.getWkn()).addBuy(assetBuy);
    }

    public HashMap<String, Asset> getAssets() {
        return assets;
    }

    public String getWknType(String wkn){
        return wknTypes.get(wkn);
    }
    public String getWknName(String wkn){
        return wknNames.get(wkn);
    }

    public void togglBuy(AssetBuy buy) {
        assets.get(buy.getWkn()).togglBuy(buy);
    }

    public String getWknUrl(String wkn) {
        return wknUrls.get(wkn);
    }
}
