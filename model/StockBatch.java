package model;

import java.time.LocalDate;

public class StockBatch {
    private int batchId;        
    private int itemId;           
    private int quantity;         
    private LocalDate expiryDate;  
    private LocalDate arrivalDate; 

    // Constructor
    public StockBatch(int batchId, int itemId, int quantity, LocalDate expiryDate, LocalDate arrivalDate) {
        this.batchId = batchId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.arrivalDate = arrivalDate;
    }

    // Getters and Setters
    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    // Utility to check if the batch is expired
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }
}
