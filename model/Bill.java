package model;

import java.util.List;

public class Bill {
    private int billId; 
    private final String billDate;
    private final double totalAmount;
    private final double discount;
    private final double finalAmount;
    private final double cashTendered;
    private final double changeDue;
    private final List<Item> items;
    private final String salesType;

    private Bill(Builder builder) {
        this.billId = builder.billId;
        this.billDate = builder.billDate;
        this.totalAmount = builder.totalAmount;
        this.discount = builder.discount;
        this.finalAmount = builder.finalAmount;
        this.cashTendered = builder.cashTendered;
        this.changeDue = builder.changeDue;
        this.items = builder.items;
        this.salesType = builder.salesType;
    }

    // setter method for bill ID
    public void setBillId(int billId) {
        this.billId = billId;
    }

    // getters
    public int getBillId() {
        return billId;
    }

    public String getBillDate() {
        return billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public double getChangeDue() {
        return changeDue;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getSalesType() {
        return salesType;
    }

    // builder class
    public static class Builder {
        private int billId;
        private String billDate;
        private double totalAmount;
        private double discount;
        private double finalAmount;
        private double cashTendered;
        private double changeDue;
        private List<Item> items;
        private String salesType;

        public Builder billId(int billId) {
            this.billId = billId;
            return this;
        }

        public Builder billDate(String billDate) {
            this.billDate = billDate;
            return this;
        }

        public Builder totalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder discount(double discount) {
            this.discount = discount;
            return this;
        }

        public Builder finalAmount(double finalAmount) {
            this.finalAmount = finalAmount;
            return this;
        }

        public Builder cashTendered(double cashTendered) {
            this.cashTendered = cashTendered;
            return this;
        }

        public Builder changeDue(double changeDue) {
            this.changeDue = changeDue;
            return this;
        }

        public Builder items(List<Item> items) {
            this.items = items;
            return this;
        }

        public Builder salesType(String salesType) {
            this.salesType = salesType;
            return this;
        }

        public Bill build() {
            return new Bill(this);
        }
    }
}
