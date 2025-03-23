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
    } %>

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
                    <a class="nav-link active" href="view-requests"><i class="fas fa-eye"></i> View Requests</a>
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

                            <!-- Hiển thị nút "Edit" nếu trạng thái là "Pending" -->
                            <c:if test="${leaveRequest.status == 'Pending'}">
                                <a href="${pageContext.request.contextPath}/view-detail?action=edit&id=${leaveRequest.id}" class="btn btn-primary">Edit</a>
                            </c:if>
                            <a href="view-requests" class="btn btn-secondary">Back</a>
                        </div>
                    </div>

                    <!-- Form chỉnh sửa (hiển thị nếu action=edit) -->
                    <c:if test="${action == 'edit' && leaveRequest.status == 'Pending'}">
                        <h3 class="mt-4">Edit Leave Request</h3>
                        <form action="${pageContext.request.contextPath}/view-detail" method="post">
                            <input type="hidden" name="id" value="${leaveRequest.id}">
                            <div class="mb-3">
                                <label for="leaveType" class="form-label">Leave Type</label>
                                <select class="form-select" id="leaveType" name="leaveType" required>
                                    <option value="">Select leave type</option>
                                    <option value="Annual Leave" ${leaveRequest.leaveType == 'Annual Leave' ? 'selected' : ''}>Annual Leave</option>
                                    <option value="Sick Leave" ${leaveRequest.leaveType == 'Sick Leave' ? 'selected' : ''}>Sick Leave</option>
                                    <option value="Unpaid Leave" ${leaveRequest.leaveType == 'Unpaid Leave' ? 'selected' : ''}>Unpaid Leave</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="startDate" class="form-label">Start Date</label>
                                <input type="date" class="form-control" id="startDate" name="startDate" 
                                       value="<fmt:formatDate value='${leaveRequest.startDate}' pattern='yyyy-MM-dd'/>" required>
                            </div>
                            <div class="mb-3">
                                <label for="endDate" class="form-label">End Date</label>
                                <input type="date" class="form-control" id="endDate" name="endDate" 
                                       value="<fmt:formatDate value='${leaveRequest.endDate}' pattern='yyyy-MM-dd'/>" required>
                            </div>
                            <div class="mb-3">
                                <label for="reason" class="form-label">Reason</label>
                                <textarea class="form-control" id="reason" name="reason" rows="3" required><c:out value="${leaveRequest.reason}"/></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary">Update Request</button>
                            <a href="view-detail?id=${leaveRequest.id}" class="btn btn-secondary">Cancel</a>
                        </form>
                    </c:if>
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

        // Validate dates before form submission
        document.querySelector('form')?.addEventListener('submit', function(e) {
            const startDate = new Date(document.getElementById('startDate').value);
            const endDate = new Date(document.getElementById('endDate').value);
            if (startDate > endDate) {
                e.preventDefault();
                alert('Start date must be less than or equal to end date.');
            }
        });
    </script>
</body>
</html>