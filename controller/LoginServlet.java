package controller;

import util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json"); // ✅ respond with JSON
        PrintWriter out = resp.getWriter();

        // Get email and password from JSON body
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }

        String jsonInput = sb.toString();
        String email = null;
        String password = null;

        try {
            // ✅ Parse JSON manually (can be replaced with Gson if needed)
            String[] parts = jsonInput.replace("{", "").replace("}", "").replace("\"", "").split(",");
            for (String part : parts) {
                String[] pair = part.split(":");
                if (pair.length == 2) {
                    if (pair[0].trim().equals("email")) {
                        email = pair[1].trim();
                    } else if (pair[0].trim().equals("password")) {
                        password = pair[1].trim();
                    }
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid request format\"}");
            return;
        }

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Email and password are required.\"}");
            return;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT user_id, name FROM Users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");

                resp.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"userId\":" + userId + ",\"name\":\"" + name + "\"}");
                System.out.println("✅ User authenticated: " + email);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":\"Invalid email or password\"}");
                System.out.println("❌ Invalid login attempt for: " + email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Database error\"}");
        }
    }
}
