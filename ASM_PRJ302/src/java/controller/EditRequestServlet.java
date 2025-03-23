package controller;

import dal.DatabaseConnection;
import model.LeaveRequest;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "EditRequestServlet", urlPatterns = {"/edit-request"})
public class EditRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Chuyển userId từ String sang int
        String userIdStr = (String) session.getAttribute("userId");
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID in session.");
            request.getRequestDispatcher("edit-request.jsp").forward(request, response);
            return;
        }

        // Lấy danh sách các yêu cầu "Pending"
        List<LeaveRequest> pendingRequests = getPendingLeaveRequests(userId);
        if (pendingRequests.isEmpty()) {
            request.setAttribute("message", "You have no requests in Pending status to edit.");
        } else {
            request.setAttribute("pendingRequests", pendingRequests);
        }

        // Kiểm tra nếu người dùng chọn một yêu cầu để chỉnh sửa
        String action = request.getParameter("action");
        String requestId = request.getParameter("id");
        if (action != null && action.equals("edit") && requestId != null) {
            try {
                LeaveRequest leaveRequest = getLeaveRequestById(Integer.parseInt(requestId), userId);
                if (leaveRequest != null && "Pending".equals(leaveRequest.getStatus())) {
                    request.setAttribute("leaveRequest", leaveRequest);
                } else {
                    request.setAttribute("error", "Request does not exist or cannot be edited.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid request ID.");
            }
        }

        // Chuyển hướng đến trang edit-request.jsp
        request.getRequestDispatcher("edit-request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Chuyển userId từ String sang int
        String userIdStr = (String) session.getAttribute("userId");
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID in session.");
            doGet(request, response);
            return;
        }

        // Lấy dữ liệu từ form
        String requestId = request.getParameter("id");
        String leaveType = request.getParameter("leaveType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        // Kiểm tra dữ liệu đầu vào
        if (requestId == null || leaveType == null || leaveType.isEmpty() ||
            startDateStr == null || startDateStr.isEmpty() ||
            endDateStr == null || endDateStr.isEmpty() ||
            reason == null || reason.isEmpty()) {
            request.setAttribute("error", "Please fill in all required fields.");
            doGet(request, response);
            return;
        }

        // Kiểm tra trạng thái của yêu cầu
        LeaveRequest leaveRequest = getLeaveRequestById(Integer.parseInt(requestId), userId);
        if (leaveRequest == null || !"Pending".equals(leaveRequest.getStatus())) {
            request.setAttribute("error", "Request does not exist or cannot be edited.");
            doGet(request, response);
            return;
        }

        // Cập nhật yêu cầu trong cơ sở dữ liệu
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE LeaveRequests SET LeaveType = ?, StartDate = ?, EndDate = ?, Reason = ?, ModifiedDate = GETDATE() " +
                     "WHERE LeaveRequestID = ? AND UserID = ? AND Status = 'Pending'")) {
            stmt.setString(1, leaveType);
            stmt.setDate(2, Date.valueOf(startDateStr));
            stmt.setDate(3, Date.valueOf(endDateStr));
            stmt.setString(4, reason);
            stmt.setInt(5, Integer.parseInt(requestId));
            stmt.setInt(6, userId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                request.setAttribute("message", "Leave request updated successfully!");
                doGet(request, response); // Tải lại trang để hiển thị danh sách và thông báo
            } else {
                request.setAttribute("error", "Unable to update request. Please try again.");
                doGet(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error updating request: " + e.getMessage());
            doGet(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date format: " + e.getMessage());
            doGet(request, response);
        }
    }

    private List<LeaveRequest> getPendingLeaveRequests(int userId) {
        List<LeaveRequest> pendingRequests = new ArrayList<>();
        String sql = "SELECT lr.LeaveRequestID, lr.UserID, lr.LeaveType, lr.StartDate, lr.EndDate, lr.Reason, lr.Status, lr.CreatedDate, lr.ModifiedDate, u.FullName " +
                     "FROM LeaveRequests lr " +
                     "LEFT JOIN Users u ON lr.UserID = u.UserID " +
                     "WHERE lr.UserID = ? AND lr.Status = 'Pending' " +
                     "ORDER BY lr.CreatedDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LeaveRequest leaveRequest = new LeaveRequest();
                leaveRequest.setId(rs.getInt("LeaveRequestID"));
                leaveRequest.setUserId(rs.getInt("UserID"));
                leaveRequest.setFullName(rs.getString("FullName") != null ? rs.getString("FullName") : "Unknown");
                leaveRequest.setLeaveType(rs.getString("LeaveType"));
                leaveRequest.setStartDate(rs.getDate("StartDate"));
                leaveRequest.setEndDate(rs.getDate("EndDate"));
                leaveRequest.setReason(rs.getString("Reason"));
                leaveRequest.setStatus(rs.getString("Status"));
                leaveRequest.setCreatedDate(rs.getTimestamp("CreatedDate"));
                leaveRequest.setModifiedDate(rs.getTimestamp("ModifiedDate"));
                pendingRequests.add(leaveRequest);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving list of Pending requests: " + e.getMessage());
        }
        return pendingRequests;
    }

    private LeaveRequest getLeaveRequestById(int requestId, int userId) {
        LeaveRequest leaveRequest = null;
        String sql = "SELECT lr.LeaveRequestID, lr.UserID, lr.LeaveType, lr.StartDate, lr.EndDate, lr.Reason, lr.Status, lr.CreatedDate, lr.ModifiedDate, u.FullName " +
                     "FROM LeaveRequests lr " +
                     "LEFT JOIN Users u ON lr.UserID = u.UserID " +
                     "WHERE lr.LeaveRequestID = ? AND lr.UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                leaveRequest = new LeaveRequest();
                leaveRequest.setId(rs.getInt("LeaveRequestID"));
                leaveRequest.setUserId(rs.getInt("UserID"));
                leaveRequest.setFullName(rs.getString("FullName") != null ? rs.getString("FullName") : "Unknown");
                leaveRequest.setLeaveType(rs.getString("LeaveType"));
                leaveRequest.setStartDate(rs.getDate("StartDate"));
                leaveRequest.setEndDate(rs.getDate("EndDate"));
                leaveRequest.setReason(rs.getString("Reason"));
                leaveRequest.setStatus(rs.getString("Status"));
                leaveRequest.setCreatedDate(rs.getTimestamp("CreatedDate"));
                leaveRequest.setModifiedDate(rs.getTimestamp("ModifiedDate"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving request details: " + e.getMessage());
        }
        return leaveRequest;
    }
}