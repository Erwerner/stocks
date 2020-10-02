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

    public static double calcRoiFromTwoPoints(WknPoint point1, WknPoint point2) {
        return calcRoiFromRange(point1.getDate(), point2.getDate(), point1.calcPercentageDifferenceTo(point2));
    }

    public Double calcWeightedRoi(List<RoiWeight> rws) {
        int amountSum = 0;
        double roiSum = 0.0;
        for (RoiWeight roiWeight : rws) {
            amountSum += roiWeight.getAmount();
            roiSum += roiWeight.getRoi() * roiWeight.getAmount();
        }
        return roiSum / amountSum;
    }

    public Double calcRoiForAssetBuyAtDate(Asset asset, AssetBuy assetBuy, LocalDate date) {
        if (assetBuy.getDate().isAfter(date))
            return null;
        if (assetBuy.getDate().plusDays(70).isAfter(date))//TODO delte for tests
            return null;
        WknPoint soldWknPoint = assetBuy.getSoldWknPoint();
        WknPoint buyWknPoint = assetBuy.getBuyWknPoint();
        if (soldWknPoint == null) {
            try {
                return calcRoiFromTwoPoints(buyWknPoint, asset.getWknPointForDate(date));
            } catch (DateNotFound dateNotFound) {
                throw new RuntimeException(dateNotFound);
            }
        } else {
            if(soldWknPoint.getDate().isBefore(date))
                return null;
            return calcRoiFromTwoPoints(buyWknPoint, soldWknPoint);
        }
    }

    public Double calcWeightedRoiForAssetsBuyAtDate(List<Asset> assets, LocalDate endDate) {
        ArrayList<RoiWeight> rws = new ArrayList<>();
        for (Asset asset : assets) {
            for (AssetBuy buy : asset.getActiveBuys()) {
                Double roi = calcRoiForAssetBuyAtDate(asset, buy, endDate);
                if (roi != null) {
                    RoiWeight roiWeight = new RoiWeight(roi, buy.getAmount());
                    rws.add(roiWeight);
                }
            }
        }
        return calcWeightedRoi(rws);
    }
}
