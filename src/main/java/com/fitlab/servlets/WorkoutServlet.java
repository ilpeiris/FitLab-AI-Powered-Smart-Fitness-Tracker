package com.fitlab.servlets;


import com.fitlab.User;
import com.fitlab.Workout;
import com.fitlab.dao.WorkoutDAO;

import com.fitlab.MLModelManager; // <-- Imported the new manager

// Import WEKA classes for AI model integration
import weka.classifiers.Classifier;
//import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
//import weka.core.SerializationHelper;
//import java.util.ArrayList;
import javax.servlet.ServletConfig; // <-- Make sure this is here
//import javax.servlet.ServletContext; // <-- Make sure this is here



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
    public void init(ServletConfig config) throws ServletException {
        super.init(config); // Call thisfirst

      
        workoutDAO = new WorkoutDAO();
        System.out.println("WorkoutDAO initialized."); 

        // removed the WEKA AI model 
      
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




// ---  UPDATED AI PREDICTION STEP ---
        String aiPrediction = "AI prediction could not be run."; 
        try {
            // Get the model and header from our static manager
            Classifier aiModel = MLModelManager.getAiModel();
            Instances dataHeader = MLModelManager.getDataHeader();

            if (aiModel != null) {
                DenseInstance newInstance = new DenseInstance(dataHeader.numAttributes());
                newInstance.setDataset(dataHeader); 
                newInstance.setValue(dataHeader.attribute("duration_mins"), durationMins);
                newInstance.setValue(dataHeader.attribute("distance_km"), distanceKm);
                newInstance.setValue(dataHeader.attribute("calories_burned"), caloriesBurned);
               
                double predictionIndex = aiModel.classifyInstance(newInstance);
                aiPrediction = dataHeader.classAttribute().value((int) predictionIndex);
                
                System.out.println("--- AI PREDICTION SUCCESS (from Servlet) ---");
                System.out.println("Predicted Activity: " + aiPrediction);
                System.out.println("------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            aiPrediction = "Error during AI prediction.";
            System.err.println("Error during AI prediction: " + e.getMessage());
        }
        // --- END OF UPDATED AI PREDICTION STEP ---



        Workout newWorkout = new Workout();
        newWorkout.setUserId(user.getUserId());
        newWorkout.setActivityType(activityType); 
        newWorkout.setDurationMins(durationMins);
        newWorkout.setDistanceKm(distanceKm);
        newWorkout.setCaloriesBurned(caloriesBurned);
        newWorkout.setWorkoutDate(workoutDate);
        newWorkout.setNotes(notes);

        workoutDAO.addWorkout(newWorkout);

        
        session.setAttribute("aiPrediction", aiPrediction);   
        session.setAttribute("userSelection", activityType); 
       
        response.sendRedirect("workouts");
    }



}