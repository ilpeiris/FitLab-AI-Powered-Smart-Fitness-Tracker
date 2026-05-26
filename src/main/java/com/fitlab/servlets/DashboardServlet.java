package com.fitlab.servlets;


import com.fitlab.User;
import com.fitlab.Workout;
import com.fitlab.dao.WorkoutDAO;



import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

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

   
        List<Workout> workouts = workoutDAO.getWorkoutsByUserId(user.getUserId());
     

   
        int totalWorkouts = workouts.size();
        int totalCalories = 0;
        double totalDistance = 0;

        for (Workout w : workouts) {
            totalCalories += w.getCaloriesBurned();
            totalDistance += w.getDistanceKm();
        }

    
        request.setAttribute("workoutList", workouts);
        request.setAttribute("totalWorkouts", totalWorkouts);
        request.setAttribute("totalCalories", totalCalories);
        request.setAttribute("totalDistance", String.format("%.2f", totalDistance)); 

     
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}