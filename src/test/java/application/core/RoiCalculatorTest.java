package application.core;

import application.core.model.Asset;
import application.core.model.AssetBuy;
import application.core.model.WknPoint;
import application.core.model.WknkRow;
import org.junit.Before;
import org.junit.Test;
import utils.UnitTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RoiCalculatorTest extends UnitTest {
    RoiCalculator cut;

    @Before
    public void setup() {
        cut = new RoiCalculator();
    }

    @Test
    public void when_roi_is_calculated_double_each_day_then_roi_is_2_power_to_356() {
        LocalDate startDate = LocalDate.of(2001, 1, 1);
        LocalDate endDate = startDate.plusDays(1);
        WknPoint point1 = new WknPoint(startDate, 1.0);
        WknPoint point2 = new WknPoint(endDate, 2.0);
        double act = RoiCalculator.calcRoiFromTwoPoints(point1, point2);
        double expected = Math.pow(2, 365);
        assertEquals(expected, act, 0);
    }

    @Test
    public void when_weighted_roi_is_calculated_then_result_is_correct() {
        List<RoiWeight> rws = new ArrayList<>();
        rws.add(new RoiWeight(0.1, 1000));
        rws.add(new RoiWeight(0.3, 1000));
        rws.add(new RoiWeight(0.4, 2000));
        Double act = cut.calcWeightedRoi(rws);
        Double exp = 0.3;
        assertEquals(exp, act);
    }

    @Test
    public void when_calc_roi_for_sold_asset_then_result_is_correct() {
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2001, 1, 1);
        wknPoints.add(new WknPoint(startDate.plusDays(2), 0.0));
        wknPoints.add(new WknPoint(startDate.plusDays(1), 2.0));
        wknPoints.add(new WknPoint(startDate, 1.0));
        WknkRow wknkRow = new WknkRow(wknPoints);
        Asset asset = new Asset(wknkRow);
        asset.addBuy(new AssetBuy("", startDate, 1000, 0.0, 1.0, startDate.plusDays(2), 4.0));
        Double act = cut.calcRoiForAssetBuyAtDate(asset, asset.getAllBuys().get(0), startDate.plusDays(2),0, false);
        Double expected = Math.pow(2, 365);
        assertEquals(expected, act);
    }

    @Test
    public void when_calc_roi_for_not_sold_asset_then_result_is_correct() {
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2001, 1, 1);
        wknPoints.add(new WknPoint(startDate.plusDays(3), 0.0));
        wknPoints.add(new WknPoint(startDate.plusDays(2), 4.0));
        wknPoints.add(new WknPoint(startDate.plusDays(1), 2.0));
        wknPoints.add(new WknPoint(startDate, 1.0));
        WknkRow wknkRow = new WknkRow(wknPoints);
        Asset asset = new Asset(wknkRow);
        asset.addBuy(new AssetBuy("", startDate, 1000, 0.0, 1.0, null, null));
        Double act = cut.calcRoiForAssetBuyAtDate(asset, asset.getAllBuys().get(0), startDate.plusDays(2),0, false);
        Double expected = Math.pow(2, 365);
        assertEquals(expected, act);
    }

    @Test
    public void when_calc_roi_for_asset_with_two_buys_then_result_is_correct() {
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2001, 1, 1);
        wknPoints.add(new WknPoint(startDate.plusDays(4), 16.0));
        wknPoints.add(new WknPoint(startDate.plusDays(2), 0.0));
        wknPoints.add(new WknPoint(startDate.plusDays(1), 2.0));
        wknPoints.add(new WknPoint(startDate, 1.0));
        WknkRow wknkRow = new WknkRow(wknPoints);
        Asset asset = new Asset(wknkRow);
        List<Asset> assets = new ArrayList<>();
        assets.add(asset);
        asset.addBuy(new AssetBuy("", startDate, 1000, 0.0, 1.0, null, null));
        asset.addBuy(new AssetBuy("", startDate.plusDays(1), 1000, 0.0, 2.0, startDate.plusDays(2), 4.0));
        Double act = cut.calcWeightedRoiForAssetsBuyAtDate(assets, startDate.plusDays(4),0, false);
        Double expected = Math.pow(2, 365);
        assertEquals(expected, act);
    }

    @Test
    public void when_calc_roi_for_alot_buys_then_result_is_correct() {
        LocalDate startDate = LocalDate.of(2001, 1, 1);
        LocalDate endDate = startDate.plusDays(365);

        Asset asset1 = makeAsset(1000, 1.1, startDate, endDate);
        Asset asset2 = makeAsset(1000, 1.3, startDate, endDate);
        Asset asset3 = makeAsset(2000, 1.4, startDate, endDate);
        List<Asset> assets = new ArrayList<>();
        assets.add(asset1);
        assets.add(asset2);
        assets.add(asset3);
        Double act = cut.calcWeightedRoiForAssetsBuyAtDate(assets, endDate,0, false);
        Double exp = 0.3;
        assertEquals(exp, act);
    }

    @Test
    public void when_calc_roi_for_alot_buys_then_buys_in_future_are_ignored() {
        LocalDate startDate = LocalDate.of(2001, 1, 1);
        LocalDate endDate = startDate.plusDays(365);

        Asset asset1 = makeAsset(1000, 1.1, startDate, endDate);
        Asset asset2 = makeAsset(1000, 1.3, startDate, endDate);
        Asset asset3 = makeAsset(2000, 1.4, startDate, endDate);
        Asset asset4 = makeAsset(2000, 1.4, startDate.plusDays(1000), endDate.plusDays(1000));
        List<Asset> assets = new ArrayList<>();
        assets.add(asset1);
        assets.add(asset2);
        assets.add(asset3);
        assets.add(asset4);
        Double act = cut.calcWeightedRoiForAssetsBuyAtDate(assets, endDate,0, false);
        Double exp = 0.3;
        assertEquals(exp, act);
    }

    @Test
    public void when_calc_roi_for_alot_buys_then_buys_in_past_are_ignored() {
        LocalDate startDate = LocalDate.of(2001, 1, 1);
        LocalDate endDate = startDate.plusDays(365);

        Asset asset1 = makeAsset(1000, 1.1, startDate, endDate);
        Asset asset2 = makeAsset(1000, 1.3, startDate, endDate);
        Asset asset3 = makeAsset(2000, 1.4, startDate, endDate);
        asset3.addBuy(new AssetBuy("", startDate, 2000, 0.0, 1.4, startDate.plusDays(1), 1.4));
        List<Asset> assets = new ArrayList<>();
        assets.add(asset1);
        assets.add(asset2);
        assets.add(asset3);
        Double act = cut.calcWeightedRoiForAssetsBuyAtDate(assets, endDate,0, false);
        Double exp = 0.3;
        assertEquals(exp, act);
    }

    private Asset makeAsset(int amount, double endValue, LocalDate startDate, LocalDate endDate) {
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        Asset asset1;
        wknPoints.add(new WknPoint(endDate, endValue));
        wknPoints.add(new WknPoint(startDate, 1.0));
        WknkRow wknkRow = new WknkRow(wknPoints);
        asset1 = new Asset(wknkRow);
        asset1.addBuy(new AssetBuy("", startDate, amount, 0.0, 1.0, null, null));
        return asset1;
    }

}
