package dao;

import model.Order;
import model.OrderItem;

import java.sql.*;

public class OnlineOrderDAO {

    // ✅ Step 1: Insert order and return generated ID
    public int insertOnlineOrder(Order order, Connection conn) throws SQLException {
        String orderInsertSql = "INSERT INTO tblOnlineOrder (user_id, address, phone_number, total, order_date) " +
                "VALUES (?, ?, ?, ?, GETDATE())";

        try (PreparedStatement orderStmt = conn.prepareStatement(orderInsertSql, Statement.RETURN_GENERATED_KEYS)) {
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setString(2, order.getAddress());
            orderStmt.setString(3, order.getPhoneNumber());
            orderStmt.setDouble(4, order.getTotal());

            int rows = orderStmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Inserting online order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);
                    return orderId;
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
    }

    // ✅ Step 2: Insert a single item linked to an order
    public void insertOnlineOrderItem(int orderId, OrderItem item, Connection conn) throws SQLException {
        String itemInsertSql = "INSERT INTO tblOnlineOrderItem (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement itemStmt = conn.prepareStatement(itemInsertSql)) {
            itemStmt.setInt(1, orderId);
            itemStmt.setInt(2, item.getItemId());
            itemStmt.setInt(3, item.getQuantity());
            itemStmt.setDouble(4, item.getPrice());
            itemStmt.executeUpdate();
        }
    }
}
