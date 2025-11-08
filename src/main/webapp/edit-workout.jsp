<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="login.jsp" />
</c:if>

<c:if test="${empty workout}">
    <c:redirect url="workouts" />
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Workout</title>
    <style>
        nav { background-color: #f0f0f0; padding: 10px; }
        nav a { margin-right: 15px; }
        body { font-family: sans-serif; }
        .form-section { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 20px; }
        .form-section h2 { margin-top: 0; }
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

    <h1>Edit Workout</h1>

    <div class="form-section">
        <form action="edit-workout" method="POST">

            <input type="hidden" name="workoutId" value="${workout.workoutId}">

            <label>Activity Type:</label>
            <select name="activityType" required>
                <option value="Running" <c:if test="${workout.activityType == 'Running'}">selected</c:if>>Running</option>
                <option value="Cycling" <c:if test="${workout.activityType == 'Cycling'}">selected</c:if>>Cycling</option>
                <option value="Walking" <c:if test="${workout.activityType == 'Walking'}">selected</c:if>>Walking</option>
                <option value="Gym Workout" <c:if test="${workout.activityType == 'Gym Workout'}">selected</c:if>>Gym Workout</option>
            </select>

            <label>Duration (mins):</label>
            <input type="number" name="durationMins" min="1" required value="${workout.durationMins}">

            <label>Distance (km):</label>
            <input type="number" name="distanceKm" min="0" step="0.1" required value="${workout.distanceKm}">

            <label>Calories Burned:</label>
            <input type="number" name="caloriesBurned" min="1" required value="${workout.caloriesBurned}">

            <label>Date:</label>
            <input type="date" name="workoutDate" required value="${workout.workoutDate}">

            <label>Notes:</label>
            <input type="text" name="notes" value="${workout.notes}">

            <button type="submit">Save Changes</button>
            <a href="workouts">Cancel</a>
        </form>
    </div>

</body>
</html>