package com.example.demo.features.user.service;

public class PantryItem {

    private final int productId;
    private final String productName;
    private int availableServings;
    private final int minimumReserve;

    public PantryItem(int productId, String productName, int availableServings, int minimumReserve) {
        this.productId = productId;
        this.productName = productName;
        this.availableServings = Math.max(availableServings, 0);
        this.minimumReserve = Math.max(minimumReserve, 1);
    }

    public boolean canFulfill(int servingsRequested) {
        return availableServings >= servingsRequested;
    }

    public void reserve(int servingsRequested) {
        if (servingsRequested <= 0) {
            return;
        }
        availableServings = Math.max(availableServings - servingsRequested, 0);
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getAvailableServings() {
        return availableServings;
    }

    public int getMinimumReserve() {
        return minimumReserve;
    }
}
