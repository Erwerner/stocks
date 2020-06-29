package infrastructure.input;

import application.core.StockBuy;
import application.core.StockPoint;
import application.service.ApplicationInput;
import helper.ResourceFileReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InfrastructureInput extends ApplicationInput {
    @Override
    public ArrayList<StockPoint> getStockPoints(String wkn) throws IOException {
        ArrayList<StockPoint> stockPoints = new ArrayList<>();
        String resource = ResourceFileReader.readResource("wkn/" + wkn);
        String[] lines = resource.split("\n");
        for (String line : lines) {
            String[] columns = line.split(" \t");
            String dateString = columns[0].substring(6, 10) + "-" + columns[0].substring(3, 5) + "-" + columns[0].substring(0, 2);
            stockPoints.add(new StockPoint(LocalDate.parse(dateString), Double.valueOf(columns[2].replace(".", "").replace(",", "."))));
        }
        return stockPoints;
    }

    @Override
    public List<StockBuy> readBuys() throws IOException {
        ArrayList<StockBuy> stockBuys = new ArrayList<>();
        String file = ResourceFileReader.readResource("buys");
        String[] buys = file.split("\n");
        for (String buy : buys) {
            String[] buyData = buy.split(",");
            stockBuys.add(new StockBuy(buyData[0], LocalDate.parse(buyData[1]), Integer.parseInt(buyData[2]), Double.parseDouble(buyData[3])));
        }
        return stockBuys;
    }
}
