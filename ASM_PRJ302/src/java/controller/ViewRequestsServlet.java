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
import java.util.List;

@WebServlet(name = "ViewRequestsServlet", urlPatterns = {"/view-requests"})
public class ViewRequestsServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int offset = (page - 1) * PAGE_SIZE;
        List<LeaveRequest> leaveRequests = getLeaveRequestsByUserId(userId, offset, request);

        if (leaveRequests.isEmpty() && page == 1) {
            request.setAttribute("message", "You have no leave requests.");
        } else {
            request.setAttribute("leaveRequests", leaveRequests);
        }

        request.getRequestDispatcher("view-requests.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private List<LeaveRequest> getLeaveRequestsByUserId(int userId, int offset, HttpServletRequest request) {
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        String sql = "SELECT lr.LeaveRequestID, lr.UserID, lr.LeaveType, lr.StartDate, lr.EndDate, lr.Reason, lr.Status, lr.CreatedDate, lr.ModifiedDate, u.FullName, r.RoleName, m.FullName AS ManagerName " +
                     "FROM LeaveRequests lr " +
                     "LEFT JOIN Users u ON lr.UserID = u.UserID " +
                     "LEFT JOIN UserRoles ur ON u.UserID = ur.UserID " +
                     "LEFT JOIN Roles r ON ur.RoleID = r.RoleID " +
                     "LEFT JOIN Users m ON u.ManagerID = m.UserID " +
                     "WHERE lr.UserID = ? " +
                     "ORDER BY lr.CreatedDate DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, offset);
            stmt.setInt(3, PAGE_SIZE);
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
            String countSql = "SELECT COUNT(*) FROM LeaveRequests WHERE UserID = ?";
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                countStmt.setInt(1, userId);
                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    int totalRecords = countRs.getInt(1);
                    int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
                    if (totalPages == 0) totalPages = 1;
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("currentPage", offset / PAGE_SIZE + 1);
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error retrieving request list: " + e.getMessage());
        }
        return leaveRequests;
    }
}