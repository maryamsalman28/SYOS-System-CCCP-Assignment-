package model;

public class Item {
    private int itemId; 
    private String itemCode; 
    private String itemName;
    private String batchNumber; 
    private int quantity; 
    private double price;
    private String dateOfPurchase;
    private String expiryDate; 
    private String salesType; 

    private Item(Builder builder) {
        this.itemId = builder.itemId;
        this.itemCode = builder.itemCode;
        this.itemName = builder.itemName;
        this.batchNumber = builder.batchNumber;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.dateOfPurchase = builder.dateOfPurchase;
        this.expiryDate = builder.expiryDate;
        this.salesType = builder.salesType;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    

    public double getPrice() {
        return price;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getSalesType() {
        return salesType;
    }



    public static class Builder {
        private int itemId;
        private String itemCode;
        private String itemName;
        private String batchNumber;
        private int quantity;
        private double price;
        private String dateOfPurchase;
        private String expiryDate;
        private String salesType;

        // No-argument constructor
        public Builder() {
        }

        public Builder itemId(int itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder itemCode(String itemCode) {
            this.itemCode = itemCode;
            return this;
        }

        public Builder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public Builder batchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

       

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder dateOfPurchase(String dateOfPurchase) {
            this.dateOfPurchase = dateOfPurchase;
            return this;
        }

        public Builder expiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder salesType(String salesType) {
            this.salesType = salesType;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }
}
