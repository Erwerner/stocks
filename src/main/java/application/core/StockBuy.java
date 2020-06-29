package application.core;

import java.time.LocalDate;

public class StockBuy {
    private final String wkn;
    private final LocalDate date;
    private final Integer amount;
    private final Double costs;
    private final Double fee;

    public StockBuy(String wkn, LocalDate date, Integer amount, Double costs, Double fee) {
        this.wkn = wkn;
        this.date = date;
        this.amount = amount;
        this.costs = costs;
        this.fee = fee;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getAmount() {
        return amount;
    }

    //Todo
    public Double getCosts() {
        return costs;
    }

    //Todo
    public Double getFee() {
        return fee;
    }

    public String getWkn() {
        return wkn;
    }
}
