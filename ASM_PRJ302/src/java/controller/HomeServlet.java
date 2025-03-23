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

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy userId từ session
        int userId;
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj instanceof Integer) {
            userId = (Integer) userIdObj;
        } else if (userIdObj instanceof String) {
            try {
                userId = Integer.parseInt((String) userIdObj);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid user ID format in session.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }
        } else {
            request.setAttribute("error", "User ID not found in session.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Tính toán số lượng yêu cầu
        setRequestCounts(request, userId);

        request.getRequestDispatcher("home.jsp").forward(request, response);
    }

    private void setRequestCounts(HttpServletRequest request, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Status, COUNT(*) as count FROM LeaveRequests WHERE UserID = ? GROUP BY Status";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                int pendingCount = 0, approvedCount = 0, rejectedCount = 0;
                while (rs.next()) {
                    String status = rs.getString("Status");
                    int count = rs.getInt("count");
                    if ("Pending".equals(status)) {
                        pendingCount = count;
                    } else if ("Approved".equals(status)) {
                        approvedCount = count;
                    } else if ("Rejected".equals(status)) {
                        rejectedCount = count;
                    }
                }
                request.setAttribute("pendingCount", pendingCount);
                request.setAttribute("approvedCount", approvedCount);
                request.setAttribute("rejectedCount", rejectedCount);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error retrieving request counts: " + e.getMessage());
        }
    }
}