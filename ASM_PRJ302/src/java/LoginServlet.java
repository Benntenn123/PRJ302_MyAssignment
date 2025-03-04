import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* Lấy thông tin đăng nhập từ form */
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            /* Kiểm tra thông tin đăng nhập (ví dụ đơn giản, bạn sẽ thay thế bằng kiểm tra database) */
            if ("admin".equals(username) && "password".equals(password)) {
                /* Đăng nhập thành công, chuyển hướng đến trang home.jsp */
                response.sendRedirect("home.jsp");
            } else {
                /* Đăng nhập thất bại, chuyển hướng lại trang login.jsp */
                response.sendRedirect("login.jsp");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Không xử lý GET request trong servlet này, chỉ xử lý POST từ form đăng nhập
        response.sendRedirect("login.jsp"); // Nếu ai đó truy cập trực tiếp servlet bằng GET, chuyển về trang login
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý đăng nhập";
    }
}