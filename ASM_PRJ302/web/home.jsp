<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Trang Chủ - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="body-home">
    <%
        if (session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
    %>

    <div id="wrapper">
        <!-- Sidebar -->
        <nav id="sidebar">
            <div class="logo">Helios</div>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link active" href="home"><i class="fas fa-tachometer-alt"></i> Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="create-request.jsp"><i class="fas fa-plus"></i> Create Request</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="edit-request.jsp"><i class="fas fa-edit"></i> Edit Request</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="delete-request.jsp"><i class="fas fa-trash"></i> Delete Request</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="approve-request.jsp"><i class="fas fa-check"></i> Approve Request</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="view-requests"><i class="fas fa-eye"></i> View Requests</a>
                </li>
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

            <!-- User Info and Role Cards -->
            <div class="user-info-section">
                <div class="user-info-card">
                    <div class="user-details">
                        <h3>Xin Chào, <%= session.getAttribute("fullName") != null ? session.getAttribute("fullName") : "User" %></h3>
                        <p>Chào mừng bạn đến với Helios - Ứng dụng Quản lý Nghỉ Phép</p>
                    </div>
                </div>
                <div class="user-position-card">
                    <div class="position-details">
                        <h3>Chức Vụ: <%= session.getAttribute("role") != null ? session.getAttribute("role") : "N/A" %></h3>
                        <p>Phòng ban: <%= session.getAttribute("department") != null ? session.getAttribute("department") : "N/A" %></p>
                        <p>Bộ phận: <%= session.getAttribute("section") != null ? session.getAttribute("section") : "N/A" %></p>
                    </div>
                </div>
            </div>

            <!-- Summary Cards -->
            <div class="summary-cards">
                <div class="summary-card pending">
                    <i class="fas fa-clock"></i>
                    <h5>Pending Requests</h5>
                    <p><%= request.getAttribute("pendingCount") != null ? request.getAttribute("pendingCount") : "5" %></p>
                </div>
                <div class="summary-card approved">
                    <i class="fas fa-check-circle"></i>
                    <h5>Approved Requests</h5>
                    <p><%= request.getAttribute("approvedCount") != null ? request.getAttribute("approvedCount") : "12" %></p>
                </div>
                <div class="summary-card rejected">
                    <i class="fas fa-times-circle"></i>
                    <h5>Rejected Requests</h5>
                    <p><%= request.getAttribute("rejectedCount") != null ? request.getAttribute("rejectedCount") : "3" %></p>
                </div>
            </div>

            <!-- Error Message -->
            <%
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
                <div class="alert alert-danger" role="alert" style="margin: 20px;">
                    <%= error %>
                </div>
            <%
                }
            %>
        </div>
    </div>

    <!-- Footer -->
    <footer class="footer-home">
        <p>© 2024 Helios. All rights reserved.</p>
    </footer>

    <!-- JavaScript for Real-Time Date -->
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>