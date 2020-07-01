package application.core;

import application.core.exception.ApplicationFailed;
import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StockAsset {
    private final WknkRow wknkRow;
    private final List<Integer> stockAmount;
    private final List<StockBuy> stockBuys;

    public StockAsset(WknkRow wknkRow) {
        this.wknkRow = wknkRow;
        assertRowIsAscending(wknkRow.getWknPoints());
        stockBuys = new ArrayList<>();
        stockAmount = new ArrayList<>();

        wknkRow.getWknPoints().forEach(point -> {
            stockAmount.add(0);
        });
    }

    private void assertRowIsAscending(List<WknPoint> wknPoints) {
        for (int i = 0; i < wknPoints.size() - 1; i++) {
            LocalDate date = wknPoints.get(i).getDate();
            LocalDate dateNext = wknPoints.get(i + 1).getDate();
            if (!date.isAfter(dateNext)) {
                System.out.println(date);
                System.out.println(dateNext);
                assert (false);
            }
        }
    }

    private Integer getIndexOfDate(LocalDate date) throws DateNotFound {
        for (int i = 0; i < wknkRow.getWknPoints().size(); i++)
            if (wknkRow.getWknPoints().get(i).getDate().equals(date))
                return i;
        throw new DateNotFound(date);
    }

    public void addBuy(StockBuy stockBuy) {
        System.out.println("Add: " + stockBuy.getWkn() + " " + stockBuy.getDate());
        stockBuys.add(stockBuy);
        stockBuys.sort(Comparator.comparing(StockBuy::getDate));
        refreshAmounts();
    }

    private void refreshAmounts() {
        stockAmount.clear();
        wknkRow.getWknPoints().forEach(point -> {
            stockAmount.add(0);
        });
        for (StockBuy stockBuy : getActiveBuys()) {

            try {
                Integer buyDateIndex = getIndexOfDate(stockBuy.getDate());
                Integer buyAmount = stockBuy.getAmount();
                int i = 0;
                while (i <= buyDateIndex) {
                    try {
                        Integer oldAmount = getAmountAtIndex(i);
                        Integer newAmount = buyAmount + oldAmount;
                        stockAmount.set(i, newAmount);
                    } catch (Exception e) {
                        stockAmount.set(i, buyAmount);
                    }
                    i++;
                }
            } catch (DateNotFound e) {
                throw new ApplicationFailed(e);
            }
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
        if (getActiveBuys().isEmpty())
            throw new NoBuys();
        return stockBuys.get(0).getDate();
    }

    public StockValue getValueAtDateWithoutBuy(LocalDate date) throws DateNotFound {
        LocalDate lastFoundDate = getLastFoundDateFor(date);
        Integer indexOfDate = getIndexOfDate(lastFoundDate);
        try {
            return StockValue.calc(wknkRow.getPointAtDate(lastFoundDate).getValue(), getAmountAtIndex(indexOfDate + 1));
        } catch (Exception e) {
            throw new DateNotFound(date);
        }
    }

    public StockValue getValueAtDateWithBuy(LocalDate date) throws DateNotFound {
        try {
            LocalDate lastFoundDate = getLastFoundDateFor(date);
            return StockValue.calc(wknkRow.getPointAtDate(lastFoundDate).getValue(), getAmountAtIndex(getIndexOfDate(lastFoundDate)));
        } catch (Exception e) {
            throw new DateNotFound(date);
        }
    }

    private LocalDate getLastFoundDateFor(LocalDate date) throws DateNotFound {
        for (WknPoint wknPoint : wknkRow.getWknPoints()) {
            if (!wknPoint.getDate().isAfter(date))
                return wknPoint.getDate();
        }
        throw new DateNotFound(date);
    }

    public boolean hasBuyAtDate(LocalDate date) {
        for (StockBuy stockBuy : getActiveBuys()) {
            if (stockBuy.getDate().equals(date))
                return true;
        }
        return false;
    }

    public Double getCostAtDate(LocalDate date) {
        Double costs = 0.0;
        for (StockBuy stockBuy : getActiveBuys()) {
            if (stockBuy.getDate().isAfter(date))
                continue;
            costs += stockBuy.getCosts();
        }
        return costs;
    }

    public List<StockBuy> getActiveBuys() {
        ArrayList<StockBuy> activeBuys = new ArrayList<>();
        for (StockBuy stockBuy : stockBuys) {
            if (stockBuy.isActive())
                activeBuys.add(stockBuy);
        }
        return activeBuys;
    }

    public void togglBuy(StockBuy buy) {
        for (StockBuy stockBuy : stockBuys) {
            if (stockBuy == buy)
                stockBuy.toggl();
        }
        refreshAmounts();
    }

    public List<StockBuy> getAllBuys() {
        return stockBuys;
    }

    public WknPoint getWknPointForDate(LocalDate date) throws DateNotFound {
        return wknkRow.getPointAtDate(getLastFoundDateFor(date));
    }

    public Double getWknPointAtDate(LocalDate date) throws DateNotFound {
        return wknkRow.getPointAtDate(getLastFoundDateFor(date)).getValue();
    }
}
