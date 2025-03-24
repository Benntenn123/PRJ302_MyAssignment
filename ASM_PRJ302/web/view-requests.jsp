<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>View My Requests - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700&family=Montserrat:wght@800&display=swap" rel="stylesheet">
</head>
<body>
    <%
        if (session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Get user role
        String role = (String) session.getAttribute("role");
        boolean isManager = role != null && !role.equals("Employee");
    %>

    <div id="wrapper">
        <nav id="sidebar">
            <div class="logo">Helios</div>
            <a href="${pageContext.request.contextPath}/home" class="nav-link"><i class="fas fa-tachometer-alt"></i> Home</a>
            <a href="${pageContext.request.contextPath}/create-request.jsp" class="nav-link"><i class="fas fa-plus"></i> Create Request</a>
            <a href="${pageContext.request.contextPath}/view-all-requests" class="nav-link"><i class="fas fa-list"></i> View All Requests</a>
            <a href="${pageContext.request.contextPath}/view-requests" class="nav-link active"><i class="fas fa-eye"></i> View Requests</a>
            <% if (isManager) { %>
            <a href="${pageContext.request.contextPath}/agenda" class="nav-link">
                <i class="fas fa-calendar-alt"></i> Team Agenda
            </a>
            <% } %>
            <a href="${pageContext.request.contextPath}/logout" class="nav-link"><i class="fas fa-sign-out-alt"></i> Logout</a>
        </nav>

        <div id="main-content">
            <div class="top-bar">
                <div class="date" id="current-date"></div>
                <div class="search-bar">
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-search"></i></span>
                        <input type="text" class="form-control" placeholder="Search">
                    </div>
                </div>
                <div class="welcome">
                    Welcome, <%= session.getAttribute("fullName") != null ? session.getAttribute("fullName") : "User" %>!
                    <img src="https://via.placeholder.com/30" alt="User Avatar">
                </div>
            </div>

            <h2 style="margin: 20px 0;">My Leave Requests</h2>

            <c:if test="${not empty message}">
                <div class="alert alert-success" role="alert" style="margin: 20px;">
                    <c:out value="${message}"/>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert" style="margin: 20px;">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <div style="margin: 20px;">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Employee Name</th>
                            <th>Leave Type</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Reason</th>
                            <th>Status</th>
                            <th>Created Date</th>
                            <th>Modified Date</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="request" items="${leaveRequests}" varStatus="loop">
                            <tr>
                                <td>${loop.count + (currentPage - 1) * 10}</td>
                                <td><c:out value="${request.fullName}"/></td>
                                <td><c:out value="${request.leaveType}"/></td>
                                <td><fmt:formatDate value="${request.startDate}" pattern="dd/MM/yyyy"/></td>
                                <td><fmt:formatDate value="${request.endDate}" pattern="dd/MM/yyyy"/></td>
                                <td><c:out value="${request.reason}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${request.status == 'Pending'}">
                                            <span class="badge bg-warning text-dark">${request.status}</span>
                                        </c:when>
                                        <c:when test="${request.status == 'Approved'}">
                                            <span class="badge bg-success">${request.status}</span>
                                        </c:when>
                                        <c:when test="${request.status == 'Rejected'}">
                                            <span class="badge bg-danger">${request.status}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${request.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><fmt:formatDate value="${request.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                <td><fmt:formatDate value="${request.modifiedDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/view-detail?id=${request.id}" class="btn btn-primary btn-sm">View Details</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation" style="margin: 20px;">
                    <ul class="pagination">
                        <c:if test="${currentPage > 1}">
                            <li class="page-item">
                                <a class="page-link" href="${pageContext.request.contextPath}/view-requests?page=${currentPage - 1}" aria-label="Previous">
                                    <span aria-hidden="true">«</span>
                                </a>
                            </li>
                        </c:if>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/view-requests?page=${i}">${i}</a>
                            </li>
                        </c:forEach>
                        <c:if test="${currentPage < totalPages}">
                            <li class="page-item">
                                <a class="page-link" href="${pageContext.request.contextPath}/view-requests?page=${currentPage + 1}" aria-label="Next">
                                    <span aria-hidden="true">»</span>
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>

    <footer class="footer-home">
        <p>© 2025 Helios. All rights reserved.</p>
    </footer>

    <script>
        function displayCurrentDate() {
            const dateElement = document.getElementById("current-date");
            if (dateElement) {
                const today = new Date();
                const options = { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };
                dateElement.textContent = today.toLocaleDateString('en-US', options);
            }
        }
        document.addEventListener('DOMContentLoaded', displayCurrentDate);
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>