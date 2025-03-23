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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ApproveRequestServlet", urlPatterns = {"/approve-request"})
public class ApproveRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String userIdStr = (String) session.getAttribute("userId");
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID in session: " + userIdStr);
            request.getRequestDispatcher("approve-request.jsp").forward(request, response);
            return;
        }

        // Kiểm tra xem người dùng có cấp dưới hay không
        Set<Integer> subordinates = getAllSubordinates(userId);
        if (subordinates.isEmpty()) {
            request.setAttribute("message", "You do not have any subordinates to approve requests for.");
            request.getRequestDispatcher("approve-request.jsp").forward(request, response);
            return;
        }

        // Lấy danh sách yêu cầu của cấp dưới
        List<LeaveRequest> leaveRequests = getLeaveRequestsForSubordinates(subordinates, request);

        if (leaveRequests.isEmpty()) {
            request.setAttribute("message", "No leave requests found for your subordinates.");
        } else {
            request.setAttribute("leaveRequests", leaveRequests);
        }

        request.getRequestDispatcher("approve-request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String userIdStr = (String) session.getAttribute("userId");
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID in session: " + userIdStr);
            request.getRequestDispatcher("approve-request-detail").forward(request, response);
            return;
        }

        String requestIdStr = request.getParameter("id");
        String action = request.getParameter("action");
        if (requestIdStr == null || action == null) {
            request.setAttribute("error", "Invalid request parameters.");
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

        // Kiểm tra quyền phê duyệt
        LeaveRequest leaveRequest = getLeaveRequestById(requestId);
        if (leaveRequest == null) {
            request.setAttribute("error", "Leave request not found.");
            doGet(request, response);
            return;
        }

        Set<Integer> subordinates = getAllSubordinates(userId);
        if (!subordinates.contains(leaveRequest.getUserId())) {
            request.setAttribute("error", "You do not have permission to approve/reject this request.");
            doGet(request, response);
            return;
        }

        if (!"Pending".equals(leaveRequest.getStatus())) {
            request.setAttribute("error", "Only requests in Pending status can be approved or rejected.");
            doGet(request, response);
            return;
        }

        // Cập nhật trạng thái của yêu cầu
        String newStatus = action.equals("approve") ? "Approved" : "Rejected";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE LeaveRequests SET Status = ?, ModifiedDate = GETDATE() WHERE LeaveRequestID = ? AND Status = 'Pending'")) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                session.setAttribute("message", "Leave request " + newStatus.toLowerCase() + " successfully!");
                response.sendRedirect("approve-request");
            } else {
                request.setAttribute("error", "Unable to update request status. Please try again.");
                doGet(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error updating request status: " + e.getMessage());
            doGet(request, response);
        }
    }

    private List<LeaveRequest> getLeaveRequestsForSubordinates(Set<Integer> subordinates, HttpServletRequest request) {
        List<LeaveRequest> leaveRequests = new ArrayList<>();

        // Chuyển Set thành chuỗi để sử dụng trong truy vấn SQL
        StringBuilder userIds = new StringBuilder();
        for (Integer id : subordinates) {
            if (userIds.length() > 0) {
                userIds.append(",");
            }
            userIds.append(id);
        }

        if (userIds.length() == 0) {
            return leaveRequests; // Không có cấp dưới
        }

        String sql = "SELECT lr.LeaveRequestID, lr.UserID, lr.LeaveType, lr.StartDate, lr.EndDate, lr.Reason, lr.Status, lr.CreatedDate, lr.ModifiedDate, u.FullName, r.RoleName, m.FullName AS ManagerName " +
                     "FROM LeaveRequests lr " +
                     "LEFT JOIN Users u ON lr.UserID = u.UserID " +
                     "LEFT JOIN UserRoles ur ON u.UserID = ur.UserID " +
                     "LEFT JOIN Roles r ON ur.RoleID = r.RoleID " +
                     "LEFT JOIN Users m ON u.ManagerID = m.UserID " +
                     "WHERE lr.UserID IN (" + userIds.toString() + ") " +
                     "ORDER BY lr.CreatedDate DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        int page = 1;
        int pageSize = 10; // Số yêu cầu trên mỗi trang
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            page = 1;
        }
        int offset = (page - 1) * pageSize;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offset);
            stmt.setInt(2, pageSize);
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
                leaveRequest.setRoleName(rs.getString("RoleName") != null ? rs.getString("RoleName") : "Unknown");
                leaveRequest.setManagerName(rs.getString("ManagerName") != null ? rs.getString("ManagerName") : "None");
                leaveRequests.add(leaveRequest);
            }

            // Tính tổng số trang
            String countSql = "SELECT COUNT(*) FROM LeaveRequests WHERE UserID IN (" + userIds.toString() + ")";
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    int totalRecords = countRs.getInt(1);
                    int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("currentPage", page);
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error retrieving request list: " + e.getMessage());
        }
        return leaveRequests;
    }

    private LeaveRequest getLeaveRequestById(int requestId) {
        LeaveRequest leaveRequest = null;
        String sql = "SELECT lr.LeaveRequestID, lr.UserID, u.FullName, lr.LeaveType, lr.StartDate, lr.EndDate, lr.Reason, lr.Status, lr.ModifiedDate, r.RoleName, m.FullName AS ManagerName " +
                     "FROM LeaveRequests lr " +
                     "INNER JOIN Users u ON lr.UserID = u.UserID " +
                     "LEFT JOIN UserRoles ur ON u.UserID = u.UserID " +
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

    // Lấy danh sách tất cả cấp dưới (trực tiếp và gián tiếp) của một người dùng
    private Set<Integer> getAllSubordinates(int userId) {
        Set<Integer> subordinates = new HashSet<>();
        String sql = "WITH RECURSIVE Subordinates AS (" +
                     "    SELECT UserID, ManagerID " +
                     "    FROM Users " +
                     "    WHERE ManagerID = ? " +
                     "    UNION ALL " +
                     "    SELECT u.UserID, u.ManagerID " +
                     "    FROM Users u " +
                     "    INNER JOIN Subordinates s ON u.ManagerID = s.UserID " +
                     ") SELECT UserID FROM Subordinates";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subordinates.add(rs.getInt("UserID"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving subordinates: " + e.getMessage());
        }
        return subordinates;
    }
}