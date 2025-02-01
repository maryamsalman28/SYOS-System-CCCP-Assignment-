package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Discount;
import util.DatabaseConnection;

public class DiscountDAO {

    // add a new discount
    public void addDiscount(Discount discount) {
        String query = "INSERT INTO Discounts (discount_code, description, discount_percentage, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, discount.getDiscountCode());
            stmt.setString(2, discount.getDescription());
            stmt.setDouble(3, discount.getDiscountPercentage());
            stmt.setDate(4, Date.valueOf(discount.getStartDate()));
            stmt.setDate(5, Date.valueOf(discount.getEndDate()));

            stmt.executeUpdate();
            System.out.println("Discount added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // retrieve all discounts
    public List<Discount> getAllDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM Discounts";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Discount discount = new Discount();
                discount.setDiscountId(rs.getInt("discount_id"));
                discount.setDiscountCode(rs.getString("discount_code"));
                discount.setDescription(rs.getString("description"));
                discount.setDiscountPercentage(rs.getDouble("discount_percentage"));
                discount.setStartDate(rs.getDate("start_date").toLocalDate());
                discount.setEndDate(rs.getDate("end_date").toLocalDate());

                discounts.add(discount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return discounts;
    }
}
