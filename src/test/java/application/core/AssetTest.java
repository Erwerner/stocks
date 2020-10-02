package application.core;

import application.core.model.Asset;
import application.core.model.AssetBuy;
import application.core.model.WknPoint;
import application.core.model.WknkRow;
import application.core.model.exception.DateNotFound;
import application.core.model.exception.NoBuys;
import org.junit.Test;
import utils.UnitTest;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AssetTest extends UnitTest {
    private Asset cut;

    @Test
    public void when_three_buys_are_added_then_the_first_is_returned() throws NoBuys {
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        LocalDate firstDate = LocalDate.parse("2000-12-31");
        LocalDate secondDate = LocalDate.parse("2001-12-31");
        LocalDate thirdDate = LocalDate.parse("2002-12-31");
        wknPoints.add(new WknPoint(thirdDate, 0.0));
        wknPoints.add(new WknPoint(secondDate, 0.0));
        wknPoints.add(new WknPoint(firstDate, 0.0));
        cut = new Asset(new WknkRow(wknPoints));
        cut.addBuy(new AssetBuy("", thirdDate, 1, null, 0.0, false));
        cut.addBuy(new AssetBuy("", firstDate, 1, null, 0.0, false));
        cut.addBuy(new AssetBuy("", secondDate, 1, null, 0.0, false));

        assertEquals(firstDate, cut.getFirstBuyDate());
    }

    @Test
    public void when_at_day_was_buy_then_get_day_without_buy_is_correct() throws DateNotFound {
        LocalDate buyDate = LocalDate.parse("2000-12-31");
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        wknPoints.add(new WknPoint(buyDate.plusDays(1), 1.1));
        wknPoints.add(new WknPoint(buyDate, 1.2));
        wknPoints.add(new WknPoint(buyDate.minusDays(1), 1.1));
        cut = new Asset(new WknkRow(wknPoints));
        cut.addBuy(new AssetBuy("", buyDate.minusDays(1), 1, null, 1.1, false));
        cut.addBuy(new AssetBuy("", buyDate, 1, null, 1.2, false));

        assertEquals((Double)1.2, cut.getValueAtDateWithoutBuy(buyDate).getValue());
    }
    @Test
    public void when_at_day_was_buy_then_get_day_with_buy_is_correct() throws DateNotFound {
        LocalDate buyDate = LocalDate.parse("2000-12-31");
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        wknPoints.add(new WknPoint(buyDate, 1.2));
        wknPoints.add(new WknPoint(buyDate.minusDays(1), 1.1));
        cut = new Asset(new WknkRow(wknPoints));
        cut.addBuy(new AssetBuy("", buyDate.minusDays(1), 1, null, 1.2, false));
        cut.addBuy(new AssetBuy("", buyDate, 1, null, 1.1, false));

        assertEquals((Double)2.4,cut.getValueAtDateWithBuy(buyDate).getValue());
    }
    @Test
    public void when_buys_are_added_then_all_amounts_are_correctly() throws DateNotFound {
        LocalDate date = LocalDate.parse("2000-12-31");
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        wknPoints.add(new WknPoint(date.plusDays(5), 5.0));
        wknPoints.add(new WknPoint(date.plusDays(4), 4.0));
        wknPoints.add(new WknPoint(date.plusDays(3), 3.0));
        wknPoints.add(new WknPoint(date.plusDays(2), 2.0));
        wknPoints.add(new WknPoint(date.plusDays(1), 1.0));
        wknPoints.add(new WknPoint(date, 0.0));
        cut = new Asset(new WknkRow(wknPoints));
        cut.addBuy(new AssetBuy("", date.plusDays(2), 1, null, 2.0, false));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        cut.addBuy(new AssetBuy("", date.plusDays(5), 1, null, 5.0, false));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        cut.addBuy(new AssetBuy("", date.plusDays(4), 1, null, 4.0, false));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        cut.addBuy(new AssetBuy("", date.plusDays(3), 1, null, 3.0, false));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        cut.addBuy(new AssetBuy("", date.plusDays(1), 1, null, 1.0, false));
        assertEquals((Double)4.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());

        assertEquals((Double)0.0,cut.getValueAtDateWithBuy(date.plusDays(0)).getValue());
        assertEquals((Double)1.0,cut.getValueAtDateWithBuy(date.plusDays(1)).getValue());
        assertEquals((Double)4.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        assertEquals((Double)9.0,cut.getValueAtDateWithBuy(date.plusDays(3)).getValue());
        assertEquals((Double)16.0,cut.getValueAtDateWithBuy(date.plusDays(4)).getValue());
        assertEquals((Double)25.0,cut.getValueAtDateWithBuy(date.plusDays(5)).getValue());
    }
    @Test
    public void when_stock_point_is_missing_then_last_point_is_used() throws DateNotFound {
        LocalDate date = LocalDate.parse("2000-12-31");
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        wknPoints.add(new WknPoint(date.plusDays(5), 5.0));
        wknPoints.add(new WknPoint(date.plusDays(4), 4.0));
        wknPoints.add(new WknPoint(date.plusDays(2), 2.0));
        wknPoints.add(new WknPoint(date.plusDays(1), 1.0));
        wknPoints.add(new WknPoint(date, 0.0));
        cut = new Asset(new WknkRow(wknPoints));
        cut.addBuy(new AssetBuy("", date.plusDays(2), 1, null, 2.0, false));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());

        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(3)).getValue());
    }
    @Test
    public void when_buys_are_added_then_costs_are_correct() {
        LocalDate date = LocalDate.parse("2000-12-31");
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        wknPoints.add(new WknPoint(date.plusDays(5), 5.0));
        wknPoints.add(new WknPoint(date.plusDays(4), 4.0));
        wknPoints.add(new WknPoint(date.plusDays(3), 3.0));
        wknPoints.add(new WknPoint(date.plusDays(2), 2.0));
        wknPoints.add(new WknPoint(date.plusDays(1), 1.0));
        wknPoints.add(new WknPoint(date, 0.0));
        cut = new Asset(new WknkRow(wknPoints));
        cut.addBuy(new AssetBuy("", date.plusDays(2), 2, 0.0, 2.0, false));
        cut.addBuy(new AssetBuy("", date.plusDays(4), 4, 0.0, 4.0, false));

        assertEquals((Double)4.0,cut.getCostAtDate(date.plusDays(2)));
        assertEquals((Double)20.0,cut.getCostAtDate(date.plusDays(4)));
    }
}
