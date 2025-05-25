package controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.Order;
import model.OrderItem;
import service.OrderProcessor;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final OrderProcessor processor = OrderProcessor.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            System.out.println("🧾 Checkout Request Received");
            System.out.println("➡️ Raw JSON: " + sb.toString());

            Order order = gson.fromJson(sb.toString(), Order.class);

            if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("Cart is empty or invalid.");
                return;
            }

            System.out.println("➡️ userId: " + order.getUserId());

            for (OrderItem item : order.getItems()) {
                System.out.println("🟢 itemId: " + item.getItemId() + ", quantity: " + item.getQuantity());
            }

            processor.queueOrder(order); // ✅ full order with customer + item list

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print("Order submitted!");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Invalid order format.");
        }
    }
}
