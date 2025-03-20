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
import java.sql.SQLException;
import java.sql.Date;

@WebServlet(name = "SubmitRequestServlet", urlPatterns = {"/submit-request"})
public class SubmitRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy thông tin từ form
        String userIdStr = (String) session.getAttribute("userId");
        String leaveType = request.getParameter("leaveType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        // Chuyển đổi userId từ String sang int
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID người dùng không hợp lệ.");
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
            return;
        }

        // Kiểm tra các tham số
        if (leaveType == null || leaveType.isEmpty() ||
            startDateStr == null || startDateStr.isEmpty() ||
            endDateStr == null || endDateStr.isEmpty() ||
            reason == null || reason.isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin.");
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
            return;
        }

        // Chèn dữ liệu vào cơ sở dữ liệu
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO LeaveRequests (UserID, LeaveType, StartDate, EndDate, Reason, Status, CreatedDate, ModifiedDate) " +
                     "VALUES (?, ?, ?, ?, ?, 'Chờ duyệt', GETDATE(), GETDATE())")) {
            stmt.setInt(1, userId);
            stmt.setString(2, leaveType);
            stmt.setDate(3, Date.valueOf(startDateStr));
            stmt.setDate(4, Date.valueOf(endDateStr));
            stmt.setString(5, reason);
            stmt.executeUpdate();

            response.sendRedirect("view-requests");
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi khi gửi đơn: " + e.getMessage());
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Ngày không hợp lệ: " + e.getMessage());
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}