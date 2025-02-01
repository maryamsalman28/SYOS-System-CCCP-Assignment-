package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.MultiStore;

public class MultiStoreDAO {
    private static final String DB_URL = "jdbc:sqlserver://DESKTOP-CIJ18KU:1433;databaseName=CCCPSYOS;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    // Add a new store
    public void addStore(MultiStore store) {
        String query = "INSERT INTO MultiStores (store_name, location, manager, contact_info) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, store.getStoreName());
            stmt.setString(2, store.getLocation());
            stmt.setString(3, store.getManager());
            stmt.setString(4, store.getContactInfo());

            stmt.executeUpdate();
            System.out.println("Store added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve all stores
    public List<MultiStore> getAllStores() {
        List<MultiStore> stores = new ArrayList<>();
        String query = "SELECT * FROM MultiStores";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                MultiStore store = new MultiStore();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                store.setLocation(rs.getString("location"));
                store.setManager(rs.getString("manager"));
                store.setContactInfo(rs.getString("contact_info"));

                stores.add(store);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stores;
    }
}
