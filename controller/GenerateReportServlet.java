package controller;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.util.concurrent.*;

@WebServlet("/generateReport")
public class GenerateReportServlet extends HttpServlet {

    private ExecutorService executor;

    @Override
    public void init() throws ServletException {
        // Fixed thread pool with 5 threads
        executor = Executors.newFixedThreadPool(5);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userId = req.getParameter("userId");

        Callable<String> task = () -> {
            // Simulate time-consuming processing
            Thread.sleep(3000); // 3 seconds
            return "Report for user " + userId + " generated at " + System.currentTimeMillis();
        };

        try {
            Future<String> result = executor.submit(task);
            String report = result.get(); // Wait for task to complete

            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println("{");
            out.println("\"status\": \"success\",");
            out.println("\"report\": \"" + report + "\"");
            out.println("}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    public void destroy() {
        executor.shutdown();
    }
}
