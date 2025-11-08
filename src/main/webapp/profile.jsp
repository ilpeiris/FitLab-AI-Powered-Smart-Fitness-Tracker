<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="login.jsp" />
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Profile</title>
    <style>
        nav { background-color: #f0f0f0; padding: 10px; }
        nav a { margin-right: 15px; }
        body { font-family: sans-serif; }
        .form-section { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 20px; }
        .form-section h2 { margin-top: 0; }
        .error { color: red; }
        .success { color: green; }
    </style>
<link rel="stylesheet" href="css/style.css">

</head>
<body>

    <nav>
        <a href="dashboard">Dashboard</a>
        <a href="workouts">Workouts</a>
        <a href="profile">Profile</a>
        <a href="logout">Logout</a>
    </nav>

    <h1>Manage Profile</h1>

    <div class="form-section">
        <h2>Change Password</h2>

        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>
        <c:if test="${not empty success}">
            <p class="success">${success}</p>
        </c:if>

        <form action="profile" method="POST">
            <div>
                <label for="currentPassword">Current Password:</label>
                <input type="password" id="currentPassword" name="currentPassword" required>
            </div>
            <div>
                <label for="newPassword">New Password:</label>
                <input type="password" id="newPassword" name="newPassword" required>
            </div>
            <div>
                <label for="confirmPassword">Confirm New Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            <div>
                <button type="submit">Update Password</button>
            </div>
        </form>
    </div>

</body>
</html>