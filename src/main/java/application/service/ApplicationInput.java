package application.service;

import application.core.AssetBuy;
import application.core.WknPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ApplicationInput {

    public abstract ArrayList<WknPoint> getWknPoints(String wkn) throws IOException;

    public abstract List<AssetBuy> readBuys() throws IOException;

    public abstract String getWknName(String wkn) throws IOException;

    public abstract String getWknType(String wkn) throws IOException;

    public abstract String[] readWatchWkns() throws IOException;
}
