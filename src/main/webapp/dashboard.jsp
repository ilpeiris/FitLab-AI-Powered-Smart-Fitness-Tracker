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
    <title>Your Dashboard</title>
    
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <style>
        /* Simple styling for our navigation */
        nav { background-color: #f0f0f0; padding: 10px; }
        nav a { margin-right: 15px; }
        .stats { display: flex; gap: 20px; }
        .stat-box { border: 1px solid #ccc; padding: 10px; }

        /* Give the chart a specific size */
        .chart-container {
            width: 80%;
            max-width: 900px;
            margin-top: 20px;
        }
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

    <h1>Welcome, ${sessionScope.user.username}!</h1>

    <h2>Your Activity Summary</h2>

    <div class="stats">
        <div class="stat-box">
            <strong>Total Workouts</strong>
            <p>${totalWorkouts}</p>
        </div>
        <div class="stat-box">
            <strong>Total Calories Burned</strong>
            <p>${totalCalories}</p>
        </div>
        <div class="stat-box">
            <strong>Total Distance (km)</strong>
            <p>${totalDistance}</p>
        </div>
    </div>


<div class="chart-container" id="chart-container">
    <h3>Workout Progress (Calories Burned)</h3>
    <canvas id="workoutChart"></canvas>
</div>



<script>
       
        document.addEventListener("DOMContentLoaded", function() {

            
            const ctx = document.getElementById('workoutChart').getContext('2d');

            let workoutsData = [];
            <c:forEach var="workout" items="${workoutList}">
                workoutsData.push({
                    date: "${workout.workoutDate}",
                    calories: ${workout.caloriesBurned}
                });
            </c:forEach>

           
            workoutsData.sort((a, b) => new Date(a.date) - new Date(b.date));

           
            const labels = workoutsData.map(w => w.date);
            const dataPoints = workoutsData.map(w => w.calories);

            
            const workoutChart = new Chart(ctx, {
                type: 'line', 
                data: {
                    labels: labels, 
                    datasets: [{
                        label: 'Calories Burned',
                        data: dataPoints, 
                        borderColor: 'rgba(75, 192, 192, 1)',
                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                        fill: true,
                        tension: 0.1 
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: 'Date'
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: 'Calories Burned'
                            },
                            beginAtZero: true
                        }
                    }
                }
            });
        });
    </script>
    </body>
</html>