package application.service;

import application.core.StockBuy;
import application.core.WknPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReaderService {
    private ApplicationInput input;

    public ReaderService(ApplicationInput input) {

        this.input = input;
    }

    public ArrayList<WknPoint> getStockRow(String wkn) throws IOException {
        return input.getWknPoints(wkn);
    }

    public List<StockBuy> importBuys() throws IOException {
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
}
