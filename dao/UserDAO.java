package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import util.DatabaseConnection;

public class UserDAO {
    public boolean addUser(String name, String email, String password, String phone, String address, String role) {
        String query = "INSERT INTO Users (name, email, password, phone_number, address, registration_date, role) VALUES (?, ?, ?, ?, ?, GETDATE(), ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setString(6, role);

            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateUser(String email, String password) {  
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // returns true if user exists

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}