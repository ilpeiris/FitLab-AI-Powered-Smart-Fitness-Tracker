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
    <title>My Workouts</title>
    <style>
        /* Simple styling */
        nav { background-color: #f0f0f0; padding: 10px; }
        nav a { margin-right: 15px; }
        body { font-family: sans-serif; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        .form-section { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 20px; }
        .form-section h2 { margin-top: 0; }
    </style>
</head>
<body>

    <nav>
        <a href="dashboard">Dashboard</a>
        <a href="workouts">Workouts</a>
        <a href="profile">Profile</a>
        <a href="logout">Logout</a>
    </nav>

    <h1>Manage Workouts</h1>

    <div class="form-section">
        <h2>Filter Workouts</h2>
        <form action="workouts" method="GET">
            <label for="type">Activity Type:</label>
            <select id="type" name="type">
                <option value="">All</option>
                <option value="Running">Running</option>
                <option value="Cycling">Cycling</option>
                <option value="Walking">Walking</option>
                <option value="Gym Workout">Gym Workout</option>
            </select>

            <label for="date">Date:</label>
            <input type="date" id="date" name="date">

            <button type="submit">Filter</button>
        </form>
    </div>

    <div class="form-section">
        <h2>Add New Workout</h2>
        <form action="workouts" method="POST">
            <label>Activity Type:</label>
            <select name="activityType" required>
                <option value="Running">Running</option>
                <option value="Cycling">Cycling</option>
                <option value="Walking">Walking</option>
                <option value="Gym Workout">Gym Workout</option>
            </select>

            <label>Duration (mins):</label>
            <input type="number" name="durationMins" min="1" required>

            <label>Distance (km):</label>
            <input type="number" name="distanceKm" min="0" step="0.1" required>

            <label>Calories Burned:</label>
            <input type="number" name="caloriesBurned" min="1" required>

            <label>Date:</label>
            <input type="date" name="workoutDate" required>

            <label>Notes:</label>
            <input type="text" name="notes">

            <button type="submit">Add Workout</button>
        </form>
    </div>

    <h2>Your Workout History</h2>
    <table>
        <thead>
            <tr>
                <th>Date</th>
                <th>Type</th>
                <th>Duration (mins)</th>
                <th>Distance (km)</th>
                <th>Calories</th>
                <th>Notes</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="workout" items="${workoutList}">
                <tr>
                    <td>${workout.workoutDate}</td>
                    <td>${workout.activityType}</td>
                    <td>${workout.durationMins}</td>
                    <td>${workout.distanceKm}</td>
                    <td>${workout.caloriesBurned}</td>
                    <td>${workout.notes}</td>
                    <td>
                        <a href="edit-workout?id=${workout.workoutId}">Edit</a>
                        <a href="workouts?action=delete&id=${workout.workoutId}">Delete</a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty workoutList}">
                <tr>
                    <td colspan="7">You have not logged any workouts yet.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

</body>
</html>