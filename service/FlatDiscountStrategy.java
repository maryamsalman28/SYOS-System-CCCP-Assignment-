package service;

public class FlatDiscountStrategy implements DiscountStrategy {
    private final double flatDiscount;

    public FlatDiscountStrategy(double flatDiscount) {
        this.flatDiscount = flatDiscount;
    }

    @Override
    public double calculateDiscount(double totalAmount) {
        return Math.min(flatDiscount, totalAmount); // Ensure the discount doesn't exceed the total
    }
}
