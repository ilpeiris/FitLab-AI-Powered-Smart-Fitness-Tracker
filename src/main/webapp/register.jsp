<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register for FitLAB</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <div class="form-container">
        <h2>Register for FitLAB</h2>

        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <form action="register" method="POST">
            <div>
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div>
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div>
                <button type="submit" class="btn">Register</button>
            </div>
        </form>
        <hr style="margin-top: 1.5rem; border: 0; border-top: 1px solid #eee;">
        <p style="text-align: center; margin-top: 1rem;">
            Already have an account? <a href="login.jsp">Login Here</a>
        </p>
    </div>

</body>
</html>