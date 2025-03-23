<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Delete Leave Request - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="body-home">
    <c:if test="${empty sessionScope.userId}">
        <c:redirect url="login.jsp"/>
    </c:if>

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
                    <a class="nav-link" href="edit-request.jsp"><i class="fas fa-edit"></i> Edit Request</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="delete-request.jsp"><i class="fas fa-trash"></i> Delete Request</a>
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

            <!-- Delete Request Section -->
            <section class="function-page-section">
                <h2>Function: Delete Leave Request</h2>
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

                <!-- List of "Pending" Requests -->
                <h3>List of Pending Requests</h3>
                <c:if test="${not empty pendingRequests}">
                    <table class="table application-table">
                        <thead>
                            <tr>
                                <th>No.</th>
                                <th>Full Name</th>
                                <th>Leave Type</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Reason</th>
                                <th>Status</th>
                                <th>Created Date</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="request" items="${pendingRequests}" varStatus="loop">
                                <tr>
                                    <td>${loop.count}</td>
                                    <td><c:out value="${request.fullName}"/></td>
                                    <td><c:out value="${request.leaveType}"/></td>
                                    <td><fmt:formatDate value="${request.startDate}" pattern="dd/MM/yyyy" /></td>
                                    <td><fmt:formatDate value="${request.endDate}" pattern="dd/MM/yyyy" /></td>
                                    <td><c:out value="${request.reason}"/></td>
                                    <td><c:out value="${request.status}"/></td>
                                    <td><fmt:formatDate value="${request.createdDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/delete-request?action=delete&id=${request.id}" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this request?')">Delete</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </section>
        </div>
    </div>

    <!-- Footer -->
    <footer class="footer-home">
        <p>© 2024 Helios. All rights reserved.</p>
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