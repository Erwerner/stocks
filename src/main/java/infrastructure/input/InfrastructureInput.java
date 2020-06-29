package infrastructure.input;

import application.core.StockPoint;
import application.service.ApplicationInput;
import helper.ResourceFileReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class InfrastructureInput extends ApplicationInput {
    @Override
    public ArrayList<StockPoint> getStockPoints(String wkn) throws IOException {
        ArrayList<StockPoint> stockPoints = new ArrayList<>();
        String resource = ResourceFileReader.readResource(wkn);
        String[] lines = resource.split("\n");
        for (String line : lines) {
            String[] columns = line.split(" \t");
            String dateString = columns[0].substring(6,10) + "-" + columns[0].substring(3,5) + "-" + columns[0].substring(0,2);
            stockPoints.add(new StockPoint(LocalDate.parse(dateString), Double.valueOf(columns[2].replace(".","").replace(",","."))));
        }
        return stockPoints;
    }
}
