package infrastructure.input;

import application.core.AssetBuy;
import application.core.WknPoint;
import application.service.ApplicationInput;
import helper.ResourceFileReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InfrastructureInput extends ApplicationInput {
    @Override
    public ArrayList<WknPoint> getWknPoints(String wkn) throws IOException {
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        String[] lines = getWknFileLines(wkn);
        String wknName = lines[0];
        String wknType = lines[1];
        for (String line : lines) {
            if (line.equals(wknName))
                continue;
            if (line.equals(wknType))
                continue;
            String[] columns = line.split(" \t");
            String dateString = columns[0].substring(6, 10) + "-" + columns[0].substring(3, 5) + "-" + columns[0].substring(0, 2);
            wknPoints.add(new WknPoint(LocalDate.parse(dateString), Double.valueOf(columns[2].replace(".", "").replace(",", "."))));
        }
        return wknPoints;
    }

    @Override
    public List<AssetBuy> readBuys() throws IOException {
        ArrayList<AssetBuy> assetBuys = new ArrayList<>();
        String file = ResourceFileReader.readResource("buys");
        String[] buys = file.split("\n");
        for (String buy : buys) {
            String[] buyData = buy.split(",");
            System.out.println(buy);
            assetBuys.add(new AssetBuy(buyData[0], LocalDate.parse(buyData[1]), Integer.parseInt(buyData[2]), Double.parseDouble(buyData[3]), Double.parseDouble(buyData[4])));
        }
        return assetBuys;
    }

    @Override
    public String getWknName(String wkn) throws IOException {
        String[] lines = getWknFileLines(wkn);
        return lines[0];
    }
    @Override
    public String getWknType(String wkn) throws IOException {
        String[] lines = getWknFileLines(wkn);
        return lines[1];
    }

    @Override
    public String[] readWatchWkns() throws IOException {
        String file = ResourceFileReader.readResource("watch");
        return file.split("\n");
    }

    @Override
    public Integer readCash() throws IOException {
        String cash = ResourceFileReader.readResource("cash");
        String[] split = cash.split("\n");
        return Integer.parseInt(split[0]);
    }

    private String[] getWknFileLines(String wkn) throws IOException {
        String resource = ResourceFileReader.readResource("wkn/" + wkn);
        return resource.split("\n");
    }
}
