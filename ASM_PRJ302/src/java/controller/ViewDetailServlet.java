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

        int requestId = Integer.parseInt(request.getParameter("id"));
        LeaveRequest leaveRequest = new LeaveRequest();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT lr.LeaveRequestID, u.FullName, lr.LeaveType, lr.StartDate, lr.EndDate, lr.Reason, lr.Status, lr.ModifiedDate, r.RoleName, m.FullName AS ManagerName " +
                         "FROM LeaveRequests lr " +
                         "INNER JOIN Users u ON lr.UserID = u.UserID " +
                         "LEFT JOIN UserRoles ur ON u.UserID = ur.UserID " +
                         "LEFT JOIN Roles r ON ur.RoleID = r.RoleID " +
                         "LEFT JOIN Users m ON u.ManagerID = m.UserID " +
                         "WHERE lr.LeaveRequestID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, requestId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                leaveRequest.setId(resultSet.getInt("LeaveRequestID"));
                leaveRequest.setFullName(resultSet.getString("FullName"));
                leaveRequest.setLeaveType(resultSet.getString("LeaveType"));
                leaveRequest.setStartDate(resultSet.getDate("StartDate"));
                leaveRequest.setEndDate(resultSet.getDate("EndDate"));
                leaveRequest.setReason(resultSet.getString("Reason"));
                leaveRequest.setStatus(resultSet.getString("Status"));
                leaveRequest.setModifiedDate(resultSet.getTimestamp("ModifiedDate"));
                leaveRequest.setRoleName(resultSet.getString("RoleName") != null ? resultSet.getString("RoleName") : "Không xác định");
                leaveRequest.setManagerName(resultSet.getString("ManagerName") != null ? resultSet.getString("ManagerName") : "Không có");
            } else {
                request.setAttribute("error", "Không tìm thấy đơn xin phép.");
            }

            request.setAttribute("leaveRequest", leaveRequest);
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi khi tải chi tiết đơn xin phép: " + e.getMessage());
            request.getRequestDispatcher("view-detail.jsp").forward(request, response);
        } finally {
            DatabaseConnection.closeConnection(connection);
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { /* Đã xử lý */ }
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { /* Đã xử lý */ }
        }
    }
}