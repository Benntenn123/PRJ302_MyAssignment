<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Team Agenda - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700&family=Montserrat:wght@800&display=swap" rel="stylesheet">
    <style>
        .agenda-container {
            margin: 20px;
            overflow-x: auto;
            background-color: white;
            border-radius: 15px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        
        .agenda-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .agenda-table th, .agenda-table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
            font-size: 14px;
        }
        
        .agenda-table th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
        
        .agenda-employee {
            text-align: left;
            font-weight: 600;
            background-color: #f8f9fa;
        }
        
        .weekend {
            background-color: #f8f9fa;
        }
        
        .working-day {
            background-color: #c6efc6;
        }
        
        .leave-day {
            background-color: #ffcccb;
            font-weight: bold;
        }
        
        .filter-section {
            margin: 20px;
            padding: 15px;
            background-color: white;
            border-radius: 15px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }
        
        .legend {
            margin: 20px;
            padding: 15px;
            background-color: white;
            border-radius: 15px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        
        .legend-item {
            display: flex;
            align-items: center;
            margin-right: 20px;
        }
        
        .color-box {
            display: inline-block;
            width: 16px;
            height: 16px;
            margin-right: 5px;
            border: 1px solid #ddd;
        }
        
        .green-box {
            background-color: #c6efc6;
        }
        
        .red-box {
            background-color: #ffcccb;
        }
        
        .white-box {
            background-color: #f8f9fa;
        }
    </style>
</head>
<body>
    <%
        if (session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Check if user has appropriate role
        String role = (String) session.getAttribute("role");
        if (role == null || role.equals("Employee")) {
            response.sendRedirect("home");
            return;
        }
    %>

    <div id="wrapper">
        <!-- Sidebar -->
        <nav id="sidebar">
            <div class="logo">Helios</div>
            <a href="${pageContext.request.contextPath}/home" class="nav-link">
                <i class="fas fa-tachometer-alt"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/create-request.jsp" class="nav-link">
                <i class="fas fa-plus"></i> Create Request
            </a>
            <a href="${pageContext.request.contextPath}/view-all-requests" class="nav-link">
                <i class="fas fa-list"></i> View All Requests
            </a>
            <a href="${pageContext.request.contextPath}/view-requests" class="nav-link">
                <i class="fas fa-eye"></i> View Requests
            </a>
            <a href="${pageContext.request.contextPath}/agenda" class="nav-link active">
                <i class="fas fa-calendar-alt"></i> Team Agenda
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

            <!-- Page Title -->
            <h2 style="margin: 20px 0 10px 20px;">Team Attendance Agenda</h2>

            <!-- Filter Section -->
            <div class="filter-section">
                <form action="${pageContext.request.contextPath}/agenda" method="get" class="row g-3">
                    <div class="col-md-4">
                        <select class="form-select" id="department" name="department">
                            <option value="">All Departments</option>
                            <c:forEach var="dept" items="${departments}">
                                <option value="${dept}" ${department == dept ? 'selected' : ''}>${dept}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" id="month" name="month">
                            <c:forEach var="i" begin="1" end="12">
                                <option value="${i}" ${month == i ? 'selected' : ''}><fmt:formatDate value="${monthNames[i-1]}" pattern="MMMM"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" id="year" name="year">
                            <c:forEach var="i" begin="${currentYear-1}" end="${currentYear+1}">
                                <option value="${i}" ${year == i ? 'selected' : ''}>${i}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="fas fa-filter me-2"></i>Apply Filter
                        </button>
                    </div>
                </form>
            </div>

            <!-- Messages -->
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

            <!-- Agenda Table -->
            <div class="agenda-container">
                <c:if test="${empty teamMembers}">
                    <div class="alert alert-info mb-0">No team members found for the selected criteria.</div>
                </c:if>
                <c:if test="${not empty teamMembers}">
                    <table class="agenda-table">
                        <thead>
                            <tr>
                                <th class="agenda-employee">Employee</th>
                                <c:forEach var="dayHeader" items="${dayHeaders}">
                                    <th class="${dayHeader.weekend ? 'weekend' : ''}">
                                        ${dayHeader.day}<br>
                                        <small>${dayHeader.dayOfWeek}</small>
                                    </th>
                                </c:forEach>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="member" items="${teamMembers}">
                                <tr>
                                    <td class="agenda-employee">
                                        ${member.fullName}
                                    </td>
                                    <c:forEach var="day" items="${member.attendanceData}">
                                        <td class="${day.weekend ? 'weekend' : day.onLeave ? 'leave-day' : 'working-day'}">
                                            <c:if test="${day.onLeave}">
                                                ${day.leaveType.charAt(0)}
                                            </c:if>
                                        </td>
                                    </c:forEach>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
            
            <!-- Legend -->
            <div class="legend">
                <div class="legend-item"><span class="color-box green-box"></span> Working Day</div>
                <div class="legend-item"><span class="color-box white-box"></span> Weekend</div>
                <div class="legend-item"><span class="color-box red-box"></span> Leave Day</div>
                <div class="legend-item">A: Annual Leave, S: Sick Leave, U: Unpaid Leave</div>
            </div>
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
            }
        }

        document.addEventListener('DOMContentLoaded', displayCurrentDate);
    </script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
