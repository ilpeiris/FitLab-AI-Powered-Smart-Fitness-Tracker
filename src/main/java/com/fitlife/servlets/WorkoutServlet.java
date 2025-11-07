package com.fitlife.servlets;


import com.fitlife.User;
import com.fitlife.Workout;
import com.fitlife.dao.WorkoutDAO;



// Import WEKA classes for AI model integration
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import java.util.ArrayList;
import javax.servlet.ServletConfig; // <-- Make sure this is here
import javax.servlet.ServletContext; // <-- Make sure this is here



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

// --- AI Model Variables ---
private Classifier aiModel;
private Instances dataHeader;
// -------------------------




@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config); // Call thisfirst

      
        workoutDAO = new WorkoutDAO();
        System.out.println("WorkoutDAO initialized."); 

        // load the WEKA AI model 
        try {
            // Get the real path to the model file inside WEB-INF
            ServletContext context = config.getServletContext();
            String modelPath = context.getRealPath("/WEB-INF/activity_model.model");

            if (modelPath == null) {
                throw new ServletException("Could not find model file. Make sure 'activity_model.model' is in /WEB-INF/");
            }

            System.out.println("Loading AI model from: " + modelPath);
            aiModel = (Classifier) SerializationHelper.read(modelPath);
            System.out.println("AI Model loaded successfully.");

    
   
            Attribute duration = new Attribute("duration_mins");
            Attribute distance = new Attribute("distance_km");
            Attribute calories = new Attribute("calories_burned");

    
            ArrayList<String> classValues = new ArrayList<>();
            classValues.add("Running");
            classValues.add("Cycling");
            classValues.add("Walking");
            classValues.add("Gym Workout");
            Attribute activityClass = new Attribute("activity_type", classValues);

           
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(duration);
            attributes.add(distance);
            attributes.add(calories);
            attributes.add(activityClass); // Add the class attribute last

     
            dataHeader = new Instances("PredictionInstance", attributes, 0);
            
     
            dataHeader.setClassIndex(dataHeader.numAttributes() - 1);

            System.out.println("WEKA data header created successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("CRITICAL ERROR: Could not load WEKA model.", e);
        }
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