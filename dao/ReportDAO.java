package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ShelfItem;
import util.DatabaseConnection;

public class ReportDAO {

    // Fetches the current stock of items on shelves
    public List<ShelfItem> getShelfItems() {
        List<ShelfItem> shelfItems = new ArrayList<>();
        String query = "SELECT s.shelf_id, i.item_name, s.quantity_on_shelf " +
                       "FROM Shelves s " +
                       "JOIN Items i ON s.item_id = i.item_id " +
                       "ORDER BY s.shelf_id ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ShelfItem item = new ShelfItem.Builder()
                        .shelfId(rs.getInt("shelf_id"))
                        .itemName(rs.getString("item_name"))
                        .quantityOnShelf(rs.getInt("quantity_on_shelf"))
                        .build();
                shelfItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shelfItems;
    }

    
}
