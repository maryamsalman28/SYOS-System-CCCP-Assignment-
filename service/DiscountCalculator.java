package service;

public class DiscountCalculator {
    private DiscountStrategy discountStrategy;

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public double calculate(double totalAmount) {
        if (discountStrategy == null) {
            throw new IllegalStateException("No discount strategy set.");
        }
        return discountStrategy.calculateDiscount(totalAmount);
    }
}
