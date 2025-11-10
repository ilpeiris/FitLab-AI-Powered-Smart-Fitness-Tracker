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
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <nav>
        <a href="dashboard">Dashboard</a>
        <a href="workouts">Workouts</a>
        <a href="profile">Profile</a>
        <a href="logout">Logout</a>
    </nav>

    


<div class="container">
        <h1 style="color:white">Manage Workouts</h1>

       <c:if test="${not empty sessionScope.aiPrediction}">
    <div class="analysis-box">
        <h3> Workout Saved!</h3>

        <div class="analysis-grid">
            <div class="analysis-item">
                <span class="analysis-item-title">Your Input:</span>
                <span class="analysis-item-data user">${sessionScope.userSelection}</span>
            </div>

            <div class="analysis-item">
                <span class="analysis-item-title">AI Suggestion:</span>
                <span class="analysis-item-data ai">${sessionScope.aiPrediction}</span>
            </div>
        </div>

        <c:choose>
            <c:when test="${sessionScope.aiPrediction == sessionScope.userSelection}">
                <p style="text-align: center; margin-top: 1rem; font-weight: 600; color: #2ada53;">
                    AI result matches your activity type.
                </p>
            </c:when>
            <c:otherwise>
                <p style="text-align: center; margin-top: 1rem; font-weight: 600; color: #e2ae11;">
                    AI result differs from your activity.
                </p>
            </c:otherwise>
        </c:choose>
    </div>


    <c:remove var="aiPrediction" scope="session" />
    <c:remove var="userSelection" scope="session" />
</c:if>




<div class="form-container" style="margin-top: 2rem;max-width: 1000px;">
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
        <input type="number" name="durationMins" id="duration" min="1" required>

        <label>Distance (km):</label>
        <input type="number" name="distanceKm" id="distance" min="0" step="0.1" required>

        <label>Calories Burned:</label>
        <input type="number" name="caloriesBurned" id="calories" min="1" required>

        <div style="margin-top: 10px; height: 20px;">
            <strong id="ai-suggestion" style="color: #007bff; font-size: 0.9rem;"></strong>
        </div>

        <label>Date:</label>
        <input type="date" name="workoutDate" required>
        
        <label>Notes:</label>
        <input type="text" name="notes">

        <button type="submit" class="btn">Add Workout</button>
    </form>
</div>



        <div class="content-box">
            <h2>Your Workout History</h2>
            <form action="workouts" method="GET" style="margin-bottom: 1rem;">
                <label for="type">Filter by Activity:</label>
                <select id="type" name="type">
                    <option value="">All</option>
                    <option value="Running">Running</option>
                    <option value="Cycling">Cycling</option>
                    <option value="Walking">Walking</option>
                    <option value="Gym Workout">Gym Workout</option>
                </select>

                <label for="filterdate">Filter by Date:</label>
                <input type="date" id="filterdate" name="date">

                <button type="submit" class="btn" style="width: auto;">Filter</button>
            </form>
            
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
        </div>
        
        </div>


<script>
    // new, important part:
    // This function will ONLY run after the entire HTML page is loaded and ready.
    document.addEventListener("DOMContentLoaded", function() {

        // Get references to the input fields
        const durationInput = document.getElementById('duration');
        const distanceInput = document.getElementById('distance');
        const caloriesInput = document.getElementById('calories');
        const suggestionSpan = document.getElementById('ai-suggestion');

        // This function wil run API call
        async function fetchPrediction() {
            // Get current values
            const duration = durationInput.value;
            const distance = distanceInput.value;
            const calories = caloriesInput.value;

            console.log('fetchPrediction called - Duration:', duration, 'Distance:', distance, 'Calories:', calories);

            // Only fetch if all three fields have a value (and are not empty strings)
            if (duration !== '' && distance !== '' && calories !== '') {
                const durationNum = parseFloat(duration);
                const distanceNum = parseFloat(distance);
                const caloriesNum = parseFloat(calories);

                console.log('Parsed numbers - durationNum:', durationNum, 'distanceNum:', distanceNum, 'caloriesNum:', caloriesNum);
                console.log('Type check - durationNum:', typeof durationNum, 'distanceNum:', typeof distanceNum, 'caloriesNum:', typeof caloriesNum);

                // Validate that they are valid numbers
                if (!isNaN(durationNum) && durationNum > 0 && !isNaN(distanceNum) && distanceNum >= 0 && !isNaN(caloriesNum) && caloriesNum > 0) {
                    suggestionSpan.innerText = "AI is thinking...";
                    
                    try {
                        // Build the URL for  new API - using string concatenation for safety
                        const url = 'api/predict?duration=' + durationNum + '&distance=' + distanceNum + '&calories=' + caloriesNum;
                        console.log('Fetching URL:', url);
                        console.log('URL constructed with values:', durationNum, distanceNum, caloriesNum);
                        
                        const response = await fetch(url);
                        const data = await response.json();

                        if (response.ok && data.prediction) {
                            suggestionSpan.style.color = "#007bff"; // Blue
                            suggestionSpan.innerText = "AI Suggestion: " + data.prediction;
                        } else {
                            suggestionSpan.style.color = "#dc3545"; // Red
                            suggestionSpan.innerText = "Could not predict.";
                        }
                    } catch (error) {
                        console.error('Error fetching prediction:', error);
                        suggestionSpan.style.color = "#dc3545"; // Red
                        suggestionSpan.innerText = "Error contacting AI.";
                    }
                } else {
                    console.log('Validation failed - not all fields have valid positive numbers');
                    suggestionSpan.innerText = ""; // Clear suggestion if validation fails
                }
            } else {
                console.log('One or more fields are empty');
                suggestionSpan.innerText = ""; // Clear suggestion if fields are empty
            }
        }

        // Add event listeners to run the function whenever the user stops typing
        durationInput.addEventListener('input', fetchPrediction);
        distanceInput.addEventListener('input', fetchPrediction);
        caloriesInput.addEventListener('input', fetchPrediction);

    }); 
</script>

</body>
</html>