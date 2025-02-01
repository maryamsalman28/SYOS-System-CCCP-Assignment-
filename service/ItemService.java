package service;

import dao.ItemDAO;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import model.Item;
import model.StockBatch;
import observer.Observer;
import observer.Subject;

public class ItemService implements Subject {
    private final ItemDAO itemDAO = new ItemDAO();
    private final List<Observer> observers = new ArrayList<>();

    // sdd a new item to the inventory
    public void addItem(Item item) {
        if (item.getQuantity() > 0 && item.getPrice() > 0) {
            itemDAO.addItem(item);
            notifyObservers("New item added: " + item.getItemName());
        } else {
            System.out.println("Invalid item details. Cannot add.");
        }
    }

    // display all items in the inventory
    public void displayAllItems() {
        List<Item> items = itemDAO.getAllItems();
        if (items.isEmpty()) {
            System.out.println("No items found.");
        } else {
            System.out.println("Items in Inventory:");
            for (Item item : items) {
                System.out.printf("ID: %d, Code: %s, Name: %s, Quantity: %d, Price: %.2f, Expiry: %s\n",
                        item.getItemId(), item.getItemCode(), item.getItemName(),
                        item.getQuantity(), item.getPrice(), item.getExpiryDate());
            }
        }
    }

    // get online inventory items
    public List<Item> getOnlineInventory() {
        return itemDAO.getOnlineInventory();
    }



    // delete an item from the inventory
    public void deleteItem(String itemCode) {
        itemDAO.deleteItem(itemCode); // Updated method in ItemDAO
        notifyObservers("Item with Code " + itemCode + " has been deleted.");
    }

    // get current inventory stock levels
    public List<Item> getInventoryStockLevels() {
        return itemDAO.getAllItems();
    }

    // Get items with low stock
    public List<Item> getLowStockItems(int reorderLevel) {
        return itemDAO.getLowStockItems(reorderLevel);
    }

// Fetch item by its Code

public Item getItemByCode(String itemCode) {
    Item item = itemDAO.getItemByCode(itemCode);
    if (item == null) {
        System.out.println("Item with Code '" + itemCode + "' not found in the database.");
    }
    return item;
}

//reduce stock after checkout
public boolean reduceStockAfterCheckout(String itemCode, int quantity, Connection conn) {  
    Item item = itemDAO.getItemByCode(itemCode);  
    if (item == null) {
        System.out.println("No item found with code: " + itemCode);
        return false;
    }

    int itemId = item.getItemId(); // Convert itemCode to itemId
    return itemDAO.reduceStockAfterCheckout(itemId, quantity, conn);
}






  

   // Generate batch-wise stock reports
   public void generateBatchWiseStockReport() {
    List<Item> allItems = itemDAO.getAllItems();
    System.out.println("\n --- Batch-Wise Stock Report ---");
    for (Item item : allItems) {
        System.out.println("Item: " + item.getItemName());
        int itemId = item.getItemId(); 
        List<StockBatch> batches = itemDAO.getBatchesForItem(item.getItemCode());  
        for (StockBatch batch : batches) {
            System.out.printf("  Batch ID: %d | Quantity: %d | Expiry Date: %s | Arrival Date: %s\n",
                    batch.getBatchId(), batch.getQuantity(), batch.getExpiryDate(), batch.getArrivalDate());
        }
    }
}

public boolean reorderNewBatch(String itemCode, int newBatchQuantity, String batchNumber, String dateOfPurchase, String expiryDate) {
    Item item = itemDAO.getItemByCode(itemCode);
    if (item == null) {
        System.out.println("Item with code '" + itemCode + "' not found.");
        return false;
    }

    int itemId = item.getItemId();
    return itemDAO.reorderNewBatch(itemId, newBatchQuantity, batchNumber, dateOfPurchase, expiryDate);
}


    // Observer Pattern Implementation
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}