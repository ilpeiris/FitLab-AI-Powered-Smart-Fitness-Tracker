package com.fitlab.api;

// JAX-RS Imports
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// WEKA and FitLAB Imports
import com.fitlab.MLModelManager;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instances;

@Path("/predict") // Available at /api/predict
public class PredictionResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrediction(
            @QueryParam("duration") int duration,
            @QueryParam("distance") double distance,
            @QueryParam("calories") int calories) {

        try {
            // Get the model and header from static manager
            Classifier aiModel = MLModelManager.getAiModel();
            Instances dataHeader = MLModelManager.getDataHeader();

            if (aiModel == null || dataHeader == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                               .entity("{\"error\": \"AI model is not loaded\"}")
                               .build();
            }

        
            double[] values = new double[dataHeader.numAttributes()];

        
            values[dataHeader.attribute("duration_mins").index()] = duration;
            values[dataHeader.attribute("distance_km").index()] = distance;
            values[dataHeader.attribute("calories_burned").index()] = calories;
            
         
            values[dataHeader.classIndex()] = weka.core.Utils.missingValue();

            DenseInstance newInstance = new DenseInstance(1.0, values);
         
            newInstance.setDataset(dataHeader);

            double predictionIndex = aiModel.classifyInstance(newInstance);
            String predictedActivity = dataHeader.classAttribute().value((int) predictionIndex);
            
            
            String jsonResponse = "{\"prediction\": \"" + predictedActivity + "\"}";
            return Response.ok(jsonResponse).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        }
    }
}