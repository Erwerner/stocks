package application.core;

import application.core.exception.ApplicationFailed;
import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StockAsset {
    private final StockRow stockRow;
    private final List<Integer> stockAmount;
    private final List<StockBuy> stockBuys;

    public StockAsset(StockRow stockRow) {
        this.stockRow = stockRow;
        assertRowIsAscending(stockRow.getStockPoints());
        stockBuys = new ArrayList<>();
        stockAmount = new ArrayList<>();

        stockRow.getStockPoints().forEach(point -> {
            stockAmount.add(0);
        });
    }

    private void assertRowIsAscending(List<StockPoint> stockPoints) {
        for (int i = 0; i < stockPoints.size() - 1; i++) {
            LocalDate date = stockPoints.get(i).getDate();
            LocalDate dateNext = stockPoints.get(i + 1).getDate();
            if (!date.isAfter(dateNext)) {
                System.out.println(date);
                System.out.println(dateNext);
                assert (false);
            }
        }
    }

    private Integer getIndexOfDate(LocalDate date) throws DateNotFound {
        for (int i = 0; i < stockRow.getStockPoints().size(); i++)
            if (stockRow.getStockPoints().get(i).getDate().equals(date))
                return i;
        throw new DateNotFound(date);
    }

    public void addBuy(StockBuy stockBuy) {
        stockBuys.add(stockBuy);
        try {
            Integer buyDateIndex = getIndexOfDate(stockBuy.getDate());
            Integer buyAmount = stockBuy.getAmount();
            int i = 0;
            while (i<=buyDateIndex) {
                try {
                    Integer oldAmount = getAmountAtIndex(i);
                    Integer newAmount = buyAmount + oldAmount;
                    stockAmount.set(i, newAmount);
                } catch (Exception e) {
                    stockAmount.set(i, buyAmount);
                }
                i++;
            }
            stockBuys.sort(Comparator.comparing(StockBuy::getDate));
        } catch (DateNotFound e) {
            throw new ApplicationFailed(e);
        }
    }

    private Integer getAmountAtIndex(int i) throws Exception {
        if (i == stockAmount.size())
            throw new Exception();
        Integer amount = stockAmount.get(i);
        if (amount == null)
            amount = 0;
        return amount;
    }

    public LocalDate getFirstBuyDate() throws NoBuys {
        if(stockBuys.isEmpty())
            throw new NoBuys();
        return stockBuys.get(0).getDate();
    }

    public StockValue getValueAtDateWithoutBuy(LocalDate date) throws DateNotFound {
        LocalDate lastFoundDate = getLastFoundDateFor(date);
        Integer indexOfDate = getIndexOfDate( lastFoundDate);
        try {
            return StockValue.calc(stockRow.getPointAtDate(lastFoundDate).getValue(), getAmountAtIndex(indexOfDate + 1));
        } catch (Exception e) {
            throw new DateNotFound(date);
        }
    }

    public StockValue getValueAtDateWithBuy(LocalDate date) throws DateNotFound {
        try {
            LocalDate lastFoundDate = getLastFoundDateFor(date);
            return StockValue.calc(stockRow.getPointAtDate(lastFoundDate).getValue(), getAmountAtIndex(getIndexOfDate(lastFoundDate)));
        } catch (Exception e) {
            throw new DateNotFound(date);
        }
    }

    private LocalDate getLastFoundDateFor(LocalDate date) throws DateNotFound {
        for (StockPoint stockPoint : stockRow.getStockPoints()) {
            if(!stockPoint.getDate().isAfter(date))
                return stockPoint.getDate();
        }
        throw new DateNotFound(date);
    }
}
