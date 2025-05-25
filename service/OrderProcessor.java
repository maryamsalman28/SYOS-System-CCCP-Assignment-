package service;

import model.Order;
import model.OrderItem;
import util.SessionPool;
import dao.ItemDAO;
import dao.OnlineOrderDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OrderProcessor implements Runnable {
    private static final OrderProcessor instance = new OrderProcessor();
    private final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>(100);
    private final ItemDAO itemDAO = new ItemDAO();
    private final OnlineOrderDAO onlineOrderDAO = new OnlineOrderDAO();

    private OrderProcessor() {
        Thread worker = new Thread(this, "OrderProcessor-Thread");
        worker.setDaemon(true);
        worker.start();
    }

    public static OrderProcessor getInstance() {
        return instance;
    }

    public void queueOrder(Order order) {
        try {
            orderQueue.put(order);
            System.out.println("Order queued for userId=" + order.getUserId() + " with " + order.getItems().size() + " items.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to queue order: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Order order = orderQueue.take();
                System.out.println("🛠️ Processing order for userId=" + order.getUserId() + " by " + Thread.currentThread().getName());

                Connection conn = SessionPool.getInstance().borrowConnection();
                try {
                    conn.setAutoCommit(false);

                    boolean stockOk = true;

                    for (OrderItem item : order.getItems()) {
                        int itemId = item.getItemId();
                        int quantity = item.getQuantity();

                        // debug check: quantity_in_store
                        try (PreparedStatement checkStmt = conn.prepareStatement(
                                "SELECT quantity_in_store FROM Items WHERE item_id = ?")) {
                            checkStmt.setInt(1, itemId);
                            try (ResultSet rs = checkStmt.executeQuery()) {
                                if (rs.next()) {
                                    System.out.println("[CHECK] quantity_in_store for itemId=" + itemId + " = " + rs.getInt(1));
                                } else {
                                    System.out.println("[CHECK] itemId=" + itemId + " NOT FOUND in Items");
                                }
                            }
                        }

                        // debug check: quantity_on_shelf
                        try (PreparedStatement shelfStmt = conn.prepareStatement(
                                "SELECT quantity_on_shelf FROM Shelves WHERE item_id = ?")) {
                            shelfStmt.setInt(1, itemId);
                            try (ResultSet rs = shelfStmt.executeQuery()) {
                                if (rs.next()) {
                                    System.out.println("[CHECK] quantity_on_shelf for itemId=" + itemId + " = " + rs.getInt(1));
                                } else {
                                    System.out.println("[CHECK] itemId=" + itemId + " NOT FOUND in Shelves");
                                }
                            }
                        }

                        System.out.println("[DEBUG] Sending itemId=" + itemId + ", quantity=" + quantity + " to stored procedure");
                        boolean updated = itemDAO.reduceStockAfterCheckout(itemId, quantity, conn);

                        if (!updated) {
                            stockOk = false;
                            break;
                        }
                    }

                    if (!stockOk) {
                        conn.rollback();
                        System.err.println("Order aborted due to stock issues.");
                        continue;
                    }

                    // Step 2: Insert order
                    int orderId = onlineOrderDAO.insertOnlineOrder(order, conn);
                    order.setOrderId(orderId);

                    // Step 3: Insert items
                    for (OrderItem item : order.getItems()) {
                        onlineOrderDAO.insertOnlineOrderItem(orderId, item, conn);
                    }

                    conn.commit();
                    System.out.println("Online order recorded: Order ID = " + orderId);

                } catch (SQLException e) {
                    conn.rollback();
                    System.err.println("DB Error during order processing: " + e.getMessage());
                } finally {
                    SessionPool.getInstance().returnConnection(conn);
                }

                Thread.sleep(1000); // Simulated delay

            } catch (Exception e) {
                System.err.println("OrderProcessor error: " + e.getMessage());
            }
        }
    }
}
