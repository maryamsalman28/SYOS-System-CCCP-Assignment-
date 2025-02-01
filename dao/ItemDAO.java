package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Item;
import model.StockBatch; 
import util.DatabaseConnection;

public class ItemDAO {

     // add a new item
     public void addItem(Item item) {
        String storedProcedure = "{CALL AddItemWithStock(?, ?, ?, ?, ?, ?)}";
    
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(storedProcedure)) {
    
            stmt.setString(1, item.getItemCode());
            stmt.setString(2, item.getItemName());
            stmt.setInt(3, item.getQuantity()); 
            stmt.setDouble(4, item.getPrice());
            stmt.setString(5, item.getExpiryDate());
            stmt.setString(6, LocalDate.now().toString());
    
            stmt.execute();
            System.out.println("Item, stock batch, and shelves updated successfully.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    

    // reduce stock after checkout
public boolean reduceStockAfterCheckout(int itemId, int quantity, Connection conn) {
    String sql = "{CALL ReduceStockAfterCheckout(?, ?)}";

    try (CallableStatement stmt = conn.prepareCall(sql)) { 
        System.out.println("[CALLING STORED PROCEDURE] Item ID: " + itemId + " | Quantity: " + quantity);

        stmt.setInt(1, itemId);
        stmt.setInt(2, quantity);

        stmt.execute();

        // check for warnings
        SQLWarning warning = stmt.getWarnings();
        if (warning != null) {
            System.out.println("[STOCK REDUCTION WARNING] " + warning.getMessage());
            return false; // ensure stock reduction failure
        }

        System.out.println("[STOCK SUCCESSFULLY REDUCED] Item ID: " + itemId);
        return true;
    } catch (SQLException e) {
        System.out.println("[STOCK REDUCTION FAILED] Error: " + e.getMessage());
        return false;
    }
}













    // fetch an item by item_code
    public Item getItemByCode(String itemCode) {
        String query = "SELECT * FROM Items WHERE item_code = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();

            
            if (conn == null || conn.isClosed()) {
                System.out.println("Database connection lost. Reconnecting...");
                conn = DatabaseConnection.getInstance().getConnection();
                if (conn == null || conn.isClosed()) {
                    throw new SQLException("Failed to re-establish database connection.");
                }
            }

            stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Item.Builder()
                        .itemId(rs.getInt("item_id"))
                        .itemCode(rs.getString("item_code"))
                        .itemName(rs.getString("item_name"))
                        .batchNumber(rs.getString("batch_number"))
                        .quantity(rs.getInt("quantity_in_store"))
                        .price(rs.getDouble("price"))
                        .expiryDate(rs.getString("expiry_date"))
                        .dateOfPurchase(rs.getString("date_of_purchase"))
                        .salesType(rs.getString("sales_type"))
                        .build();
            } else {
                System.out.println("Item with Code '" + itemCode + "' not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
           
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    

    // fetch all items 
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM Items";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Item item = new Item.Builder()
                        .itemId(rs.getInt("item_id"))
                        .itemCode(rs.getString("item_code"))
                        .itemName(rs.getString("item_name"))
                        .batchNumber(rs.getString("batch_number"))
                        .quantity(rs.getInt("quantity_in_store"))
                        .price(rs.getDouble("price"))
                        .expiryDate(rs.getString("expiry_date"))
                        .dateOfPurchase(rs.getString("date_of_purchase"))
                        .salesType(rs.getString("sales_type"))
                        .build();
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
    // method to retrieve only online inventory
    public List<Item> getOnlineInventory() {
        List<Item> onlineItems = new ArrayList<>();
        String query = "SELECT * FROM Items WHERE sales_type = 'online'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Item item = new Item.Builder()
                .itemId(rs.getInt("item_id"))
                .itemCode(rs.getString("item_code"))
                .itemName(rs.getString("item_name"))
                .batchNumber(rs.getString("batch_number"))
                .quantity(rs.getInt("quantity_in_store")) 
                .price(rs.getDouble("price"))
                .expiryDate(rs.getString("expiry_date"))
                .dateOfPurchase(rs.getString("date_of_purchase"))
                .salesType(rs.getString("sales_type")) 
                .build();

                onlineItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return onlineItems;
    }




   // delete an item
   public void deleteItem(String itemCode) {
    String query = "DELETE FROM Items WHERE item_code = ?";

    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, itemCode);
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println(" Item deleted successfully.");
        } else {
            System.out.println("No item found with Code: " + itemCode);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // get inventory stock levels
    public List<Item> getInventoryStockLevels() {
        List<Item> inventory = new ArrayList<>();
        String query = "SELECT item_code, item_name, quantity_in_store FROM Items";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Item item = new Item.Builder()
                        .itemCode(rs.getString("item_code"))
                        .itemName(rs.getString("item_name"))
                        .quantity(rs.getInt("quantity_in_store"))
                        .build();
                inventory.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventory;
    }


  // get low-stock items using item_code
    public List<Item> getLowStockItems(int reorderLevel) {
        List<Item> lowStockItems = new ArrayList<>();
        String query = "SELECT item_code, item_name, quantity_in_store FROM Items WHERE quantity_in_store < ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reorderLevel);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item.Builder()
                        .itemCode(rs.getString("item_code"))
                        .itemName(rs.getString("item_name"))
                        .quantity(rs.getInt("quantity_in_store"))
                        .build();
                lowStockItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lowStockItems;
    }


// fetch batches for an item (FIFO + Expiry Date Priority)
public List<StockBatch> getBatchesForItem(String itemCode) { 
    List<StockBatch> batches = new ArrayList<>();
    String query = "SELECT sb.* FROM StockBatches sb JOIN Items i ON sb.item_id = i.item_id WHERE i.item_code = ? ORDER BY sb.expiry_date ASC";

    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, itemCode);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            StockBatch batch = new StockBatch(
                    rs.getInt("batch_id"),
                    rs.getInt("item_id"),
                    rs.getInt("quantity"),
                    rs.getDate("expiry_date").toLocalDate(),
                    rs.getDate("date_of_purchase").toLocalDate()
            );
            batches.add(batch);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return batches;
}

//shelf restocking
public boolean restockShelves(Connection conn) {
    String sql = "{CALL RestockShelves}";

    try (CallableStatement stmt = conn.prepareCall(sql)) {  
        System.out.println("Calling RestockShelves procedure...");

        // ensure autocommit is enabled to prevent transaction conflicts
        conn.setAutoCommit(true);  

        // execute the stored procedure
        boolean executed = stmt.execute();  
        System.out.println(" Procedure executed successfully: " + executed);

        // Skip getUpdateCount() and assume execution success unless an exception occurs
        return true;
    } catch (SQLException e) {
        System.err.println("SQL Error while restocking shelves: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

//reorder new batch

public boolean reorderNewBatch(int itemId, int newBatchQuantity, String batchNumber, String dateOfPurchase, String expiryDate) {
    String callSP = "{CALL ReorderNewBatch(?, ?, ?, ?, ?)}"; 

    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         CallableStatement callableStmt = conn.prepareCall(callSP)) {

        // Set parameters for stored procedure
        callableStmt.setInt(1, itemId);
        callableStmt.setInt(2, newBatchQuantity);
        callableStmt.setString(3, batchNumber);
        callableStmt.setString(4, dateOfPurchase);
        callableStmt.setString(5, expiryDate);

        // Execute the stored procedure
        callableStmt.execute();

        System.out.println("Stored procedure executed successfully.");
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}













   // test database connection
   public void testConnection() {
    try {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn != null) {
            System.out.println("Database connection successful!");
        } else {
            System.out.println("Failed to connect to the database.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}