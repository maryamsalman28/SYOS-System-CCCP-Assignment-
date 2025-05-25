package controller;

import com.google.gson.Gson;
import dao.ItemDAO;
import model.Item;
import service.ItemService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private final ItemService itemService = new ItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Item> items = itemService.getAvailableItems();
            String json = new Gson().toJson(items);

            resp.setContentType("application/json");
            resp.getWriter().print(json);
        } catch (Exception e) {
            e.printStackTrace();  // ✅ This will show the real error in IntelliJ
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("Error loading products");
        }
    }
}


