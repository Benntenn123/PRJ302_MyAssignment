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

    private static final int PAGE_SIZE = 5; // Số bản ghi trên mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy tham số lọc và phân trang
        String statusFilter = request.getParameter("status") != null ? request.getParameter("status") : "";
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;

        List<LeaveRequest> leaveRequests = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int totalRecords = 0;

        try {
            connection = DatabaseConnection.getConnection();

            // Đếm tổng số bản ghi để tính phân trang
            String countSql = "SELECT COUNT(*) as total FROM LeaveRequests lr INNER JOIN Users u ON lr.UserID = u.UserID";
            if (!statusFilter.isEmpty()) {
                countSql += " WHERE lr.Status = ?";
            }
            preparedStatement = connection.prepareStatement(countSql);
            if (!statusFilter.isEmpty()) {
                preparedStatement.setString(1, statusFilter);
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalRecords = resultSet.getInt("total");
            }
            resultSet.close();
            preparedStatement.close();

            // Truy vấn danh sách đơn với phân trang
            int offset = (page - 1) * PAGE_SIZE;
            String sql = "SELECT lr.LeaveRequestID, u.FullName, lr.Status, lr.StartDate, lr.EndDate, lr.ModifiedDate " +
                         "FROM LeaveRequests lr " +
                         "INNER JOIN Users u ON lr.UserID = u.UserID";
            if (!statusFilter.isEmpty()) {
                sql += " WHERE lr.Status = ?";
            }
            sql += " ORDER BY lr.ModifiedDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            preparedStatement = connection.prepareStatement(sql);
            int paramIndex = 1;
            if (!statusFilter.isEmpty()) {
                preparedStatement.setString(paramIndex++, statusFilter);
            }
            preparedStatement.setInt(paramIndex++, offset);
            preparedStatement.setInt(paramIndex, PAGE_SIZE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LeaveRequest leaveRequest = new LeaveRequest();
                leaveRequest.setId(resultSet.getInt("LeaveRequestID"));
                leaveRequest.setFullName(resultSet.getString("FullName"));
                leaveRequest.setStatus(resultSet.getString("Status"));
                leaveRequest.setStartDate(resultSet.getDate("StartDate"));
                leaveRequest.setEndDate(resultSet.getDate("EndDate"));
                leaveRequest.setModifiedDate(resultSet.getTimestamp("ModifiedDate"));
                leaveRequests.add(leaveRequest);
            }

            // Tính tổng số trang
            int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

            // Truyền dữ liệu vào request
            request.setAttribute("leaveRequests", leaveRequests);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("view-requests.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi khi tải danh sách đơn xin phép: " + e.getMessage());
            request.getRequestDispatcher("view-requests.jsp").forward(request, response);
        } finally {
            DatabaseConnection.closeConnection(connection);
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { /* Đã xử lý */ }
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { /* Đã xử lý */ }
        }
    }
}