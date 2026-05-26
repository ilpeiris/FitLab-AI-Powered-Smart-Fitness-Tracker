package com.fitlab;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class MLModelManager {

    private static Classifier aiModel;
    private static Instances dataHeader;

    public static void loadModel(ServletContext context) throws ServletException {
        try {
            String modelPath = context.getRealPath("/WEB-INF/activity_model.model");
            if (modelPath == null) {
                throw new ServletException("Could not find model file. Make sure 'activity_model.model' is in /WEB-INF/");
            }

            System.out.println("MLModelManager: Loading AI model from: " + modelPath);
            aiModel = (Classifier) SerializationHelper.read(modelPath);
            System.out.println("MLModelManager: AI Model loaded successfully.");

            // Create the data header
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
            attributes.add(activityClass);

            dataHeader = new Instances("PredictionInstance", attributes, 0);
            dataHeader.setClassIndex(dataHeader.numAttributes() - 1);

            System.out.println("MLModelManager: WEKA data header created successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("CRITICAL ERROR: MLModelManager could not load WEKA model.", e);
        }
    }

    public static Classifier getAiModel() {
        return aiModel;
    }

    public static Instances getDataHeader() {
        return dataHeader;
    }
}