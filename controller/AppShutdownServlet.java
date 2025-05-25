package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import util.SessionPool;

// @WebServlet(urlPatterns = "/shutdown", loadOnStartup = 1)
public class AppShutdownServlet extends HttpServlet {

    @Override
    public void destroy() {
        System.out.println("🧹 AppShutdownServlet triggered: calling SessionPool.shutdown()");
        SessionPool.getInstance().shutdown();
    }

    @Override
    public void init() throws ServletException {
        System.out.println("🚀 AppShutdownServlet initialized.");
    }
}
