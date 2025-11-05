package com.fitlife.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This is a helper class to manage the database connection.
 * We use this so all our DAO classes can get a connection from one place.
 */
public class DatabaseManager {

    // This is the "connection string" for our SQLite database file.
    private static final String DATABASE_URL = "jdbc:sqlite:fitlife.db";

    /**
     * Loads the SQLite-JDBC driver and returns a connection to the database.
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            // This line loads the SQLite driver into memory.
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // This error means the sqlite-jdbc.jar file is missing (but Maven got it for us)
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        }

        // This line actually connects to the database file.
        return DriverManager.getConnection(DATABASE_URL);
    }
}