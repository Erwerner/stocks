package application.core;

import java.time.LocalDate;

public class AssetBuy {
    private final String wkn;
    private final LocalDate date;
    private final Integer amount;
    private final Double fee;
    private final Double value;

    private boolean active;

    public AssetBuy(String wkn, LocalDate date, Integer amount, Double fee, Double value) {
        this.wkn = wkn;
        this.date = date;
        this.amount = amount;
        this.fee = fee;
        this.value = value;
        setActive(true);
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getAmount() {
        return amount;
    }

    public Double getCosts() {
        return amount * this.value * (1 + fee);
    }

    public String getWkn() {
        return wkn;
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
}