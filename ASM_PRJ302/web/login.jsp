<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page - Selene</title> <!-- Page title, company name changed to Selene -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="login-style.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    </head>
    <body class="body-login body-login-visual"> <!-- Class body-login-visual for image background -->
        <div class="login-container login-container-visual"> <!-- Class login-container-visual for container with image -->
            <div class="login-header">
                <div class="logo logo-visual">Selene</div> <!-- Logo "Selene" with class logo-visual -->
                <h2 class="header-visual">Welcome Back</h2> <!-- Welcome message with class header-visual -->
            </div>

            <% if (request.getAttribute("loginError") != null) { %>
                <div class="alert alert-danger" role="alert">
                    <%= request.getAttribute("loginError") %>
                </div>
            <% } %>

            <form action="LoginServlet" method="post" class="login-form login-form-visual"> <!-- Login form with class login-form-visual -->
                <div class="form-group form-group-visual"> <!-- Form group with class form-group-visual -->
                    <label for="username" class="label-visual">Username</label> <!-- Label "Username" with class label-visual -->
                    <div class="input-group input-group-visual"> <!-- Input group with class input-group-visual -->
                        <span class="input-group-icon input-group-icon-visual"><i class="fas fa-envelope"></i></span> <!-- Email icon (Font Awesome) with class icon-visual -->
                        <input type="text" class="form-control form-control-visual" id="username" name="username" placeholder="Enter your username" required> <!-- Username input, placeholder and class control-visual -->
                    </div>
                </div>
                <div class="form-group form-group-visual"> <!-- Form group with class form-group-visual -->
                    <label for="password" class="label-visual">Password</label> <!-- Label "Password" with class label-visual -->
                    <div class="input-group input-group-visual"> <!-- Input group with class input-group-visual -->
                        <span class="input-group-icon input-group-icon-visual"><i class="fas fa-key"></i></span> <!-- Lock icon (Font Awesome) with class icon-visual -->
                        <input type="password" class="form-control form-control-visual" id="password" name="password" placeholder="Enter your password" required> <!-- Password input, placeholder and class control-visual -->
                    </div>
                </div>
                <button type="submit" class="btn btn-primary btn-block sign-in-button sign-in-button-visual">Login</button> <!-- "Login" button with class button-visual -->
            </form>
            <!-- REMOVED login-footer -->
        </div>

        <script src="https://kit.fontawesome.com/your_fontawesome_kit.js"></script> <!-- Font Awesome link (REPLACE with your kit) -->
    </body>
</html>