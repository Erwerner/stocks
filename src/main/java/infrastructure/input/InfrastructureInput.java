package infrastructure.input;

import application.core.StockBuy;
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
        for (String line : lines) {
            if (line.equals(wknName))
                continue;
            String[] columns = line.split(" \t");
            String dateString = columns[0].substring(6, 10) + "-" + columns[0].substring(3, 5) + "-" + columns[0].substring(0, 2);
            wknPoints.add(new WknPoint(LocalDate.parse(dateString), Double.valueOf(columns[2].replace(".", "").replace(",", "."))));
        }
        return wknPoints;
    }

    @Override
    public List<StockBuy> readBuys() throws IOException {
        ArrayList<StockBuy> stockBuys = new ArrayList<>();
        String file = ResourceFileReader.readResource("buys");
        String[] buys = file.split("\n");
        for (String buy : buys) {
            String[] buyData = buy.split(",");
            System.out.println(buy);
            stockBuys.add(new StockBuy(buyData[0], LocalDate.parse(buyData[1]), Integer.parseInt(buyData[2]), Double.parseDouble(buyData[3]), Double.parseDouble(buyData[4])));
        }
        return stockBuys;
    }

    @Override
    public String getWknName(String wkn) throws IOException {
        String[] lines = getWknFileLines(wkn);
        return lines[0];
    }

    private String[] getWknFileLines(String wkn) throws IOException {
        String resource = ResourceFileReader.readResource("wkn/" + wkn);
        return resource.split("\n");
    }
}
