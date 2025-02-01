package service;

import dao.BillDAO;
import dao.ItemDAO;
import model.Bill;
import model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {
    private ReportService reportService;
    private ItemDAO itemDAO;
    private BillDAO billDAO;

    @BeforeEach
    void setUp() {
        itemDAO = new ItemDAO();  // Use actual DAO implementation
        billDAO = new BillDAO();
        reportService = new ReportService(); // No dependency injection needed
    }

    @Test
    void testGenerateBillHistoryReport() {
        List<Bill> bills = billDAO.getAllBills();
        assertNotNull(bills, "Bill list should not be null");
    }

    @Test
    void testGenerateReorderReport() {
        List<Item> lowStockItems = itemDAO.getLowStockItems(50);
        assertNotNull(lowStockItems, "Low stock items list should not be null");
    }
}
