
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Bill;
import model.Item;
import util.DatabaseConnection;


public class BillDAO {

    public void addBill(Bill bill, Connection conn) throws SQLException {
        String billQuery = "INSERT INTO Bills (bill_date, total_amount, discount_applied, final_amount, cash_tendered, change_due) VALUES (?, ?, ?, ?, ?, ?)";
        String itemQuery = "INSERT INTO BillItems (bill_id, item_id, quantity, price_per_unit, total_price) VALUES (?, ?, ?, ?, ?)";
    
        PreparedStatement billStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;
    
        ItemDAO itemDAO = new ItemDAO();
    
        try {
            conn.setAutoCommit(false); 
            System.out.println("[TRANSACTION STARTED] Processing new bill...");
    
            // insert bill
            billStmt = conn.prepareStatement(billQuery, Statement.RETURN_GENERATED_KEYS);
            billStmt.setString(1, bill.getBillDate());
            billStmt.setDouble(2, bill.getTotalAmount());
            billStmt.setDouble(3, bill.getDiscount());
            billStmt.setDouble(4, bill.getFinalAmount());
            billStmt.setDouble(5, bill.getCashTendered());
            billStmt.setDouble(6, bill.getChangeDue());
    
            int affectedRows = billStmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("[ERROR] Bill was NOT inserted!");
                throw new SQLException("Bill insertion failed, no rows affected.");
            }
    
            // retrieve generated bill ID
            rs = billStmt.getGeneratedKeys();
            if (!rs.next()) {
                System.out.println("[ERROR] Failed to retrieve generated bill ID!");
                throw new SQLException("Failed to retrieve generated bill ID.");
            }
            int billId = rs.getInt(1);
            System.out.println("[BILL INSERTED] Bill ID: " + billId);
    
            // set the generated bill ID
            bill.setBillId(billId);
    
            // insert items
            itemStmt = conn.prepareStatement(itemQuery);
            for (Item item : bill.getItems()) {
                System.out.println("[ADDING ITEM TO BILL] Item ID: " + item.getItemId() + " | Quantity: " + item.getQuantity());
    
                itemStmt.setInt(1, billId);
                itemStmt.setInt(2, item.getItemId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getPrice());
                itemStmt.setDouble(5, item.getQuantity() * item.getPrice());
                itemStmt.addBatch();
            }
    
            int[] itemInsertResults = itemStmt.executeBatch();
            if (itemInsertResults.length == 0) {
                System.out.println("[ERROR] No items were inserted into BillItems!");
                throw new SQLException("No items were inserted into BillItems.");
            }
            System.out.println("[ITEMS INSERTED] Items successfully added to Bill ID: " + billId);
    
            // reduce stock using SP
            for (Item item : bill.getItems()) {
                System.out.println("[STOCK REDUCTION CALL] Item ID: " + item.getItemId() + " | Quantity: " + item.getQuantity());
                boolean stockReduced = itemDAO.reduceStockAfterCheckout(item.getItemId(), item.getQuantity(), conn);
                if (!stockReduced) {
                    System.out.println("[ERROR] Stock reduction failed! Rolling back transaction.");
                    conn.rollback();
                    return; // stop execution if stock reduction fails
                }
            }
    
            // commit transaction
            conn.commit();
            System.out.println("🔵 [TRANSACTION COMMITTED] Bill ID: " + billId);
        } catch (SQLException e) {
            conn.rollback(); // rollback transaction if any error occurs
            System.out.println("[TRANSACTION ROLLED BACK] Due to error: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (billStmt != null) billStmt.close();
            if (itemStmt != null) itemStmt.close();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    

    

    // retrieve all bills
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM Bills";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Bill bill = new Bill.Builder()
                        .billId(rs.getInt("bill_id"))
                        .billDate(rs.getString("bill_date"))
                        .totalAmount(rs.getDouble("total_amount"))
                        .discount(rs.getDouble("discount_applied")) // Fixed column name
                        .finalAmount(rs.getDouble("final_amount"))
                        .cashTendered(rs.getDouble("cash_tendered"))
                        .changeDue(rs.getDouble("change_due"))
                        .salesType(rs.getString("sales_type")) // Retrieve sales type
                        .build();

                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bills;
    }

    // retrieve bills by type and date
    public List<Bill> getBillsByTypeAndDate(String salesType, String date) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM Bills WHERE sales_type = ? AND bill_date = ?"; 
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, salesType);
            stmt.setDate(2, Date.valueOf(date));  
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Bill bill = new Bill.Builder()
                        .billId(rs.getInt("bill_id"))
                        .billDate(rs.getString("bill_date"))
                        .totalAmount(rs.getDouble("total_amount"))
                        .discount(rs.getDouble("discount_applied"))
                        .finalAmount(rs.getDouble("final_amount"))
                        .cashTendered(rs.getDouble("cash_tendered"))
                        .changeDue(rs.getDouble("change_due"))
                        .salesType(rs.getString("sales_type"))
                        .build();
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bills;
    }
}

