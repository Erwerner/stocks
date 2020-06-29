package application.service;

import application.core.StockBuy;
import application.core.StockPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ApplicationInput {

    public abstract ArrayList<StockPoint> getStockPoints(String wkn) throws IOException;

    public abstract List<StockBuy> readBuys() throws IOException;
}
