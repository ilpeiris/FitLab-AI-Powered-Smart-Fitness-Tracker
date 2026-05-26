/**
 * FitLAB — ML-Powered Fitness Tracking Platform
 * Author:  Isuru Lakmal Peiris
 * GitHub:  github.com/ilpeiris
 * License: GPL v3
 */



package com.fitlab;

import com.fitlab.dao.DatabaseManager; // Import our DatabaseManager

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * This is a ServletContextListener. This class runs ONE TIME when the web application starts up. We will use it to find the *real* path of our database and save it in the DatabaseManager.
 */
@WebListener // This annotation tells Tomcat to run this class on startup
public class AppLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // This method runs when the app starts.

        //get the ServletContext
        javax.servlet.ServletContext context = sce.getServletContext();

        //get the "real, absolute path" to our database file
        // This will be C:\apache-tomcat-9\webapps\fitlab\fitlab.db 
        String dbPath = context.getRealPath("/fitlab.db");

        // Save this path in our DatabaseManager for all our DAOs to use.
        DatabaseManager.setDatabasePath(dbPath);

        System.out.println("-------------------------------------------------");
        System.out.println("DATABASE PATH SET TO: " + dbPath);
        System.out.println("-------------------------------------------------");
   
   // ---  NEW CODE for realtime ai ---
        try {
            com.fitlab.MLModelManager.loadModel(context); // Load the AI model
        } catch (javax.servlet.ServletException e) {
            e.printStackTrace();
        }
        // -------------------------
   
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // This method runs when the app shuts down. We don't need it.
    }
}
