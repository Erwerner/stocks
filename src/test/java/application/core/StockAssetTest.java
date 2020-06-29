package application.core;

import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;
import org.junit.Before;
import org.junit.Test;
import utils.UnitTest;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class StockAssetTest extends UnitTest {
    StockAsset cut;

    @Test
    public void when_three_buys_are_added_then_the_first_is_returned() throws ParseException, NoBuys {
        ArrayList<StockPoint> stockPoints = new ArrayList<>();
        LocalDate firstDate = LocalDate.parse("2000-12-31");
        LocalDate secondDate = LocalDate.parse("2001-12-31");
        LocalDate thirdDate = LocalDate.parse("2002-12-31");
        stockPoints.add(new StockPoint(thirdDate, 0.0));
        stockPoints.add(new StockPoint(secondDate, 0.0));
        stockPoints.add(new StockPoint(firstDate, 0.0));
        cut = new StockAsset(new StockRow(stockPoints));
        cut.addBuy(new StockBuy(thirdDate, 1, null, null));
        cut.addBuy(new StockBuy(firstDate, 1, null, null));
        cut.addBuy(new StockBuy(secondDate, 1, null, null));

        assertEquals(firstDate, cut.getFirstBuyDate());
    }

    @Test
    public void when_at_day_was_buy_then_get_day_without_buy_is_correct() throws ParseException, DateNotFound {
        LocalDate buyDate = LocalDate.parse("2000-12-31");
        ArrayList<StockPoint> stockPoints = new ArrayList<>();
        stockPoints.add(new StockPoint(buyDate.plusDays(1), 1.1));
        stockPoints.add(new StockPoint(buyDate, 1.2));
        stockPoints.add(new StockPoint(buyDate.minusDays(1), 1.1));
        cut = new StockAsset(new StockRow(stockPoints));
        cut.addBuy(new StockBuy(buyDate.minusDays(1), 1, null, null));
        cut.addBuy(new StockBuy(buyDate, 1, null, null));

        assertEquals((Double)1.2, cut.getValueAtDateWithoutBuy(buyDate).getValue());
    }
    @Test
    public void when_at_day_was_buy_then_get_day_with_buy_is_correct() throws ParseException, DateNotFound {
        LocalDate buyDate = LocalDate.parse("2000-12-31");
        ArrayList<StockPoint> stockPoints = new ArrayList<>();
        stockPoints.add(new StockPoint(buyDate, 1.2));
        stockPoints.add(new StockPoint(buyDate.minusDays(1), 1.1));
        cut = new StockAsset(new StockRow(stockPoints));
        cut.addBuy(new StockBuy(buyDate.minusDays(1), 1, null, null));
        cut.addBuy(new StockBuy(buyDate, 1, null, null));

        assertEquals((Double)2.4,cut.getValueAtDateWithBuy(buyDate).getValue());
    }
    @Test
    public void when_buys_are_added_then_all_amounts_are_correctly() throws ParseException, DateNotFound {
        LocalDate date = LocalDate.parse("2000-12-31");
        ArrayList<StockPoint> stockPoints = new ArrayList<>();
        stockPoints.add(new StockPoint(date.plusDays(5), 5.0));
        stockPoints.add(new StockPoint(date.plusDays(4), 4.0));
        stockPoints.add(new StockPoint(date.plusDays(3), 3.0));
        stockPoints.add(new StockPoint(date.plusDays(2), 2.0));
        stockPoints.add(new StockPoint(date.plusDays(1), 1.0));
        stockPoints.add(new StockPoint(date, 0.0));
        cut = new StockAsset(new StockRow(stockPoints));
        cut.addBuy(new StockBuy(date.plusDays(2), 1, null, null));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        cut.addBuy(new StockBuy(date.plusDays(5), 1, null, null));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        cut.addBuy(new StockBuy(date.plusDays(4), 1, null, null));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        cut.addBuy(new StockBuy(date.plusDays(3), 1, null, null));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        cut.addBuy(new StockBuy(date.plusDays(1), 1, null, null));
        assertEquals((Double)4.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());

        assertEquals((Double)0.0,cut.getValueAtDateWithBuy(date.plusDays(0)).getValue());
        assertEquals((Double)1.0,cut.getValueAtDateWithBuy(date.plusDays(1)).getValue());
        assertEquals((Double)4.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        assertEquals((Double)9.0,cut.getValueAtDateWithBuy(date.plusDays(3)).getValue());
        assertEquals((Double)16.0,cut.getValueAtDateWithBuy(date.plusDays(4)).getValue());
        assertEquals((Double)25.0,cut.getValueAtDateWithBuy(date.plusDays(5)).getValue());
    }
    @Test
    public void when_stock_point_is_missing_then_last_point_is_used() throws ParseException, DateNotFound {
        LocalDate date = LocalDate.parse("2000-12-31");
        ArrayList<StockPoint> stockPoints = new ArrayList<>();
        stockPoints.add(new StockPoint(date.plusDays(5), 5.0));
        stockPoints.add(new StockPoint(date.plusDays(4), 4.0));
        stockPoints.add(new StockPoint(date.plusDays(2), 2.0));
        stockPoints.add(new StockPoint(date.plusDays(1), 1.0));
        stockPoints.add(new StockPoint(date, 0.0));
        cut = new StockAsset(new StockRow(stockPoints));
        cut.addBuy(new StockBuy(date.plusDays(2), 1, null, null));
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());

        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(2)).getValue());
        assertEquals((Double)2.0,cut.getValueAtDateWithBuy(date.plusDays(3)).getValue());
    }
}
