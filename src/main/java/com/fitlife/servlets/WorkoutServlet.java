package com.fitlife.servlets;


import com.fitlife.User;
import com.fitlife.Workout;
import com.fitlife.dao.WorkoutDAO;

// Import Servlet libraries
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors; 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/workouts")
public class WorkoutServlet extends HttpServlet {

    private WorkoutDAO workoutDAO;

    @Override
    public void init() {
        workoutDAO = new WorkoutDAO();
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

     
        String action = request.getParameter("action");

        if ("delete".equals(action)) {

            int workoutId = Integer.parseInt(request.getParameter("id"));
            workoutDAO.deleteWorkout(workoutId);
  
            response.sendRedirect("workouts"); 

        } else {
           
            List<Workout> allWorkouts = workoutDAO.getWorkoutsByUserId(user.getUserId());

          
            String filterType = request.getParameter("type");
            String filterDate = request.getParameter("date");

            
            List<Workout> filteredWorkouts = allWorkouts.stream()
                .filter(w -> (filterType == null || filterType.isEmpty() || w.getActivityType().equalsIgnoreCase(filterType)))
                .filter(w -> (filterDate == null || filterDate.isEmpty() || w.getWorkoutDate().equals(filterDate)))
                .collect(Collectors.toList());

            
            request.setAttribute("workoutList", filteredWorkouts);
            request.getRequestDispatcher("workouts.jsp").forward(request, response);
        }
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        
        String activityType = request.getParameter("activityType");
        int durationMins = Integer.parseInt(request.getParameter("durationMins"));
        double distanceKm = Double.parseDouble(request.getParameter("distanceKm"));
        int caloriesBurned = Integer.parseInt(request.getParameter("caloriesBurned"));
        String workoutDate = request.getParameter("workoutDate");
        String notes = request.getParameter("notes");

        
        Workout newWorkout = new Workout();
        newWorkout.setUserId(user.getUserId());
        newWorkout.setActivityType(activityType);
        newWorkout.setDurationMins(durationMins);
        newWorkout.setDistanceKm(distanceKm);
        newWorkout.setCaloriesBurned(caloriesBurned);
        newWorkout.setWorkoutDate(workoutDate);
        newWorkout.setNotes(notes);

       
        workoutDAO.addWorkout(newWorkout);

       
        response.sendRedirect("workouts");
    }
}