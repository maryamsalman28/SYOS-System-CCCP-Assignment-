package service;

import dao.DiscountDAO;
import java.time.LocalDate;
import java.util.List;
import model.Discount;

public class DiscountService {
    private DiscountDAO discountDAO = new DiscountDAO();

    // Add a new discount
    public void addDiscount(Discount discount) {
        discountDAO.addDiscount(discount);
    }

    // Retrieve all discounts
    public List<Discount> getAllDiscounts() {
        return discountDAO.getAllDiscounts();
    }

    // Get active discounts based on the current date
    public List<Discount> getActiveDiscounts() {
        List<Discount> allDiscounts = discountDAO.getAllDiscounts();
        LocalDate currentDate = LocalDate.now();
        return allDiscounts.stream()
                .filter(discount -> !currentDate.isBefore(discount.getStartDate()) && !currentDate.isAfter(discount.getEndDate()))
                .toList();
    }

    //  select a discount strategy
    public DiscountStrategy getDiscountStrategy(double totalAmount) {
        List<Discount> activeDiscounts = getActiveDiscounts();

        if (activeDiscounts.isEmpty()) {
            System.out.println("No active discounts available.");
            return null; // No active discounts
        }

        // select the first active discount (or customize logic)
        Discount selectedDiscount = activeDiscounts.get(0);

        System.out.println("Applying discount: " + selectedDiscount.getDescription());
        
        // appropriate strategy based on the discount type
        if (selectedDiscount.getDiscountPercentage() > 0) {
            return new PercentageDiscountStrategy(selectedDiscount.getDiscountPercentage());
        } else if (totalAmount > 5000) { // Example tiered discount logic
            return new TieredDiscountStrategy();
        } else {
            return new FlatDiscountStrategy(100); // Apply a flat discount if nothing else matches
        }
    }
}
