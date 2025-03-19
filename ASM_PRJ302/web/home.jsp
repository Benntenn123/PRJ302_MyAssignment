<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Trang Chủ - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        /* CSS nội tuyến để kiểm tra nhanh */
        body {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            margin: 0;
        }
        .home-main {
            flex: 1 0 auto; /* Cho phép main co giãn và đẩy footer xuống */
        }
        .footer-home {
            flex-shrink: 0; /* Ngăn footer co lại */
        }
    </style>
</head>
<body class="body-home body-home-dashboard">
    <% if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    } %>
    <header class="header-home">
        <nav class="navbar navbar-expand-lg navbar-light bg-light home-navbar">
            <div class="container-fluid">
                <a class="navbar-brand home-logo" href="home">Helios</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav home-navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link active" aria-current="page" href="home">Trang Chủ</a>
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
                                <li><a class="dropdown-item" href="view-requests">Xem Danh Sách Đơn Xin Phép</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="logout.jsp">Đăng Xuất</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>

    <main class="home-main">
        <section class="user-info-section">
            <div class="container">
                <div class="user-info-card">
                    <div class="user-avatar">
                        <i class="fas fa-user-circle fa-5x"></i>
                    </div>
                    <div class="user-details">
                        <h3>Xin Chào, ${fullName}</h3>
                        <p>Chào mừng bạn đến với Helios - Ứng dụng Quản lý Nghỉ Phép</p>
                    </div>
                </div>
            </div>
        </section>

        <section class="user-position-section">
            <div class="container">
                <div class="user-position-card">
                    <div class="position-details">
                        <h3>Chức Vụ: ${role}</h3>
                        <p>Phòng ban: ${department}</p>
                        <p>Bộ phận: ${section}</p>
                    </div>
                </div>
            </div>
        </section>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger" role="alert" style="margin: 20px;">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
    </main>

    <footer class="footer-home">
        <p>© 2024 Helios. All rights reserved.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>