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
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "ViewAllDetailServlet", urlPatterns = {"/view-all-detail"})
public class ViewAllDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy userId từ session
        int userId = (Integer) session.getAttribute("userId");

        // Lấy role của người dùng hiện tại
        int userRole = getUserRole(userId);
        if (userRole == -1) {
            request.setAttribute("error", "Unable to determine your role.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Lấy danh sách UserID của tất cả cấp dưới (trực tiếp và gián tiếp)
        Set<Integer> subordinateIds = getSubordinateIds(userId, userRole);
        if (subordinateIds.isEmpty()) {
            request.setAttribute("error", "You have no subordinates to view requests for.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Lấy requestId từ tham số
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("error", "Invalid request ID.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid request ID format.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Lấy thông tin chi tiết của đơn
        LeaveRequest leaveRequest = getLeaveRequestById(requestId);
        if (leaveRequest == null) {
            request.setAttribute("error", "Leave request not found.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Kiểm tra xem đơn có thuộc về cấp dưới không
        if (!subordinateIds.contains(leaveRequest.getUserId())) {
            request.setAttribute("error", "You are not authorized to view this request.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Truyền dữ liệu vào request
        request.setAttribute("leaveRequest", leaveRequest);
        request.getRequestDispatcher("view-all-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy userId từ session
        int userId = (Integer) session.getAttribute("userId");

        // Lấy role của người dùng hiện tại
        int userRole = getUserRole(userId);
        if (userRole == -1) {
            request.setAttribute("error", "Unable to determine your role.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Lấy danh sách UserID của tất cả cấp dưới (trực tiếp và gián tiếp)
        Set<Integer> subordinateIds = getSubordinateIds(userId, userRole);
        if (subordinateIds.isEmpty()) {
            request.setAttribute("error", "You have no subordinates to view requests for.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Lấy requestId từ tham số
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("error", "Invalid request ID.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid request ID format.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Lấy thông tin chi tiết của đơn
        LeaveRequest leaveRequest = getLeaveRequestById(requestId);
        if (leaveRequest == null) {
            request.setAttribute("error", "Leave request not found.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Kiểm tra xem đơn có thuộc về cấp dưới không
        if (!subordinateIds.contains(leaveRequest.getUserId())) {
            request.setAttribute("error", "You are not authorized to view this request.");
            request.getRequestDispatcher("view-all-requests").forward(request, response);
            return;
        }

        // Kiểm tra trạng thái của đơn
        if (!"Pending".equals(leaveRequest.getStatus())) {
            request.setAttribute("error", "This request cannot be approved or rejected because it is not in Pending status.");
            request.setAttribute("leaveRequest", leaveRequest);
            request.getRequestDispatcher("view-all-detail.jsp").forward(request, response);
            return;
        }

        // Xử lý hành động approve hoặc reject
        String action = request.getParameter("action");
        if (action != null) {
            if ("approve".equals(action)) {
                updateRequestStatus(requestId, "Approved");
                request.setAttribute("message", "Leave request has been approved successfully.");
            } else if ("reject".equals(action)) {
                updateRequestStatus(requestId, "Rejected");
                request.setAttribute("message", "Leave request has been rejected successfully.");
            }
            // Chuyển hướng về danh sách sau khi xử lý
            response.sendRedirect("view-all-requests");
            return;
        }

        // Nếu không có hành động, hiển thị lại trang chi tiết
        request.setAttribute("leaveRequest", leaveRequest);
        request.getRequestDispatcher("view-all-detail.jsp").forward(request, response);
    }

    // Lấy role của người dùng hiện tại
    private int getUserRole(int userId) {
        String sql = "SELECT r.RoleID " +
                     "FROM UserRoles ur " +
                     "INNER JOIN Roles r ON ur.RoleID = r.RoleID " +
                     "WHERE ur.UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("RoleID");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user role: " + e.getMessage());
        }
        return -1; // Trả về -1 nếu không tìm thấy role
    }

    // Lấy danh sách UserID của tất cả cấp dưới (trực tiếp và gián tiếp) dựa trên role
    private Set<Integer> getSubordinateIds(int userId, int userRole) {
        Set<Integer> subordinateIds = new HashSet<>();

        // Nếu userRole không phải là role cao (ví dụ: role 1), không có cấp dưới
        if (userRole <= 1) {
            return subordinateIds;
        }

        // Lấy danh sách tất cả người dùng có role thấp hơn userRole
        String sql = "SELECT u.UserID " +
                     "FROM Users u " +
                     "INNER JOIN UserRoles ur ON u.UserID = ur.UserID " +
                     "INNER JOIN Roles r ON ur.RoleID = r.RoleID " +
                     "WHERE r.RoleID < ? AND u.UserID != ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userRole);
            stmt.setInt(2, userId); // Loại trừ chính người dùng hiện tại
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subordinateIds.add(rs.getInt("UserID"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving subordinate IDs: " + e.getMessage());
        }
        return subordinateIds;
    }

    // Lấy thông tin chi tiết của đơn nghỉ phép
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

    // Cập nhật trạng thái của đơn nghỉ phép
    private void updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE LeaveRequests SET Status = ?, ModifiedDate = ? WHERE LeaveRequestID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, requestId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
        }
    }
}