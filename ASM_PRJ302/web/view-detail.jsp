<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Chi Tiết Đơn Xin Phép - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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
                            <a class="nav-link" href="home">Trang Chủ</a>
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

    <main class="home-main home-home-dashboard">
        <section class="application-table-section">
            <div class="container">
                <h2>Chi Tiết Đơn Xin Phép</h2>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
                <% } else { %>
                    <c:if test="${not empty leaveRequest}">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Thông Tin Đơn</h5>
                                <p><strong>Mã Đơn:</strong> ${leaveRequest.id}</p>
                                <p><strong>Họ và Tên:</strong> ${leaveRequest.fullName}</p>
                                <p><strong>Loại Nghỉ:</strong> ${leaveRequest.leaveType}</p>
                                <p><strong>Ngày Bắt Đầu:</strong> <fmt:formatDate value="${leaveRequest.startDate}" pattern="dd/MM/yyyy" /></p>
                                <p><strong>Ngày Kết Thúc:</strong> <fmt:formatDate value="${leaveRequest.endDate}" pattern="dd/MM/yyyy" /></p>
                                <p><strong>Lý Do:</strong> ${leaveRequest.reason}</p>
                                <p><strong>Trạng Thái:</strong> ${leaveRequest.status}</p>
                                <p><strong>Thời Gian Cập Nhật:</strong> <fmt:formatDate value="${leaveRequest.modifiedDate}" pattern="dd/MM/yyyy HH:mm:ss" /></p>
                                <a href="view-requests" class="btn btn-secondary">Quay Lại</a>
                            </div>
                        </div>
                    </c:if>
                <% } %>
            </div>
        </section>
    </main>

    <footer class="footer-home">
        <p>© 2024 Helios. All rights reserved.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>