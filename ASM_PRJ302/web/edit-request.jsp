<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Edit Leave Request - Helios</title>
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
                    <a class="nav-link active" href="edit-request"><i class="fas fa-edit"></i> Edit Request</a>
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

            <!-- Edit Request Section -->
            <section class="function-page-section">
                <h2>Function: Edit Leave Request</h2>
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
                                <th>Action</th>
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
                                    <td><span class="badge bg-warning text-dark"><c:out value="${request.status}"/></span></td>
                                    <td><fmt:formatDate value="${request.createdDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/edit-request?action=edit&id=${request.id}" class="btn btn-primary btn-sm">Edit</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <c:if test="${empty pendingRequests}">
                    <div class="alert alert-info" role="alert">
                        You have no requests in Pending status to edit.
                    </div>
                </c:if>

                <!-- Edit Request Form (Displayed when a request is selected) -->
                <c:if test="${not empty leaveRequest}">
                    <h3>Edit Leave Request</h3>
                    <form action="${pageContext.request.contextPath}/edit-request" method="post">
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
                        <a href="edit-request" class="btn btn-secondary">Cancel</a>
                    </form>
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