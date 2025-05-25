package controller;

import util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");

        System.out.println("SignupServlet triggered");
        System.out.println("Received: " + name + ", " + email + ", " + phone + ", " + address);

        if (name == null || email == null || password == null || phone == null || address == null ||
                name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("Missing required fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM Users WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.print("Email already exists.");
                    return;
                }
            }

            String insertSql = "INSERT INTO Users (name, email, password, phone_number, address, registration_date, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password); // Hash in production
                insertStmt.setString(4, phone);
                insertStmt.setString(5, address);
                insertStmt.setDate(6, new java.sql.Date(System.currentTimeMillis())); // registration_date
                insertStmt.setString(7, "customer");

                int rows = insertStmt.executeUpdate();
                if (rows > 0) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.print("Signup successful.");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("Signup failed.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Database error.");
        }
    }
}
