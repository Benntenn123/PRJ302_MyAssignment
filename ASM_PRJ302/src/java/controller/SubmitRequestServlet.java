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
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@WebServlet(name = "SubmitRequestServlet", urlPatterns = {"/SubmitRequestServlet"})
public class SubmitRequestServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String leaveType = request.getParameter("leaveType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = null, endDate = null;

        try {
            startDate = new Date(sdf.parse(startDateStr).getTime());
            endDate = new Date(sdf.parse(endDateStr).getTime());
            if (!sdf.parse(startDateStr).before(sdf.parse(endDateStr))) {
                throw new ParseException("Ngày kết thúc phải sau ngày bắt đầu.", 0);
            }
        } catch (ParseException e) {
            request.setAttribute("error", "Định dạng ngày không hợp lệ (dd/mm/yyyy) hoặc ngày không hợp lệ: " + e.getMessage());
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnection.getConnection();
            String sql = "INSERT INTO LeaveRequests (UserID, LeaveType, StartDate, EndDate, Reason) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, leaveType);
            preparedStatement.setDate(3, startDate);
            preparedStatement.setDate(4, endDate);
            preparedStatement.setString(5, reason);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                response.sendRedirect("home.jsp");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi gửi đơn xin phép.");
                request.getRequestDispatcher("create-request.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi kết nối database: " + e.getMessage());
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
        } finally {
            DatabaseConnection.closeConnection(connection);
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { /* Đã xử lý trong closeConnection */ }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("create-request.jsp");
    }
}