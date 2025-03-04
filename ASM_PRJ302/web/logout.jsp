<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // **SERVER-SIDE LOGIC - XỬ LÝ ĐĂNG XUẤT (HỦY SESSION)**
    HttpSession sessionLogout = request.getSession(false); // Lấy session hiện tại, không tạo mới nếu không có
    if (sessionLogout != null) {
        sessionLogout.invalidate(); // Hủy session hiện tại, đăng xuất người dùng
    }

    // **CLIENT-SIDE REDIRECT - CHUYỂN HƯỚNG VỀ TRANG LOGIN**
    String loginPageURL = "login.jsp"; // **ĐIỀU CHỈNH ĐƯỜNG DẪN TRANG LOGIN NẾU CẦN**
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Đăng Xuất - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="body-home body-home-dashboard">
    <header class="header-home">
        <nav class="navbar navbar-expand-lg navbar-light bg-light home-navbar">
            <div class="container-fluid">
                <a class="navbar-brand home-logo" href="home.jsp">Helios</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav home-navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link" aria-current="page" href="home.jsp">Trang Chủ</a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-bars"></i>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownMenuLink">
                                <li><a class="dropdown-item" href="create-request.jsp">Tạo Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="edit-request.jsp">Sửa Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="delete-request.jsp">Xóa Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="approve-request.jsp">Duyệt Đơn Xin Phép</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item active" href="logout.jsp">Đăng Xuất</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>

    <main class="home-main home-main-dashboard">
        <section class="function-page-section">
            <div class="container">
                <h2>Đăng Xuất Thành Công</h2> <//-- Tiêu đề trang thông báo đăng xuất thành công -->
                <p>Bạn đã đăng xuất khỏi hệ thống thành công. <br>
                Bạn sẽ được tự động chuyển hướng về trang đăng nhập trong giây lát...</p> <//-- Thông báo cho người dùng -->
                 <//-- **KHÔNG CẦN NÚT ĐĂNG XUẤT NỮA VÌ ĐÃ TỰ ĐỘNG CHUYỂN HƯỚNG** -->
                </div>
        </section>
    </main>

    <footer class="footer-home">
        <p>&copy; 2024 Helios. All rights reserved.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // **CLIENT-SIDE REDIRECT - CHUYỂN HƯỚNG SAU 2 GIÂY (ví dụ)**
        setTimeout(function() {
            window.location.href = '<%=loginPageURL%>'; // Chuyển hướng đến trang login.jsp
        }, 2000); // Thời gian chờ 2 giây (2000 milliseconds)
    </script>
</body>
</html>