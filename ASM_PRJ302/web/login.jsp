<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trang Đăng Nhập - Selene</title> <//-- Tiêu đề trang, đã đổi tên công ty thành Selene -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="login-style.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    </head>
    <body class="body-login body-login-visual"> <//-- Class body-login-visual cho nền hình ảnh -->
        <div class="login-container login-container-visual"> <//-- Class login-container-visual cho container hình ảnh -->
            <div class="login-header">
                <div class="logo logo-visual">Selene</div> <//-- Logo "Selene" với class logo-visual -->
                <h2 class="header-visual">Wellcome back</h2> <//-- Tiêu đề chào mừng với class header-visual -->
            </div>
            <form action="LoginServlet" method="post" class="login-form login-form-visual"> <//-- Form đăng nhập với class login-form-visual -->
                <div class="form-group form-group-visual"> <//-- Form group với class form-group-visual -->
                    <label for="username" class="label-visual">Username</label> <//-- Label "Tài khoản" với class label-visual -->
                    <div class="input-group input-group-visual"> <//-- Input group với class input-group-visual -->
                        <span class="input-group-icon input-group-icon-visual"><i class="fas fa-envelope"></i></span> <//-- Icon email (Font Awesome) với class icon-visual -->
                        <input type="text" class="form-control form-control-visual" id="username" name="username" placeholder="Enter your username" required> <//-- Input tài khoản, placeholder và class control-visual -->
                    </div>
                </div>
                <div class="form-group form-group-visual"> <//-- Form group với class form-group-visual -->
                    <label for="password" class="label-visual">Password</label> <//-- Label "Mật Khẩu" với class label-visual -->
                    <div class="input-group input-group-visual"> <//-- Input group với class input-group-visual -->
                        <span class="input-group-icon input-group-icon-visual"><i class="fas fa-key"></i></span> <//-- Icon khóa (Font Awesome) với class icon-visual -->
                        <input type="password" class="form-control form-control-visual" id="password" name="password" placeholder="Enter your password" required> <//-- Input mật khẩu, placeholder và class control-visual -->
                    </div>
                </div>
                <button type="submit" class="btn btn-primary btn-block sign-in-button sign-in-button-visual">Đăng Nhập</button> <//-- Nút "Đăng Nhập" với class button-visual -->
            </form>
            <//-- ĐÃ LOẠI BỎ login-footer -->
        </div>

        <script src="https://kit.fontawesome.com/your_fontawesome_kit.js"></script> <//-- Link Font Awesome (THAY THẾ bằng kit của bạn) -->
    </body>
</html>