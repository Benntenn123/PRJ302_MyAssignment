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

@WebServlet(name = "ViewAllRequestsServlet", urlPatterns = {"/view-all-requests"})
public class ViewAllRequestsServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

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
                request.getRequestDispatcher("home").forward(request, response);
                return;
            }
        } else {
            request.setAttribute("error", "User ID not found in session.");
            request.getRequestDispatcher("home").forward(request, response);
            return;
        }

        // Lấy role của người dùng hiện tại
        int userRole = getUserRole(userId);
        if (userRole == -1) {
            request.setAttribute("error", "Unable to determine your role.");
            request.getRequestDispatcher("home").forward(request, response);
            return;
        }

        // Lấy danh sách UserID của tất cả cấp dưới (trực tiếp và gián tiếp) dựa trên role
        Set<Integer> subordinateIds = getSubordinateIds(userId, userRole);

        if (subordinateIds.isEmpty()) {
            request.setAttribute("error", "You have no subordinates to view requests for.");
            request.setAttribute("allRequests", new ArrayList<LeaveRequest>());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
            request.getRequestDispatcher("view-all-requests.jsp").forward(request, response);
            return;
        }

        // Lấy số trang hiện tại
        String pageStr = request.getParameter("page");
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Tính toán offset
        int offset = (page - 1) * PAGE_SIZE;

        // Lấy tổng số yêu cầu của cấp dưới
        int totalRequests = getTotalRequestCount(subordinateIds);

        // Tính tổng số trang
        int totalPages = (int) Math.ceil((double) totalRequests / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        // Lấy danh sách yêu cầu cho trang hiện tại
        List<LeaveRequest> allRequests = getRequestsForPage(subordinateIds, offset, PAGE_SIZE);

        // Truyền dữ liệu vào request
        request.setAttribute("allRequests", allRequests);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("view-all-requests.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
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

    // Lấy tổng số yêu cầu của tất cả cấp dưới
    private int getTotalRequestCount(Set<Integer> userIds) {
        if (userIds.isEmpty()) return 0;

        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) " +
            "FROM LeaveRequests lr " +
            "INNER JOIN Users u ON lr.UserID = u.UserID " +
            "WHERE lr.UserID IN ("
        );
        for (int i = 0; i < userIds.size(); i++) {
            sql.append("?");
            if (i < userIds.size() - 1) sql.append(",");
        }
        sql.append(")");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            for (Integer userId : userIds) {
                stmt.setInt(index++, userId);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total request count: " + e.getMessage());
        }
        return 0;
    }

    // Lấy danh sách yêu cầu cho trang hiện tại
    private List<LeaveRequest> getRequestsForPage(Set<Integer> userIds, int offset, int pageSize) {
        List<LeaveRequest> requests = new ArrayList<>();
        if (userIds.isEmpty()) return requests;

        StringBuilder sql = new StringBuilder(
            "SELECT lr.LeaveRequestID, lr.UserID, u.FullName, lr.LeaveType, lr.StartDate, lr.EndDate, lr.Reason, lr.Status, lr.CreatedDate, lr.ModifiedDate " +
            "FROM LeaveRequests lr " +
            "INNER JOIN Users u ON lr.UserID = u.UserID " +
            "WHERE lr.UserID IN ("
        );
        for (int i = 0; i < userIds.size(); i++) {
            sql.append("?");
            if (i < userIds.size() - 1) sql.append(",");
        }
        sql.append(") ORDER BY lr.CreatedDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            for (Integer userId : userIds) {
                stmt.setInt(index++, userId);
            }
            stmt.setInt(index++, offset);
            stmt.setInt(index, pageSize);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LeaveRequest request = new LeaveRequest();
                request.setId(rs.getInt("LeaveRequestID"));
                request.setUserId(rs.getInt("UserID"));
                request.setFullName(rs.getString("FullName"));
                request.setLeaveType(rs.getString("LeaveType"));
                request.setStartDate(rs.getDate("StartDate"));
                request.setEndDate(rs.getDate("EndDate"));
                request.setReason(rs.getString("Reason"));
                request.setStatus(rs.getString("Status"));
                request.setCreatedDate(rs.getTimestamp("CreatedDate"));
                request.setModifiedDate(rs.getTimestamp("ModifiedDate"));
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving requests: " + e.getMessage());
        }
        return requests;
    }
}