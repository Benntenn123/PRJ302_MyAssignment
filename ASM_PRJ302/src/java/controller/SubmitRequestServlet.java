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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "SubmitRequestServlet", urlPatterns = {"/submit-request"})
public class SubmitRequestServlet extends HttpServlet {

    private static final long ONE_YEAR_MILLIS = 365L * 24 * 60 * 60 * 1000;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String leaveType = request.getParameter("leaveType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        if (isEmptyOrNull(leaveType) || isEmptyOrNull(startDateStr) || 
            isEmptyOrNull(endDateStr) || isEmptyOrNull(reason)) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            Date currentDate = new Date();
            // Chuẩn hóa currentDate về đầu ngày (00:00:00)
            String currentDateStr = dateFormat.format(currentDate);
            Date normalizedCurrentDate = dateFormat.parse(currentDateStr);
            Date maxDate = new Date(currentDate.getTime() + ONE_YEAR_MILLIS);

            if (startDate.before(normalizedCurrentDate)) {
                request.setAttribute("error", "Start date cannot be in the past.");
                request.getRequestDispatcher("create-request.jsp").forward(request, response);
                return;
            }
            if (endDate.before(startDate)) {
                request.setAttribute("error", "End date must be after start date.");
                request.getRequestDispatcher("create-request.jsp").forward(request, response);
                return;
            }
            if (endDate.after(maxDate)) {
                request.setAttribute("error", "End date cannot be more than 1 year from today.");
                request.getRequestDispatcher("create-request.jsp").forward(request, response);
                return;
            }

            insertLeaveRequest(userId, leaveType, startDate, endDate, reason, request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Invalid date format: " + e.getMessage());
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("create-request.jsp");
    }

    private boolean isEmptyOrNull(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void insertLeaveRequest(int userId, String leaveType, Date startDate, Date endDate, 
                                    String reason, HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO LeaveRequests (UserID, LeaveType, StartDate, EndDate, Reason, Status, CreatedDate, ModifiedDate) " +
                         "VALUES (?, ?, ?, ?, ?, 'Pending', ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, leaveType);
                stmt.setDate(3, new java.sql.Date(startDate.getTime()));
                stmt.setDate(4, new java.sql.Date(endDate.getTime()));
                stmt.setString(5, reason);
                Timestamp now = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(6, now);
                stmt.setTimestamp(7, now);
                stmt.executeUpdate();
            }
            request.setAttribute("message", "Leave request submitted successfully.");
            request.getRequestDispatcher("view-requests").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Error submitting request: " + e.getMessage());
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
        }
    }
}