package application.service;

import application.core.StockPoint;

import java.io.IOException;
import java.util.ArrayList;

public abstract class ApplicationInput {

    public abstract ArrayList<StockPoint> getStockPoints(String wkn) throws IOException;
}
