package application.service;

import application.core.model.AssetBuy;
import application.core.model.WknPoint;
import helper.ResourceNotFound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ApplicationInput {

    public abstract ArrayList<WknPoint> getWknPoints(String wkn) throws IOException;

    public abstract List<AssetBuy> readBuys() throws IOException;

    public abstract String getWknName(String wkn) throws IOException;

    public abstract String getWknType(String wkn) throws IOException;

    public abstract String[] readWatchTypes() throws IOException;

    public abstract Integer readCash() throws IOException;

    public abstract Integer readBank() throws IOException;

    public abstract String[] getAllWkns() throws ResourceNotFound;
}
