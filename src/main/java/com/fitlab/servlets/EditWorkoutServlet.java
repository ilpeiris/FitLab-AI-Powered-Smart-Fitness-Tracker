package com.fitlab.servlets;


import com.fitlab.User;
import com.fitlab.Workout;
import com.fitlab.dao.WorkoutDAO;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



@WebServlet("/edit-workout")
public class EditWorkoutServlet extends HttpServlet {

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

        try {
       
            int workoutId = Integer.parseInt(request.getParameter("id"));

        
            Workout workout = workoutDAO.getWorkoutById(workoutId);

            if (workout != null) {
      
                request.setAttribute("workout", workout);
        
                request.getRequestDispatcher("edit-workout.jsp").forward(request, response);
            } else {
        
                response.sendRedirect("workouts");
            }
        } catch (Exception e) {
   
            response.sendRedirect("workouts");
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


        int workoutId = Integer.parseInt(request.getParameter("workoutId"));
        String activityType = request.getParameter("activityType");
        int durationMins = Integer.parseInt(request.getParameter("durationMins"));
        double distanceKm = Double.parseDouble(request.getParameter("distanceKm"));
        int caloriesBurned = Integer.parseInt(request.getParameter("caloriesBurned"));
        String workoutDate = request.getParameter("workoutDate");
        String notes = request.getParameter("notes");
        User user = (User) session.getAttribute("user");

        Workout updatedWorkout = new Workout();
        updatedWorkout.setWorkoutId(workoutId);
        updatedWorkout.setUserId(user.getUserId()); // Set the user ID
        updatedWorkout.setActivityType(activityType);
        updatedWorkout.setDurationMins(durationMins);
        updatedWorkout.setDistanceKm(distanceKm);
        updatedWorkout.setCaloriesBurned(caloriesBurned);
        updatedWorkout.setWorkoutDate(workoutDate);
        updatedWorkout.setNotes(notes);

        workoutDAO.updateWorkout(updatedWorkout);

        response.sendRedirect("workouts");
    }
}