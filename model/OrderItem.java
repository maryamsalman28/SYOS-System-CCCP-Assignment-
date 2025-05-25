package model;

public class OrderItem {
    private int itemId;
    private int quantity;
    private double price;

    public OrderItem() {
    }

    public OrderItem(int itemId, int quantity, double price) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
