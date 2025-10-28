package org.eecs4413.eecs4413term_project.model;

public class Receipt {
    private final Purchases purchase;

    public Receipt(Purchases purchase) {
        this.purchase = purchase;
    }

    public Purchases getPurchase() {
        return purchase;
    }
}
