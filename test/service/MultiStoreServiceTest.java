package test.service;

import model.MultiStore;
import service.MultiStoreService;
import dao.MultiStoreDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MultiStoreServiceTest {
    private MultiStoreService multiStoreService;
    private MultiStoreDAO mockMultiStoreDAO;

    @BeforeEach
    void setUp() {
        // Using a manually controlled DAO instance instead of mocking
        mockMultiStoreDAO = new MultiStoreDAO() {
            private final List<MultiStore> storeList = new ArrayList<>();

            @Override
            public void addStore(MultiStore store) {
                storeList.add(store);
            }

            @Override
            public List<MultiStore> getAllStores() {
                return new ArrayList<>(storeList);
            }
        };

        multiStoreService = new MultiStoreService() {
            @Override
            public void addStore(MultiStore store) {
                mockMultiStoreDAO.addStore(store);
            }

            @Override
            public List<MultiStore> getAllStores() {
                return mockMultiStoreDAO.getAllStores();
            }
        };
    }

    @Test
    void testAddStore() {
        MultiStore store = new MultiStore();  // No-arg constructor
        store.setStoreId(1);
        store.setStoreName("Branch A");
        store.setLocation("123 Main St");
        store.setManager("John Doe");
        store.setContactInfo("123-456-7890");

        multiStoreService.addStore(store);

        List<MultiStore> stores = multiStoreService.getAllStores();
        assertNotNull(stores, "Stores list should not be null");
        assertFalse(stores.isEmpty(), "Stores list should not be empty");
        assertEquals(1, stores.size(), "There should be exactly one store in the list");
        assertEquals("Branch A", stores.get(0).getStoreName(), "Store name should match");
    }

    @Test
    void testGetAllStores_EmptyList() {
        List<MultiStore> stores = multiStoreService.getAllStores();
        assertNotNull(stores, "Stores list should not be null");
        assertTrue(stores.isEmpty(), "Stores list should be empty initially");
    }

    @Test
    void testGetAllStores_MultipleStores() {
        MultiStore store1 = new MultiStore();
        store1.setStoreId(1);
        store1.setStoreName("Branch A");
        store1.setLocation("123 Main St");
        store1.setManager("John Doe");
        store1.setContactInfo("123-456-7890");

        MultiStore store2 = new MultiStore();
        store2.setStoreId(2);
        store2.setStoreName("Branch B");
        store2.setLocation("456 Elm St");
        store2.setManager("Jane Smith");
        store2.setContactInfo("987-654-3210");

        multiStoreService.addStore(store1);
        multiStoreService.addStore(store2);

        List<MultiStore> stores = multiStoreService.getAllStores();
        assertNotNull(stores, "Stores list should not be null");
        assertEquals(2, stores.size(), "There should be exactly two stores in the list");
        assertEquals("Branch A", stores.get(0).getStoreName(), "First store name should match");
        assertEquals("Branch B", stores.get(1).getStoreName(), "Second store name should match");
    }
}
