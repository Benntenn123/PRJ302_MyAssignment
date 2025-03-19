<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Xem Danh Sách Đơn Xin Phép - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/home-style.css">
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
                <a class="navbar-brand home-logo" href="${pageContext.request.contextPath}/home">Helios</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav home-navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/home">Trang Chủ</a>
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
                                <li><a class="dropdown-item active" href="${pageContext.request.contextPath}/view-requests">Xem Danh Sách Đơn Xin Phép</a></li>
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
                <h2>Danh Sách Đơn Xin Phép</h2>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
                <% } %>

                <!-- Bộ lọc theo trạng thái -->
                <form action="${pageContext.request.contextPath}/view-requests" method="get" class="mb-3">
                    <div class="row">
                        <div class="col-md-3">
                            <select name="status" class="form-select" onchange="this.form.submit()">
                                <option value="">Tất cả</option>
                                <option value="Đang chờ duyệt" ${statusFilter == 'Đang chờ duyệt' ? 'selected' : ''}>Đang chờ duyệt</option>
                                <option value="Đã duyệt" ${statusFilter == 'Đã duyệt' ? 'selected' : ''}>Đã duyệt</option>
                                <option value="Từ chối" ${statusFilter == 'Từ chối' ? 'selected' : ''}>Từ chối</option>
                            </select>
                        </div>
                    </div>
                </form>

                <table class="table application-table">
                    <thead>
                        <tr>
                            <th>No.</th>
                            <th>Họ và Tên</th>
                            <th>Ngày Bắt Đầu</th>
                            <th>Ngày Kết Thúc</th>
                            <th>Trạng Thái Đơn</th>
                            <th>Thời Gian Cập Nhật</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="request" items="${leaveRequests}" varStatus="loop">
                            <tr>
                                <td>${loop.count + (currentPage - 1) * PAGE_SIZE}</td>
                                <td>${request.fullName}</td>
                                <td><fmt:formatDate value="${request.startDate}" pattern="dd/MM/yyyy" /></td>
                                <td><fmt:formatDate value="${request.endDate}" pattern="dd/MM/yyyy" /></td>
                                <td>${request.status}</td>
                                <td><fmt:formatDate value="${request.modifiedDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                                <td><a href="${pageContext.request.contextPath}/view-detail?id=${request.id}" class="btn btn-primary btn-sm">Xem Chi Tiết</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty leaveRequests}">
                            <tr><td colspan="7">Không có đơn xin phép nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>

                <!-- Điều hướng phân trang -->
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-center">
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/view-requests?page=${i}${statusFilter != '' ? '&status=' : ''}${statusFilter}">${i}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </div>
        </section>
    </main>

    <footer class="footer-home">
        <p>© 2024 Helios. All rights reserved.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>