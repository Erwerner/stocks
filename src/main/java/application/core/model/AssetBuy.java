package application.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter

@RequiredArgsConstructor
public class AssetBuy {
    private final String wkn;
    private final LocalDate date;
    private final Integer amount;
    private final Double fee;
    private final Double value;
    private final LocalDate soldDate;
    private final Double soldValue;
    private boolean active = true;
    public static boolean showSold = true;

    public Double getCosts() {
        return amount * this.value * (1 + fee);
    }

    public void toggl() {
        setActive(!active);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean show() {
        return !(soldDate==null) || showSold;
    }

    public WknPoint getBuyWknPoint() {
        return new WknPoint(date, value);
    }

    public WknPoint getSoldWknPoint() {
        if (soldDate == null)
            return null;
        return new WknPoint(soldDate, soldValue);
    }
}
