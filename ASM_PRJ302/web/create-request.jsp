<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.sql.Date" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Create Request - Helios</title>
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
        String today = new Date(System.currentTimeMillis()).toString();
        long oneYearFromNow = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000);
        String maxDate = new Date(oneYearFromNow).toString();
    %>

    <div id="wrapper">
        <nav id="sidebar">
            <div class="logo">Helios</div>
            <a href="${pageContext.request.contextPath}/home" class="nav-link"><i class="fas fa-tachometer-alt"></i> Home</a>
            <a href="${pageContext.request.contextPath}/create-request.jsp" class="nav-link active"><i class="fas fa-plus"></i> Create Request</a>
            <a href="${pageContext.request.contextPath}/view-all-requests" class="nav-link"><i class="fas fa-list"></i> View All Requests</a>
            <a href="${pageContext.request.contextPath}/view-requests" class="nav-link"><i class="fas fa-eye"></i> View Requests</a>
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

            <h2 style="margin: 20px 0;">Create Leave Request</h2>

            <!-- Thêm thông báo lỗi hoặc thành công -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert" style="margin: 20px;">
                    <c:out value="${error}"/>
                </div>
            </c:if>
            <c:if test="${not empty message}">
                <div class="alert alert-success" role="alert" style="margin: 20px;">
                    <c:out value="${message}"/>
                </div>
            </c:if>

            <div style="margin: 20px;">
                <form action="${pageContext.request.contextPath}/submit-request" method="post">
                    <div class="mb-3">
                        <label for="leaveType" class="form-label">Leave Type</label>
                        <select class="form-select" id="leaveType" name="leaveType" required>
                            <option value="">Select Leave Type</option>
                            <option value="Annual Leave">Annual Leave</option>
                            <option value="Sick Leave">Sick Leave</option>
                            <option value="Unpaid Leave">Unpaid Leave</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="startDate" class="form-label">Start Date</label>
                        <input type="date" class="form-control" id="startDate" name="startDate" 
                               min="<%= today %>" max="<%= maxDate %>" required>
                    </div>
                    <div class="mb-3">
                        <label for="endDate" class="form-label">End Date</label>
                        <input type="date" class="form-control" id="endDate" name="endDate" 
                               min="<%= today %>" max="<%= maxDate %>" required>
                    </div>
                    <div class="mb-3">
                        <label for="reason" class="form-label">Reason</label>
                        <textarea class="form-control" id="reason" name="reason" rows="3" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Submit Request</button>
                </form>
            </div>
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