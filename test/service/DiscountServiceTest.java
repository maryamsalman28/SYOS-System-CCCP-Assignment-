package service;

import dao.DiscountDAO;
import model.Discount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscountServiceTest {
    private DiscountService discountService;
    private DiscountDAO discountDAO;

    @BeforeEach
    void setUp() {
        discountDAO = new DiscountDAO(); // If mocking is needed, change this to a mock instance
        discountService = new DiscountService();
    }

    @Test
    void testAddDiscount() {
        Discount discount = new Discount();
        discount.setDiscountId(1);
        discount.setDescription("Summer Sale");
        discount.setDiscountPercentage(10.0);
        discount.setStartDate(LocalDate.of(2025, 6, 1));
        discount.setEndDate(LocalDate.of(2025, 6, 30));

        discountService.addDiscount(discount);

        List<Discount> discounts = discountService.getAllDiscounts();
        assertTrue(discounts.contains(discount));
    }

    @Test
    void testGetAllDiscounts() {
        Discount discount1 = new Discount();
        discount1.setDiscountId(1);
        discount1.setDescription("Holiday Sale");
        discount1.setDiscountPercentage(15.0);
        discount1.setStartDate(LocalDate.of(2025, 12, 1));
        discount1.setEndDate(LocalDate.of(2025, 12, 31));

        Discount discount2 = new Discount();
        discount2.setDiscountId(2);
        discount2.setDescription("Black Friday");
        discount2.setDiscountPercentage(20.0);
        discount2.setStartDate(LocalDate.of(2025, 11, 25));
        discount2.setEndDate(LocalDate.of(2025, 11, 27));

        discountService.addDiscount(discount1);
        discountService.addDiscount(discount2);

        List<Discount> result = discountService.getAllDiscounts();

        assertEquals(2, result.size());
    }

    @Test
    void testGetActiveDiscounts_NoActiveDiscounts() {
        Discount pastDiscount = new Discount();
        pastDiscount.setDiscountId(1);
        pastDiscount.setDescription("Expired");
        pastDiscount.setDiscountPercentage(10.0);
        pastDiscount.setStartDate(LocalDate.of(2023, 1, 1));
        pastDiscount.setEndDate(LocalDate.of(2023, 1, 30));

        discountService.addDiscount(pastDiscount);

        List<Discount> activeDiscounts = discountService.getActiveDiscounts();

        assertTrue(activeDiscounts.isEmpty());
    }

    @Test
    void testGetActiveDiscounts_ValidActiveDiscount() {
        Discount activeDiscount = new Discount();
        activeDiscount.setDiscountId(1);
        activeDiscount.setDescription("Summer Discount");
        activeDiscount.setDiscountPercentage(15.0);
        activeDiscount.setStartDate(LocalDate.now().minusDays(5));
        activeDiscount.setEndDate(LocalDate.now().plusDays(10));

        Discount expiredDiscount = new Discount();
        expiredDiscount.setDiscountId(2);
        expiredDiscount.setDescription("Old Discount");
        expiredDiscount.setDiscountPercentage(20.0);
        expiredDiscount.setStartDate(LocalDate.of(2024, 1, 1));
        expiredDiscount.setEndDate(LocalDate.of(2024, 1, 15));

        discountService.addDiscount(activeDiscount);
        discountService.addDiscount(expiredDiscount);

        List<Discount> activeDiscounts = discountService.getActiveDiscounts();

        assertEquals(1, activeDiscounts.size());
        assertEquals("Summer Discount", activeDiscounts.get(0).getDescription());
    }

    @Test
    void testGetDiscountStrategy_NoActiveDiscounts() {
        DiscountStrategy strategy = discountService.getDiscountStrategy(2000);
        assertNull(strategy);
    }

    @Test
    void testGetDiscountStrategy_PercentageDiscount() {
        Discount percentageDiscount = new Discount();
        percentageDiscount.setDiscountId(1);
        percentageDiscount.setDescription("Festival Offer");
        percentageDiscount.setDiscountPercentage(10.0);
        percentageDiscount.setStartDate(LocalDate.now().minusDays(1));
        percentageDiscount.setEndDate(LocalDate.now().plusDays(1));

        discountService.addDiscount(percentageDiscount);

        DiscountStrategy strategy = discountService.getDiscountStrategy(2000);

        assertNotNull(strategy);
        assertTrue(strategy instanceof PercentageDiscountStrategy);
    }

    @Test
    void testGetDiscountStrategy_TieredDiscount() {
        Discount noPercentageDiscount = new Discount();
        noPercentageDiscount.setDiscountId(1);
        noPercentageDiscount.setDescription("Special Offer");
        noPercentageDiscount.setDiscountPercentage(0.0);
        noPercentageDiscount.setStartDate(LocalDate.now().minusDays(1));
        noPercentageDiscount.setEndDate(LocalDate.now().plusDays(1));

        discountService.addDiscount(noPercentageDiscount);

        DiscountStrategy strategy = discountService.getDiscountStrategy(6000);

        assertNotNull(strategy);
        assertTrue(strategy instanceof TieredDiscountStrategy);
    }

    @Test
    void testGetDiscountStrategy_FlatDiscount() {
        Discount noPercentageDiscount = new Discount();
        noPercentageDiscount.setDiscountId(1);
        noPercentageDiscount.setDescription("Basic Offer");
        noPercentageDiscount.setDiscountPercentage(0.0);
        noPercentageDiscount.setStartDate(LocalDate.now().minusDays(1));
        noPercentageDiscount.setEndDate(LocalDate.now().plusDays(1));

        discountService.addDiscount(noPercentageDiscount);

        DiscountStrategy strategy = discountService.getDiscountStrategy(4000);

        assertNotNull(strategy);
        assertTrue(strategy instanceof FlatDiscountStrategy);
    }
}
