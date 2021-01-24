package application.core.output;

import application.core.model.Value;

import java.time.LocalDate;

public class BuyOutput {
    private final LocalDate buyDate;
    private final String buyWkn;
    private final double wknChangeToday;
    private final double winDay;
    private final Value buyWin;
    private final boolean active;
    private final String wknType;
    private final String wknName;
    private final double roiFromRange;
    private final boolean isPendingRoi;

    public BuyOutput(LocalDate buyDate, String buyWkn, double wknChangeToday, double winDay, Value buyWin, boolean active, String wknType, String wknName, double roiFromRange, boolean isPendingRoi) {
        this.buyDate = buyDate;
        this.buyWkn = buyWkn;
        this.wknChangeToday = wknChangeToday;
        this.winDay = winDay;
        this.buyWin = buyWin;
        this.active = active;
        this.wknType = wknType;
        this.wknName = wknName;
        this.roiFromRange = roiFromRange;
        this.isPendingRoi = isPendingRoi;
    }

    public LocalDate getBuyDate() {
        return buyDate;
    }

    public String getBuyWkn() {
        return buyWkn;
    }

    public double getWknChangeToday() {
        return wknChangeToday;
    }

    public double getWinDay() {
        return winDay;
    }

    public Value getBuyWin() {
        return buyWin;
    }

    public boolean isActive() {
        return active;
    }

    public String getWknType() {
        return wknType;
    }

    public String getWknName() {
        return wknName;
    }

    public double getRoiFromRange() {
        return roiFromRange;
    }

    public boolean isPendingRoi() {
        return isPendingRoi;
    }
}
