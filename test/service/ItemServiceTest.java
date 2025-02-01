package service;

import dao.ItemDAO;
import model.Item;
import model.StockBatch;
import observer.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {
    private ItemService itemService;
    private Connection connection;

    @BeforeEach
    void setUp() {
        itemService = new ItemService();
    }

    @Test
    void testAddItem_ValidItem() {
        Item item = new Item.Builder()
                .itemId(1)
                .itemCode("CODE123")
                .itemName("Laptop")
                .quantity(10)
                .price(999.99)
                .expiryDate("2025-12-31")
                .build();

        itemService.addItem(item);
        assertNotNull(itemService.getItemByCode("CODE123"));
    }

    @Test
    void testAddItem_InvalidItem() {
        Item invalidItem = new Item.Builder()
                .itemId(2)
                .itemCode("CODE456")
                .itemName("Mouse")
                .quantity(0) // Invalid quantity
                .price(-10.0) // Invalid price
                .expiryDate("2025-12-31")
                .build();

        itemService.addItem(invalidItem);
        assertNull(itemService.getItemByCode("CODE456"));
    }

    @Test
    void testGetItemByCode_ItemExists() {
        Item item = new Item.Builder()
                .itemId(1)
                .itemCode("CODE123")
                .itemName("Laptop")
                .quantity(10)
                .price(999.99)
                .expiryDate("2025-12-31")
                .build();

        itemService.addItem(item);
        Item result = itemService.getItemByCode("CODE123");

        assertNotNull(result);
        assertEquals("Laptop", result.getItemName());
    }

    @Test
    void testGetItemByCode_ItemDoesNotExist() {
        Item result = itemService.getItemByCode("UNKNOWN");
        assertNull(result);
    }

    @Test
    void testReduceStockAfterCheckout_ItemExists() {
        Item item = new Item.Builder()
                .itemId(1)
                .itemCode("CODE123")
                .itemName("Laptop")
                .quantity(10)
                .price(999.99)
                .expiryDate("2025-12-31")
                .build();

        itemService.addItem(item);
        boolean result = itemService.reduceStockAfterCheckout("CODE123", 2, connection);
        assertTrue(result);
    }

    @Test
    void testReduceStockAfterCheckout_ItemNotExists() {
        boolean result = itemService.reduceStockAfterCheckout("UNKNOWN", 2, connection);
        assertFalse(result);
    }

    @Test
    void testGenerateBatchWiseStockReport() {
        Item item = new Item.Builder()
                .itemId(1)
                .itemCode("CODE123")
                .itemName("Laptop")
                .quantity(10)
                .price(999.99)
                .expiryDate("2025-12-31")
                .build();

        itemService.addItem(item);
        itemService.generateBatchWiseStockReport();
        assertNotNull(itemService.getItemByCode("CODE123"));
    }
}
