package service;

import dao.BillDAO;
import dao.ItemDAO;
import java.util.List;
import model.Bill;
import model.Item;
import model.StockBatch;

public class ReportService {
    private final ItemDAO itemDAO = new ItemDAO();
    private final BillDAO billDAO = new BillDAO();

    // Generate report for items reshelved at the end of the day
    public void generateReshelvedItemsReport() {
        System.out.println("\n--- Reshelved Items Report ---");
        List<Item> allItems = itemDAO.getAllItems();

        for (Item item : allItems) {
            int itemId = item.getItemId(); // 
            List<StockBatch> batches = itemDAO.getBatchesForItem(item.getItemCode());  
            for (StockBatch batch : batches) {
                if (batch.isExpired()) {
                    System.out.printf("Item: %s | Batch ID: %d | Quantity: %d | Expiry Date: %s\n",
                            item.getItemName(), batch.getBatchId(), batch.getQuantity(), batch.getExpiryDate());
                }
            }
        }
    }

    // Generate batch-wise stock levels report
    public void generateBatchWiseStockReport() {
        System.out.println("\n--- Batch-Wise Stock Levels ---");
        List<Item> allItems = itemDAO.getAllItems();

        for (Item item : allItems) {
            System.out.println("Item: " + item.getItemName());
            int itemId = item.getItemId(); // 
            List<StockBatch> batches = itemDAO.getBatchesForItem(item.getItemCode());  
            for (StockBatch batch : batches) {
                System.out.printf("  Batch ID: %d | Quantity: %d | Expiry Date: %s | Arrival Date: %s\n",
                        batch.getBatchId(), batch.getQuantity(), batch.getExpiryDate(), batch.getArrivalDate());
            }
        }
    }

    // Generate reorder report for items below a quantity of 50
    public void generateReorderReport() {
        System.out.println("\n--- Reorder Report (Items Below 50) ---");
        List<Item> lowStockItems = itemDAO.getLowStockItems(50);

        for (Item item : lowStockItems) {
            System.out.printf("Item: %s | Quantity: %d | Item Code: %s\n",
                    item.getItemName(), item.getQuantity(), item.getItemCode());
        }
    }

    // Generate complete bill history
    public void generateBillHistoryReport() {
        System.out.println("\n--- Complete Bill History ---");
        List<Bill> bills = billDAO.getAllBills(); 

        for (Bill bill : bills) {
            System.out.printf("Bill ID: %d | Date: %s | Total: %.2f | Discount: %.2f | Final: %.2f\n",
                    bill.getBillId(), bill.getBillDate(), bill.getTotalAmount(), bill.getDiscount(), bill.getFinalAmount());
        }
    }

    // Generate daily sales report for both in-store and online transactions
    public void generateDailySalesReport(String date) {
        System.out.println("\n--- Daily Sales Report ---");

        // Retrieve in-store sales
        List<Bill> inStoreSales = billDAO.getBillsByTypeAndDate("in-store", date);
        System.out.println("--- In-Store Sales ---");
        for (Bill bill : inStoreSales) {
            System.out.printf("Bill ID: %d | Total: %.2f | Discount: %.2f | Final: %.2f\n",
                    bill.getBillId(), bill.getTotalAmount(), bill.getDiscount(), bill.getFinalAmount());
        }

        // Retrieve online sales
        List<Bill> onlineSales = billDAO.getBillsByTypeAndDate("online", date);
        System.out.println("--- Online Sales ---");
        for (Bill bill : onlineSales) {
            System.out.printf("Bill ID: %d | Total: %.2f | Discount: %.2f | Final: %.2f\n",
                    bill.getBillId(), bill.getTotalAmount(), bill.getDiscount(), bill.getFinalAmount());
        }
    }

    // Generate report for stock reduction after checkout
    public void generateStockReductionReport() {
        System.out.println("\n--- Stock Reduction Report ---");
        List<Item> allItems = itemDAO.getAllItems();

        for (Item item : allItems) {
            int itemId = item.getItemId(); 
            int remainingStock = item.getQuantity(); 
            System.out.printf("Item: %s | Remaining Stock: %d\n", item.getItemName(), remainingStock);
        }
    }
}
