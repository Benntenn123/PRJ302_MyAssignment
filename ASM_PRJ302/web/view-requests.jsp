<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Xem Danh Sách Đơn Xin Phép - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="body-home body-home-dashboard">
    <c:if test="${empty sessionScope.userId}">
        <c:redirect url="login.jsp"/>
    </c:if>

    <header class="header-home">
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
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
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/create-request.jsp">Tạo Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/edit-request.jsp">Sửa Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/delete-request.jsp">Xóa Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/approve-request.jsp">Duyệt Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item active" href="${pageContext.request.contextPath}/view-requests">Xem Danh Sách</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Đăng Xuất</a></li>
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
                <h2>Danh Sách Đơn Xin Phép Của Bạn</h2>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${error}"/>
                    </div>
                </c:if>
                <c:if test="${not empty message}">
                    <div class="alert alert-info" role="alert">
                        <c:out value="${message}"/>
                    </div>
                </c:if>

                <c:if test="${empty message}">
                    <table class="table application-table">
                        <thead>
                            <tr>
                                <th>No.</th>
                                <th>Họ và Tên</th>
                                <th>Loại Nghỉ</th>
                                <th>Ngày Bắt Đầu</th>
                                <th>Ngày Kết Thúc</th>
                                <th>Lý Do</th>
                                <th>Trạng Thái</th>
                                <th>Ngày Tạo</th>
                                <th>Ngày Cập Nhật</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="request" items="${leaveRequests}" varStatus="loop">
                                <tr>
                                    <td>${loop.count + (currentPage - 1) * 10}</td>
                                    <td><c:out value="${request.fullName}"/></td>
                                    <td><c:out value="${request.leaveType}"/></td>
                                    <td><fmt:formatDate value="${request.startDate}" pattern="dd/MM/yyyy" /></td>
                                    <td><fmt:formatDate value="${request.endDate}" pattern="dd/MM/yyyy" /></td>
                                    <td><c:out value="${request.reason}"/></td>
                                    <td><c:out value="${request.status}"/></td>
                                    <td><fmt:formatDate value="${request.createdDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                                    <td><fmt:formatDate value="${request.modifiedDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/view-detail?id=${request.id}" class="btn btn-primary btn-sm">Xem Chi Tiết</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <c:if test="${not empty leaveRequests}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="view-requests?page=${currentPage - 1}">Previous</a>
                                    </li>
                                </c:if>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="view-requests?page=${i}">${i}</a>
                                    </li>
                                </c:forEach>
                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="view-requests?page=${currentPage + 1}">Next</a>
                                    </li>
                                </c:if>
                            </ul>
                        </nav>
                    </c:if>
                </c:if>
            </div>
        </section>
    </main>

    <footer class="footer-home">
        <p>© 2024 Helios. All rights reserved.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>