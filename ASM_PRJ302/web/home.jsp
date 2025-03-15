<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Trang Chủ - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/home-style.css"> <//-- Liên kết đến file CSS riêng cho trang home -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body class="body-home body-home-dashboard">
    <header class="header-home">
        <nav class="navbar navbar-expand-lg navbar-light bg-light home-navbar">
            <div class="container-fluid">
                <a class="navbar-brand home-logo" href="home.jsp">Helios</a> <//-- Logo "Helios" - TO HƠN, ĐẬM HƠN, MÀU #F2C641 -->
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav home-navbar-nav ms-auto"> <//-- Class 'ms-auto' đẩy menu sang phải -->
                        <li class="nav-item">
                            <a class="nav-link active" aria-current="page" href="home.jsp">Trang Chủ</a>
                        </li>
                        <li class="nav-item dropdown"> <//-- Dropdown menu 3 gạch - CHO 4 CHỨC NĂNG -->
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-bars"></i> <//-- Icon 3 gạch (Font Awesome) -->
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownMenuLink"> <//-- Class 'dropdown-menu-end' để menu xổ xuống bên phải -->
                                <li><a class="dropdown-item" href="create-request.jsp">Tạo Đơn Xin Phép</a></li>
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
        <section class="user-info-section">
            <div class="container">
                <div class="user-info-card">
                    <div class="user-avatar">
                        <i class="fas fa-user-circle fa-5x"></i>
                    </div>
                    <div class="user-details">
                        <h3>Xin Chào, ${fullName}</h3> <//-- **THAY THẾ BẰNG TÊN NGƯỜI DÙNG ĐĂNG NHẬP TỪ SERVER-SIDE** -->
                        <p>Chào mừng bạn đến với Helios - Ứng dụng Quản lý Nghỉ Phép</p>
                    </div>
                </div>
            </div>
        </section>

        <section class="user-position-section"> <//-- SECTION THÔNG TIN CHỨC VỤ, PHÒNG BAN - THÊM SECTION MỚI -->
            <div class="container">
                <div class="user-position-card">
                    <div class="position-details">
                        <h3>Chức Vụ: ${role}</h3> <//-- **THAY THẾ BẰNG CHỨC VỤ NGƯỜI DÙNG TỪ SERVER-SIDE** -->
                        <p>Phòng ban: ${department}</p> <//-- **THAY THẾ BẰNG PHÒNG BAN NGƯỜI DÙNG TỪ SERVER-SIDE** -->
                        <p>Bộ phận: ${section}</p> <//-- **THAY THẾ BẰNG BỘ PHẬN NGƯỜI DÙNG TỪ SERVER-SIDE** -->
                    </div>
                </div>
            </div>
        </section>

        <section class="application-table-section">
            <div class="container">
                <h2>Danh Sách Đơn Xin Phép</h2>
                <table class="table application-table">
                    <thead>
                        <tr>
                            <th>No.</th>
                            <th>Họ và Tên</th>
                            <th>Trạng Thái Đơn</th>
                            <th></th> <//-- Cột trống cho nút "Xem Chi Tiết" -->
                        </tr>
                    </thead>
                    <tbody>
                        <//-- **DỮ LIỆU BẢNG SẼ ĐƯỢC ĐỔ VÀO ĐÂY TỪ SERVER-SIDE (JSP/Servlet)** -->
                        <tr>
                            <td>1</td>
                            <td>Nguyễn Văn B</td>
                            <td>Đang chờ duyệt</td>
                            <td><a href="#">Xem Chi Tiết</a></td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td>Trần Thị C</td>
                            <td>Đã duyệt</td>
                            <td><a href="#">Xem Chi Tiết</a></td>
                        </tr>
                        <tr>
                            <td>3</td>
                            <td>Lê Văn D</td>
                            <td>Từ chối</td>
                            <td><a href="#">Xem Chi Tiết</a></td>
                        </tr>
                        <//-- **... THÊM CÁC HÀNG DỮ LIỆU ĐƠN XIN PHÉP ...** -->
                    </tbody>
                </table>
            </div>
        </section>
          <//-- CÓ THỂ CHÈN THÊM CÁC SECTION NỘI DUNG KHÁC CỦA TRANG CHỦ VÀO ĐÂY (ví dụ: thống kê, biểu đồ...) -->
    </main>

    <footer class="footer-home">
        <p>&copy; 2024 Helios. All rights reserved.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>