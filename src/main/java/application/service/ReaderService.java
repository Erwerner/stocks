package application.service;

import application.core.model.AssetBuy;
import application.core.model.WknPoint;
import helper.ResourceNotFound;

import java.io.IOException;
import java.util.*;

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

    private String[] getWatchTypes() {
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

    public String[] getAllWkns() throws ResourceNotFound {
        return input.getAllWkns();
    }

    public List<String> getWatchWkns()  {
        List<String> watchWkns = new ArrayList<>();
        String[] watchTypes = getWatchTypes();
        try {
            for (String wkn : getAllWkns()) {
                if (Arrays.asList(watchTypes).contains(getWknType(wkn))){
                    watchWkns.add(wkn);
                }
            }
        } catch (ResourceNotFound e) {
            throw new RuntimeException(e);
        }

        return watchWkns;
    }

    public Map<String,List<String>> getGroups() throws ResourceNotFound, IOException {
        Map<String, List<String>> groups= new HashMap<>();
        String[] wkns = input.getAllWkns();
        for (String wkn : wkns) {
            String type = input.getWknType(wkn);
            if(!groups.containsKey(type)){
                groups.put(type, new ArrayList<>());
            }
            groups.get(type).add(wkn + " " + input.getWknName(wkn));
        }
        return groups;
    }
}
