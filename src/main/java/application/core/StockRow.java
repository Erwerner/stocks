package application.core;

import application.core.exception.DateNotFound;

import java.time.LocalDate;
import java.util.List;

public class StockRow {
    private final List<StockPoint> stockPoints;

    public StockRow(List<StockPoint> stockPoints) {
        this.stockPoints = stockPoints;
    }

    public List<StockPoint> getStockPoints() {
        return stockPoints;
    }

    public StockPoint getPointAtDate(LocalDate date) throws DateNotFound {
        for(StockPoint stockPoint:stockPoints){
            if(stockPoint.getDate().equals(date))
                return stockPoint;
        }
        throw new DateNotFound(date);
    }
}
