package application.core;

import application.core.model.Asset;
import application.core.model.AssetBuy;
import application.core.model.WknPoint;
import application.core.model.exception.DateNotFound;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoiCalculator {
    public static double calcRoiFromRange(LocalDate startDate, LocalDate endDate, Double percentageDifference) {
        long daysOfRange = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
        double years = daysOfRange / 365.0;
        return Math.pow(1 + percentageDifference, 1 / years) - 1;
    }

    double calcRoiFromTwoPoints(WknPoint point1, WknPoint point2) {
        return calcRoiFromRange(point1.getDate(), point2.getDate(), point1.calcPercentageDifferenceTo(point2));
    }

    Double calcWeightedRoi(List<RoiWeight> rws) {
        int costSum = 0;
        double roiSum = 0.0;
        for (RoiWeight roiWeight : rws) {
            costSum += roiWeight.getCost();
            roiSum += roiWeight.getRoi() * roiWeight.getCost();
        }
        return roiSum / costSum;
    }

    public Double calcRoiForAssetBuyAtDate(Asset asset, AssetBuy assetBuy, int dayRange, LocalDate endDate, int minimumDays, boolean excludeSold) {
        if (assetBuy.getDate().isAfter(endDate))
            return null;
        if (assetBuy.getDate().plusDays(minimumDays).isAfter(endDate))
            return null;
        WknPoint startWknPoint;
        LocalDate startDate = endDate.minusDays(dayRange-1);
        if (startDate.isAfter(assetBuy.getDate())) {
            try {
                startWknPoint = asset.getWknPointForDate(startDate);
            } catch (DateNotFound e) {
                throw new RuntimeException(e);
            }
        } else {
            startWknPoint = assetBuy.getBuyWknPoint();
        }
        WknPoint soldWknPoint = assetBuy.getSoldWknPoint();
        if (soldWknPoint == null) {
            try {
                return calcRoiFromTwoPoints(startWknPoint, asset.getWknPointForDate(endDate));
            } catch (DateNotFound dateNotFound) {
                throw new RuntimeException(dateNotFound);
            }
        } else {
            if (soldWknPoint.getDate().isBefore(endDate) && excludeSold)
                return null;
            return calcRoiFromTwoPoints(startWknPoint, soldWknPoint);
        }
    }

    public Double calcWeightedRoiForAssetsBuyAtDate(List<Asset> assets, int dayRange, LocalDate endDate, int minimumDays, boolean excludeSold) {
        ArrayList<RoiWeight> rws = new ArrayList<>();
        for (Asset asset : assets) {
            for (AssetBuy buy : asset.getActiveBuys()) {
                Double roi = calcRoiForAssetBuyAtDate(asset, buy, dayRange, endDate, minimumDays, excludeSold);
                if (roi != null) {
                    RoiWeight roiWeight = new RoiWeight(roi, buy.getCosts());
                    rws.add(roiWeight);
                }
            }
        }
        return calcWeightedRoi(rws);
    }
}
