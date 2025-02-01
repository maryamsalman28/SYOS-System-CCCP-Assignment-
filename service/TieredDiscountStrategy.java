package service;

public class TieredDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(double totalAmount) {
        if (totalAmount > 10000) {
            return totalAmount * 0.15; // 15% discount for totals over 10,000
        } else if (totalAmount > 5000) {
            return totalAmount * 0.10; // 10% discount for totals over 5,000
        } else if (totalAmount > 2000) {
            return totalAmount * 0.05; // 5% discount for totals over 2,000
        }
        return 0; // No discount for totals below 2,000
    }
}
