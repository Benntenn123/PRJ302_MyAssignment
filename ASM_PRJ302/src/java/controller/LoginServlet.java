package controller;

import dal.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String loginError = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.UserID, u.FullName, r.RoleName, u.Department, u.Section " +
                     "FROM Users u " +
                     "LEFT JOIN UserRoles ur ON u.UserID = ur.UserID " +
                     "LEFT JOIN Roles r ON ur.RoleID = r.RoleID " +
                     "WHERE u.Username = ? AND u.Password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getString("UserID"));
                session.setAttribute("fullName", rs.getString("FullName"));
                session.setAttribute("role", rs.getString("RoleName") != null ? rs.getString("RoleName") : "Unknown");
                session.setAttribute("department", rs.getString("Department"));
                session.setAttribute("section", rs.getString("Section"));
                response.sendRedirect("home");
                return;
            } else {
                loginError = "Incorrect username or password!";
            }
        } catch (SQLException e) {
            loginError = "Database connection error: " + e.getMessage();
        }

        request.setAttribute("loginError", loginError);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            response.sendRedirect("home");
        } else {
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}