<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Leave Request Details - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="body-home">
    <% if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Get user role
    String role = (String) session.getAttribute("role");
    boolean isManager = role != null && !role.equals("Employee");
    %>

    <div id="wrapper">
        <!-- Sidebar -->
        <nav id="sidebar">
            <div class="logo">Helios</div>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link" href="home"><i class="fas fa-tachometer-alt"></i> Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="create-request.jsp"><i class="fas fa-plus"></i> Create Request</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="delete-request.jsp"><i class="fas fa-trash"></i> Delete Request</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="approve-request.jsp"><i class="fas fa-check"></i> Approve Request</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="view-all-requests"><i class="fas fa-list"></i> View All Requests</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="view-requests"><i class="fas fa-eye"></i> View Requests</a>
                </li>
                <% if (isManager) { %>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/agenda">
                        <i class="fas fa-calendar-alt"></i> Team Agenda
                    </a>
                </li>
                <% } %>
                <li class="nav-item">
                    <a class="nav-link" href="logout"><i class="fas fa-sign-out-alt"></i> Logout</a>
                </li>
            </ul>
        </nav>

        <!-- Main Content -->
        <div id="main-content">
            <!-- Top Bar (Includes Date) -->
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
                    <img src="https://via.placeholder.com/40" alt="User Avatar">
                </div>
            </div>

            <!-- View Detail Section -->
            <section class="application-table-section">
                <h2>Leave Request Details</h2>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${error}"/>
                    </div>
                </c:if>
                <c:if test="${not empty message}">
                    <div class="alert alert-success" role="alert">
                        <c:out value="${message}"/>
                    </div>
                </c:if>

                <c:if test="${not empty leaveRequest}">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Request Information</h5>
                            <p><strong>Request ID:</strong> ${leaveRequest.id}</p>
                            <p><strong>Full Name:</strong> ${leaveRequest.fullName}</p>
                            <p><strong>Leave Type:</strong> ${leaveRequest.leaveType}</p>
                            <p><strong>Start Date:</strong> <fmt:formatDate value="${leaveRequest.startDate}" pattern="dd/MM/yyyy" /></p>
                            <p><strong>End Date:</strong> <fmt:formatDate value="${leaveRequest.endDate}" pattern="dd/MM/yyyy" /></p>
                            <p><strong>Reason:</strong> ${leaveRequest.reason}</p>
                            <p><strong>Status:</strong> ${leaveRequest.status}</p>
                            <p><strong>Last Updated:</strong> <fmt:formatDate value="${leaveRequest.modifiedDate}" pattern="dd/MM/yyyy HH:mm:ss" /></p>
                            <p><strong>Role:</strong> ${leaveRequest.roleName}</p>
                            <p><strong>Manager:</strong> ${leaveRequest.managerName}</p>

                            <!-- Hiển thị nút "Approve" và "Reject" nếu trạng thái là "Pending" -->
                            <c:if test="${leaveRequest.status == 'Pending'}">
                                <form action="${pageContext.request.contextPath}/view-all-detail" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="${leaveRequest.id}">
                                    <input type="hidden" name="action" value="approve">
                                    <button type="submit" class="btn btn-success" onclick="return confirm('Are you sure you want to approve this request?');">Approve</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/view-all-detail" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="${leaveRequest.id}">
                                    <input type="hidden" name="action" value="reject">
                                    <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to reject this request?');">Reject</button>
                                </form>
                            </c:if>
                            <a href="view-all-requests" class="btn btn-secondary">Back</a>
                        </div>
                    </div>
                </c:if>
            </section>
        </div>
    </div>

    <!-- Footer -->
    <footer class="footer-home">
        <p>© 2025 Helios. All rights reserved.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Function to display the current date
        function displayCurrentDate() {
            const dateElement = document.getElementById("current-date");
            if (dateElement) {
                const today = new Date();
                const options = { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };
                dateElement.textContent = today.toLocaleDateString('en-US', options);
            } else {
                console.error("Element with ID 'current-date' not found.");
            }
        }

        // Call the function when the page loads
        document.addEventListener('DOMContentLoaded', displayCurrentDate);
    </script>
</body>
</html>