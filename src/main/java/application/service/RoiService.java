package application.service;

import application.core.RoiCalculator;
import application.core.model.ApplicationData;
import application.core.model.Asset;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoiService {
    public List<Double> getWeightedRois(ApplicationData data, LocalDate startDate, LocalDate endDate, int dayRange, int minimumDays, boolean excludeSold) {
        ArrayList<Double> rois = new ArrayList<>();
        RoiCalculator roiCalculator = new RoiCalculator();
        for (int i = 0; !startDate.plusDays(i).isAfter(endDate); i++) {
            Collection<Asset> assets = data.getAssets().values();
            Double weightedRoiForDate = roiCalculator.calcWeightedRoiForAssetsBuyAtDate(new ArrayList<>(assets), dayRange, startDate.plusDays(i), minimumDays, excludeSold);
            rois.add(weightedRoiForDate);
        }
        return rois;
    }

    public Double getTotalRoiForDateRange(ApplicationData data, int dayRange, LocalDate endDate, int minimumDays, boolean excludeSold) {
        return new RoiCalculator().calcWeightedRoiForAssetsBuyAtDate(new ArrayList<>(data.getAssets().values()), dayRange, endDate, minimumDays, excludeSold);
    }
}
