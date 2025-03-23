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

@WebServlet(name = "ViewDetailServlet", urlPatterns = {"/view-detail"})
public class ViewDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String requestIdStr = request.getParameter("id");
        if (requestIdStr == null) {
            request.setAttribute("error", "Request ID is missing.");
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid request ID.");
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);
            return;
        }

        // Xử lý yêu cầu xóa
        String action = request.getParameter("action");
        if (action != null && action.equals("delete")) {
            handleDeleteRequest(request, response, requestId);
            return;
        }

        // Lấy thông tin chi tiết của yêu cầu
        LeaveRequest leaveRequest = getLeaveRequestById(requestId);
        if (leaveRequest == null) {
            request.setAttribute("error", "Leave request not found.");
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);
            return;
        }

        // Đặt leaveRequest vào request attribute
        request.setAttribute("leaveRequest", leaveRequest);

        // Kiểm tra nếu người dùng muốn chỉnh sửa (action=edit)
        if (action != null && action.equals("edit")) {
            if (!"Pending".equals(leaveRequest.getStatus())) {
                request.setAttribute("error", "Only requests in Pending status can be edited.");
            } else {
                request.setAttribute("action", "edit");
            }
        }

        request.getRequestDispatcher("view-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            request.setAttribute("error", "Invalid user ID in session: " + userIdStr);
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);
            return;
        }

        // Lấy dữ liệu từ form
        String requestIdStr = request.getParameter("id");
        String leaveType = request.getParameter("leaveType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        // Kiểm tra dữ liệu đầu vào
        if (requestIdStr == null || leaveType == null || leaveType.isEmpty() ||
            startDateStr == null || startDateStr.isEmpty() ||
            endDateStr == null || endDateStr.isEmpty() ||
            reason == null || reason.isEmpty()) {
            request.setAttribute("error", "Please fill in all required fields.");
            doGet(request, response);
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid request ID: " + requestIdStr);
            doGet(request, response);
            return;
        }

        // Kiểm tra trạng thái của yêu cầu
        LeaveRequest leaveRequest = getLeaveRequestById(requestId);
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
            stmt.setInt(5, requestId);
            stmt.setInt(6, userId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                request.setAttribute("message", "Leave request updated successfully!");
                doGet(request, response); // Tải lại trang để hiển thị thông tin đã cập nhật
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

    private void handleDeleteRequest(HttpServletRequest request, HttpServletResponse response, int requestId)
            throws ServletException, IOException {
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
            request.setAttribute("error", "Invalid user ID in session: " + userIdStr);
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);
            return;
        }

        // Kiểm tra trạng thái của yêu cầu
        LeaveRequest leaveRequest = getLeaveRequestById(requestId);
        if (leaveRequest == null || !"Pending".equals(leaveRequest.getStatus())) {
            request.setAttribute("error", "Request does not exist or cannot be deleted. Only Pending requests can be deleted.");
            request.setAttribute("leaveRequest", leaveRequest);
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);
            return;
        }

        // Xóa yêu cầu trong cơ sở dữ liệu
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM LeaveRequests WHERE LeaveRequestID = ? AND UserID = ? AND Status = 'Pending'")) {
            stmt.setInt(1, requestId);
            stmt.setInt(2, userId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                // Chuyển hướng về view-requests.jsp với thông báo thành công
                session.setAttribute("message", "Leave request deleted successfully!");
                response.sendRedirect("view-requests");
            } else {
                request.setAttribute("error", "Unable to delete request. Please try again.");
                request.setAttribute("leaveRequest", leaveRequest);
                request.getRequestDispatcher("view-detail.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error deleting request: " + e.getMessage());
            request.setAttribute("leaveRequest", leaveRequest);
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);
        }
    }

    private LeaveRequest getLeaveRequestById(int requestId) {
        LeaveRequest leaveRequest = null;
        String sql = "SELECT lr.LeaveRequestID, lr.UserID, u.FullName, lr.LeaveType, lr.StartDate, lr.EndDate, lr.Reason, lr.Status, lr.ModifiedDate, r.RoleName, m.FullName AS ManagerName " +
                     "FROM LeaveRequests lr " +
                     "INNER JOIN Users u ON lr.UserID = u.UserID " +
                     "LEFT JOIN UserRoles ur ON u.UserID = ur.UserID " +
                     "LEFT JOIN Roles r ON ur.RoleID = r.RoleID " +
                     "LEFT JOIN Users m ON u.ManagerID = m.UserID " +
                     "WHERE lr.LeaveRequestID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                leaveRequest = new LeaveRequest();
                leaveRequest.setId(rs.getInt("LeaveRequestID"));
                leaveRequest.setUserId(rs.getInt("UserID"));
                leaveRequest.setFullName(rs.getString("FullName"));
                leaveRequest.setLeaveType(rs.getString("LeaveType"));
                leaveRequest.setStartDate(rs.getDate("StartDate"));
                leaveRequest.setEndDate(rs.getDate("EndDate"));
                leaveRequest.setReason(rs.getString("Reason"));
                leaveRequest.setStatus(rs.getString("Status"));
                leaveRequest.setModifiedDate(rs.getTimestamp("ModifiedDate"));
                leaveRequest.setRoleName(rs.getString("RoleName") != null ? rs.getString("RoleName") : "Unknown");
                leaveRequest.setManagerName(rs.getString("ManagerName") != null ? rs.getString("ManagerName") : "None");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving request details: " + e.getMessage());
        }
        return leaveRequest;
    }
}