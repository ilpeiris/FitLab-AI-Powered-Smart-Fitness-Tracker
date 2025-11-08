<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register for FitLife</title>

    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <h2>Register for FitLife</h2>

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
            <button type="submit">Register</button>
        </div>
    </form>

    <hr>

    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <p>
        Already have an account? <a href="login.jsp">Login Here</a>
    </p>

</body>
</html>