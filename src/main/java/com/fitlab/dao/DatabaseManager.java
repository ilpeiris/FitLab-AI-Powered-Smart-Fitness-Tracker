package com.fitlab.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** This is the UPDATED DatabaseManager.. * It no longer has a hard-coded path. Instead, it waits for the AppLifecycleListener to give it the correct path. */


public class DatabaseManager {

   
    private static String DATABASE_URL;

    /**
     * This method is called by AppLifecycleListener when the app starts.
     * @param absolutePath The real, absolute path to fitlab.db
     */

    public static void setDatabasePath(String absolutePath) {
        DATABASE_URL = "jdbc:sqlite:" + absolutePath;
    }

    
    public static Connection getConnection() throws SQLException {
        if (DATABASE_URL == null) {
            throw new SQLException("Database path has not been set by the listener.");
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        }

        // This now uses the 100% correct, portable path
        return DriverManager.getConnection(DATABASE_URL);
    }
}