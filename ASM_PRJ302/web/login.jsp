<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Trang Đăng Nhập - Helios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/home-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
</head>
<body class="body-login">
    <div class="login-container">
        <div class="login-header">
            <div class="logo">Helios</div>
            <h2>Chào mừng trở lại</h2>
        </div>

        <%
            String loginError = (String) request.getAttribute("loginError");
            if (loginError != null) {
        %>
            <div class="alert alert-danger" role="alert">
                <%= loginError %>
            </div>
        <%
            }
        %>

        <form action="LoginServlet" method="post" class="login-form">
            <div class="form-group">
                <label for="username">Tài khoản</label>
                <div class="input-group">
                    <span class="input-group-icon"><i class="fas fa-envelope"></i></span>
                    <input type="text" class="form-control" id="username" name="username" placeholder="Nhập tài khoản của bạn" required>
                </div>
            </div>
            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <div class="input-group">
                    <span class="input-group-icon"><i class="fas fa-key"></i></span>
                    <input type="password" class="form-control" id="password" name="password" placeholder="Nhập mật khẩu của bạn" required>
                </div>
            </div>
            <button type="submit" class="sign-in-button">Đăng Nhập</button>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>