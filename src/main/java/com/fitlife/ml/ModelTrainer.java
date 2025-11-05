package com.fitlife.ml;

// Import all the WEKA classes we need
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48; // This is the J48 Decision Tree classifier
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Random;

/**
 * This is a standalone program to train our AI model.
 * We run this file ONCE to create the 'activity_model.model' file.
 * This fulfills all of Part B, items 2 and 3, of the assignment.
 */
public class ModelTrainer {

    // This 'main' method means we can run this file directly
    public static void main(String[] args) {
        try {
            // --- 1. LOAD THE DATASET ---
            String dataFilePath = "src/main/resources/data/workouts.arff";
            DataSource source = new DataSource(dataFilePath);
            Instances dataset = source.getDataSet();

            // Tell WEKA which "column" is the one we want to predict.
            dataset.setClassIndex(dataset.numAttributes() - 1);

            System.out.println("Loaded dataset: " + dataFilePath);
            System.out.println("Number of instances: " + dataset.numInstances());
            System.out.println("------------------------------------");

            // --- 2. TRAIN THE CLASSIFIER ---
            J48 classifier = new J48();
            classifier.buildClassifier(dataset);

            System.out.println("Classifier trained successfully.");
            System.out.println("------------------------------------");

            // --- 3. EVALUATE THE MODEL (FOR OUR REPORT) ---
            Evaluation eval = new Evaluation(dataset);

            // We'll use 10-fold Cross-Validation as shown in the slides
            eval.crossValidateModel(classifier, dataset, 10, new Random(1));

            System.out.println("--- MODEL EVALUATION (FOR REPORT) ---");
            System.out.println(eval.toSummaryString());
            System.out.println(eval.toMatrixString());
            System.out.println("------------------------------------");


            // --- 4. SAVE THE TRAINED MODEL ---
            // This is the most important step. We save the "smart" model to a file.
            String modelFilePath = "activity_model.model";
            SerializationHelper.write(modelFilePath, classifier);

            System.out.println("Trained model saved to: " + modelFilePath);

        } catch (Exception e) {
            // This will catch any errors, like "file not found"
            e.printStackTrace();
        }
    }
}