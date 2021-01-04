package application.core.model;

import application.core.model.exception.ApplicationFailed;
import application.core.model.exception.DateNotFound;
import application.core.model.exception.NoBuys;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Asset {
    private final String wkn;
    private final WknkRow wknkRow;
    private final List<Integer> stockAmount;
    private final List<AssetBuy> assetBuys;

    public Asset(String wkn, WknkRow wknkRow) {
        this.wkn = wkn;
        this.wknkRow = wknkRow;
        assertRowIsAscending(wknkRow.getWknPoints());
        assetBuys = new ArrayList<>();
        stockAmount = new ArrayList<>();

        wknkRow.getWknPoints().forEach(point -> stockAmount.add(0));
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

    public void addBuy(AssetBuy assetBuy) {
        System.out.println("Add: " + assetBuy.getWkn() + " " + assetBuy.getDate());
        assetBuys.add(assetBuy);
        assetBuys.sort(Comparator.comparing(AssetBuy::getDate));
        refreshAmounts();
    }

    public void refreshAmounts() {
        stockAmount.clear();
        wknkRow.getWknPoints().forEach(point -> stockAmount.add(0));
        for (AssetBuy assetBuy : getActiveBuys()) {
            try {
                Integer buyDateIndex = getIndexOfDate(assetBuy.getDate());
                Integer buyAmount = assetBuy.getAmount();
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
        return getShowBuys().get(0).getDate();
    }

    public Value getValueAtDateWithoutBuy(LocalDate date) throws DateNotFound {
        LocalDate lastFoundDate = getLastFoundDateFor(date);
        Integer indexOfDate = getIndexOfDate(lastFoundDate);
        try {
            return Value.calcFromAmount(wknkRow.getPointAtDate(lastFoundDate).getValue(), getAmountAtIndex(indexOfDate + 1));
        } catch (Exception e) {
            throw new DateNotFound(date);
        }
    }

    public Value getValueAtDateWithBuy(LocalDate date) throws DateNotFound {
        try {
            LocalDate lastFoundDate = getLastFoundDateFor(date);
            return Value.calcFromAmount(wknkRow.getPointAtDate(lastFoundDate).getValue(), getAmountAtIndex(getIndexOfDate(lastFoundDate)));
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

    public Double getCostAtDate(LocalDate date) {
        Double costs = 0.0;
        for (AssetBuy assetBuy : getActiveBuys()) {
            if (assetBuy.getDate().isAfter(date))
                continue;
            costs += assetBuy.getCosts();
        }
        return costs;
    }

    public List<AssetBuy> getActiveBuys() {
        ArrayList<AssetBuy> activeBuys = new ArrayList<>();
        for (AssetBuy assetBuy : getShowBuys()) {
            if (assetBuy.isActive())
                activeBuys.add(assetBuy);
        }
        return activeBuys;
    }

    public void togglBuy(AssetBuy buy) {
        for (AssetBuy assetBuy : getShowBuys()) {
            if (assetBuy == buy)
                assetBuy.toggl();
        }
        refreshAmounts();
    }

    public List<AssetBuy> getShowBuys() {
        ArrayList<AssetBuy> showBuys = new ArrayList<>();
        for (AssetBuy assetBuy : assetBuys) {
            if (assetBuy.show())
                showBuys.add(assetBuy);
        }
        return showBuys;
    }

    public WknPoint getWknPointForDate(LocalDate date) throws DateNotFound {
        return wknkRow.getPointAtDate(getLastFoundDateFor(date));
    }

    public Double getWknPointAtDate(LocalDate date) throws DateNotFound {
        return wknkRow.getPointAtDate(getLastFoundDateFor(date)).getValue();
    }

    public LocalDate getLastDate() {
        return wknkRow.getWknPoints().get(0).getDate();
    }

    public List<AssetBuy> getAllBuys() {
        return assetBuys;
    }

    public String getWkn() {
        return wkn;
    }
}
