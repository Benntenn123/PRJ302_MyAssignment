<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Tạo Đơn Xin Phép - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.9.0/css/bootstrap-datepicker.min.css">
    <style>
        .datepicker { z-index: 1151 !important; }
    </style>
</head>
<body class="body-home body-home-dashboard">
    <% if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    } %>
    <header class="header-home">
        <nav class="navbar navbar-expand-lg navbar-light bg-light home-navbar">
            <div class="container-fluid">
                <a class="navbar-brand home-logo" href="home.jsp">Helios</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav home-navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link" aria-current="page" href="home.jsp">Trang Chủ</a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-bars"></i>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownMenuLink">
                                <li><a class="dropdown-item active" href="create-request.jsp">Tạo Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="edit-request.jsp">Sửa Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="delete-request.jsp">Xóa Đơn Xin Phép</a></li>
                                <li><a class="dropdown-item" href="approve-request.jsp">Duyệt Đơn Xin Phép</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="logout.jsp">Đăng Xuất</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>

    <main class="home-main home-main-dashboard">
        <section class="function-page-section">
            <div class="container">
                <h2>Chức năng: Tạo Đơn Xin Phép</h2>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
                <% } %>
                <form action="SubmitRequestServlet" method="post">
                    <div class="mb-3">
                        <label for="leaveType" class="form-label">Loại Nghỉ Phép:</label>
                        <select class="form-select" id="leaveType" name="leaveType" required>
                            <option value="">-- Chọn loại nghỉ --</option>
                            <option value="annual">Nghỉ Phép Năm</option>
                            <option value="sick">Nghỉ Ốm</option>
                            <option value="personal">Nghỉ Việc Riêng</option>
                            <option value="maternity">Nghỉ Thai Sản</option>
                            <option value="other">Khác</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="startDate" class="form-label">Ngày Bắt Đầu:</label>
                        <input type="text" class="form-control datepicker" id="startDate" name="startDate" required pattern="\d{2}/\d{2}/\d{4}" title="Định dạng dd/mm/yyyy">
                    </div>
                    <div class="mb-3">
                        <label for="endDate" class="form-label">Ngày Kết Thúc:</label>
                        <input type="text" class="form-control datepicker" id="endDate" name="endDate" required pattern="\d{2}/\d{2}/\d{4}" title="Định dạng dd/mm/yyyy">
                    </div>
                    <div class="mb-3">
                        <label for="reason" class="form-label">Lý Do Nghỉ Phép:</label>
                        <textarea class="form-control" id="reason" name="reason" rows="3" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Gửi Đơn Xin Phép</button>
                </form>
            </div>
        </section>
    </main>

    <footer class="footer-home">
        <p>© 2025 Helios. All rights reserved.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.9.0/js/bootstrap-datepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.9.0/locales/bootstrap-datepicker.vi.min.js"></script>
    <script>
        $(document).ready(function() {
            $('.datepicker').datepicker({
                format: 'dd/mm/yyyy',
                language: 'vi',
                autoclose: true,
                startDate: new Date()
            }).on('changeDate', function(e) {
                var startDate = $('#startDate').datepicker('getDate');
                var endDate = $('#endDate').datepicker('getDate');
                if (endDate && startDate && endDate < startDate) {
                    alert('Ngày kết thúc phải sau ngày bắt đầu!');
                    $(this).val('');
                }
            });
        });
    </script>
</body>
</html>