<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Home - Helios</title>
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
    %>

    <div id="wrapper">
        <!-- Sidebar -->
        <nav id="sidebar">
            <div class="logo">Helios</div>
            <a href="${pageContext.request.contextPath}/home" class="nav-link active">
                <i class="fas fa-tachometer-alt"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/create-request.jsp" class="nav-link">
                <i class="fas fa-plus"></i> Create Request
            </a>
            <a href="${pageContext.request.contextPath}/edit-request.jsp" class="nav-link">
                <i class="fas fa-edit"></i> Edit Request
            </a>
            <a href="${pageContext.request.contextPath}/delete-request.jsp" class="nav-link">
                <i class="fas fa-trash"></i> Delete Request
            </a>
            <a href="${pageContext.request.contextPath}/approve-request.jsp" class="nav-link">
                <i class="fas fa-check"></i> Approve Request
            </a>
            <a href="${pageContext.request.contextPath}/view-requests" class="nav-link">
                <i class="fas fa-eye"></i> View Requests
            </a>
            <a href="${pageContext.request.contextPath}/logout" class="nav-link">
                <i class="fas fa-sign-out-alt"></i> Logout
            </a>
        </nav>

        <!-- Main Content -->
        <div id="main-content">
            <!-- Top Bar -->
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

            <!-- User Info Section -->
            <div class="user-info-section">
                <div class="user-info-card">
                    <div class="user-details">
                        <h3>Hello, <%= session.getAttribute("fullName") != null ? session.getAttribute("fullName") : "User" %></h3>
                        <p>Welcome to Helios - Leave Management Application</p>
                    </div>
                </div>
                <div class="user-position-card">
                    <div class="position-details">
                        <h3>Position: <%= session.getAttribute("role") != null ? session.getAttribute("role") : "N/A" %></h3>
                        <p>Department: <%= session.getAttribute("department") != null ? session.getAttribute("department") : "N/A" %></p>
                        <p>Section: <%= session.getAttribute("section") != null ? session.getAttribute("section") : "N/A" %></p>
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
        <p>© 2025 Helios. All rights reserved.</p>
    </footer>

    <!-- JavaScript for Real-Time Date -->
    <script>
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

        document.addEventListener('DOMContentLoaded', displayCurrentDate);
    </script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>