package com.fitlife.servlets;


import com.fitlife.User;
import com.fitlife.Workout;
import com.fitlife.Goal;
import com.fitlife.dao.WorkoutDAO;
import com.fitlife.dao.GoalDAO;


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
    private GoalDAO goalDAO;

    @Override
    public void init() {
     
        workoutDAO = new WorkoutDAO();
        goalDAO = new GoalDAO();
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
        List<Goal> goals = goalDAO.getGoalsByUserId(user.getUserId());

   
        int totalWorkouts = workouts.size();
        int totalCalories = 0;
        double totalDistance = 0;

        for (Workout w : workouts) {
            totalCalories += w.getCaloriesBurned();
            totalDistance += w.getDistanceKm();
        }

    
        request.setAttribute("workoutList", workouts);
        request.setAttribute("goalList", goals);
        request.setAttribute("totalWorkouts", totalWorkouts);
        request.setAttribute("totalCalories", totalCalories);
        request.setAttribute("totalDistance", String.format("%.2f", totalDistance)); 

     
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}