package controller;

import dal.DatabaseConnection; // Nếu bạn để DatabaseConnection trong package dal
// import DatabaseConnection; // Nếu bạn để DatabaseConnection ở default package
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "HomeServlet", urlPatterns = {"/HomeServlet"})
public class HomeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String fullName = (String) session.getAttribute("fullName");
        String role = (String) session.getAttribute("role");
        String department = (String) session.getAttribute("department");
        String section = (String) session.getAttribute("section");

        request.setAttribute("fullName", fullName);
        request.setAttribute("role", role);
        request.setAttribute("department", department);
        request.setAttribute("section", section);

        request.getRequestDispatcher("home.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}