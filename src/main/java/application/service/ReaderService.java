package application.service;

import application.core.AssetBuy;
import application.core.WknPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReaderService {
    private final ApplicationInput input;

    public ReaderService(ApplicationInput input) {

        this.input = input;
    }

    public ArrayList<WknPoint> getStockRow(String wkn) throws IOException {
        return input.getWknPoints(wkn);
    }

    public List<AssetBuy> importBuys() throws IOException {
        return input.readBuys();
    }

    public String getWknUrl(String wkn) throws IOException {
        return input.getWknName(wkn);
    }

    public String getWknType(String wkn) {
        try {
            return input.getWknType(wkn);
        } catch (IOException e) {
            return "";
        }
    }

    public String getWknName(String wkn) throws IOException {
        return getWknUrl(wkn)
                .replace("https://www.finanzen.net", "")
                .replace("kurse", "")
                .replace("historisch", "")
                .replace("/", "")
                .replace("etf", "")
                .replace("etc", "");
    }

    public String[] getWatchWkns() {
        try {
            return input.readWatchWkns();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getWatchTypes() {
        try {
            return input.readWatchTypes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer readCash() throws IOException {
        return input.readCash();
    }
}
