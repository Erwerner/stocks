package application.core.model;

import java.time.LocalDate;

public class AssetBuy {
    private final String wkn;
    private final LocalDate date;
    private final Integer amount;
    private final Double fee;
    private final Double value;
    private final boolean sold;

    private boolean active;
    public static boolean showSold = false;

    public AssetBuy(String wkn, LocalDate date, Integer amount, Double fee, Double value, boolean sold) {
        this.wkn = wkn;
        this.date = date;
        this.amount = amount;
        this.fee = fee;
        this.value = value;
        this.sold = sold;
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

    public boolean show(){
        return !sold || showSold;
    }
}
