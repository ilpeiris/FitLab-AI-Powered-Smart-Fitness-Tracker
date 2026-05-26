package com.fitlab.ml;


import weka.classifiers.Classifier; 
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes; // Import NaiveBayes
import weka.classifiers.trees.J48;        // Import J48 (Decision Tree)
import weka.classifiers.trees.RandomForest; // Import RandomForest
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.HashMap;
import java.util.Map;


public class ModelTrainer {

    public static void main(String[] args) {
        try {
            
            
            DataSource trainSource = new DataSource("src/main/resources/data/workouts-training.arff");
            Instances trainingSet = trainSource.getDataSet();
            trainingSet.setClassIndex(trainingSet.numAttributes() - 1); 

            
            DataSource testSource = new DataSource("src/main/resources/data/workouts-testing.arff");
            Instances testingSet = testSource.getDataSet();
            testingSet.setClassIndex(testingSet.numAttributes() - 1); 

            System.out.println("Loaded " + trainingSet.numInstances() + " training instances.");
            System.out.println("Loaded " + testingSet.numInstances() + " testing instances.");
            System.out.println("------------------------------------");

            
            Map<String, Classifier> modelsToTest = new HashMap<>();
            modelsToTest.put("J48 Decision Tree", new J48());
            modelsToTest.put("RandomForest", new RandomForest());
            modelsToTest.put("NaiveBayes", new NaiveBayes());

            Classifier bestModel = null;
            double bestAccuracy = 0.0;
            String bestModelName = "";

            
            for (Map.Entry<String, Classifier> entry : modelsToTest.entrySet()) {
                String modelName = entry.getKey();
                Classifier classifier = entry.getValue();

                System.out.println("Training " + modelName + "...");

                //Train the model on the TRAINING set
                classifier.buildClassifier(trainingSet);

                // Evaluate the model on the TESTING set
                Evaluation eval = new Evaluation(trainingSet);
                eval.evaluateModel(classifier, testingSet);

                //  Print the results for our report
                System.out.println("--- RESULTS FOR: " + modelName + " ---");
                System.out.println(eval.toSummaryString());
                System.out.println(eval.toMatrixString()); 
                System.out.println("Accuracy: " + eval.pctCorrect() + "%");
                System.out.println("------------------------------------");

                //  Check if this is the best model so far
                if (eval.pctCorrect() > bestAccuracy) {
                    bestAccuracy = eval.pctCorrect();
                    bestModel = classifier;
                    bestModelName = modelName;
                }
            }

            //SAVE THE *BEST* MODEL
            if (bestModel != null) {
                String modelFilePath = "activity_model.model";
                SerializationHelper.write(modelFilePath, bestModel);

                System.out.println("====================================");
                System.out.println("CRITICAL EVALUATION COMPLETE.");
                System.out.println("The best model was " + bestModelName + " with " + bestAccuracy + "% accuracy.");
                System.out.println("Saving this model to: " + modelFilePath);
                System.out.println("====================================");
            } else {
                System.out.println("Error: No models were trained.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}