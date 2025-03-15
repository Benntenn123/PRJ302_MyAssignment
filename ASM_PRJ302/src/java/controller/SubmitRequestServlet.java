package controller;

import dal.DatabaseConnection; // Nếu bạn để DatabaseConnection trong package dal
// import DatabaseConnection; // Nếu bạn để DatabaseConnection ở default package
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
            response.sendRedirect("login.jsp"); // Chuyển hướng nếu chưa đăng nhập
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String leaveType = request.getParameter("leaveType"); // Lấy loại nghỉ phép
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");
        // String attachment = request.getParameter("attachment"); // Bạn sẽ cần xử lý file upload khác

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Định dạng ngày tháng trong form của bạn
        Date startDate = null;
        Date endDate = null;

        try {
            startDate = new Date(sdf.parse(startDateStr).getTime());
            endDate = new Date(sdf.parse(endDateStr).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            request.setAttribute("error", "Định dạng ngày không hợp lệ (dd/mm/yyyy).");
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "INSERT INTO LeaveRequests (UserID, StartDate, EndDate, Reason) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, startDate);
            preparedStatement.setDate(3, endDate);
            preparedStatement.setString(4, reason);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                response.sendRedirect("home.jsp"); // Chuyển hướng về trang chủ sau khi gửi thành công
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi gửi đơn xin phép.");
                request.getRequestDispatcher("create-request.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi kết nối database: " + e.getMessage());
            request.getRequestDispatcher("create-request.jsp").forward(request, response);
        } finally {
            DatabaseConnection.closeConnection(connection);
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("create-request.jsp"); // Nếu người dùng truy cập trực tiếp bằng GET
    }
}