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

@WebServlet(name = "DeleteRequestServlet", urlPatterns = {"/delete-request"})
public class DeleteRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");
        String requestId = request.getParameter("id");

        if ("delete".equals(action) && requestId != null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "DELETE FROM LeaveRequests WHERE LeaveRequestID = ? AND UserID = ? AND Status = 'Pending'")) {
                stmt.setInt(1, Integer.parseInt(requestId));
                stmt.setInt(2, userId);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    request.setAttribute("message", "Request deleted successfully.");
                } else {
                    request.setAttribute("error", "Unable to delete request. The request does not exist or is not in Pending status.");
                }
            } catch (SQLException e) {
                request.setAttribute("error", "Error deleting request: " + e.getMessage());
            }
        }

        List<LeaveRequest> pendingRequests = getPendingLeaveRequests(userId);
        if (pendingRequests.isEmpty()) {
            request.setAttribute("message", "You have no requests in Pending status to delete.");
        } else {
            request.setAttribute("pendingRequests", pendingRequests);
        }

        request.getRequestDispatcher("delete-request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
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
            System.err.println("Error retrieving pending requests: " + e.getMessage());
        }
        return pendingRequests;
    }
}