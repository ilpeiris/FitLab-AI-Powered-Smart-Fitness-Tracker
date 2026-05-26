package com.fitlab.dao;

import com.fitlab.Workout; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WorkoutDAO {


    public boolean addWorkout(Workout workout) {
        String sql = "INSERT INTO Workouts (user_id, activity_type, duration_mins, distance_km, calories_burned, workout_date, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, workout.getUserId());
            pstmt.setString(2, workout.getActivityType());
            pstmt.setInt(3, workout.getDurationMins());
            pstmt.setDouble(4, workout.getDistanceKm());
            pstmt.setInt(5, workout.getCaloriesBurned());
            pstmt.setString(6, workout.getWorkoutDate());
            pstmt.setString(7, workout.getNotes());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding workout: " + e.getMessage());
            return false;
        }
    }


    public List<Workout> getWorkoutsByUserId(int userId) {
        List<Workout> workouts = new ArrayList<>();
        String sql = "SELECT * FROM Workouts WHERE user_id = ? ORDER BY workout_date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Workout workout = new Workout();
                    workout.setWorkoutId(rs.getInt("workout_id"));
                    workout.setUserId(rs.getInt("user_id"));
                    workout.setActivityType(rs.getString("activity_type"));
                    workout.setDurationMins(rs.getInt("duration_mins"));
                    workout.setDistanceKm(rs.getDouble("distance_km"));
                    workout.setCaloriesBurned(rs.getInt("calories_burned"));
                    workout.setWorkoutDate(rs.getString("workout_date"));
                    workout.setNotes(rs.getString("notes"));
                    workouts.add(workout);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting workouts: " + e.getMessage());
        }
        return workouts;
    }


    public boolean deleteWorkout(int workoutId) {
        String sql = "DELETE FROM Workouts WHERE workout_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, workoutId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting workout: " + e.getMessage());
            return false;
        }
    }



public Workout getWorkoutById(int workoutId) {
    String sql = "SELECT * FROM Workouts WHERE workout_id = ?";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, workoutId);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Workout workout = new Workout();
                workout.setWorkoutId(rs.getInt("workout_id"));
                workout.setUserId(rs.getInt("user_id"));
                workout.setActivityType(rs.getString("activity_type"));
                workout.setDurationMins(rs.getInt("duration_mins"));
                workout.setDistanceKm(rs.getDouble("distance_km"));
                workout.setCaloriesBurned(rs.getInt("calories_burned"));
                workout.setWorkoutDate(rs.getString("workout_date"));
                workout.setNotes(rs.getString("notes"));
                return workout;
            }
        }
    } catch (SQLException e) {
        System.err.println("Error getting workout by ID: " + e.getMessage());
    }
    return null; // Not found
}

public boolean updateWorkout(Workout workout) {
    String sql = "UPDATE Workouts SET activity_type = ?, duration_mins = ?, distance_km = ?, calories_burned = ?, workout_date = ?, notes = ? WHERE workout_id = ?";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, workout.getActivityType());
        pstmt.setInt(2, workout.getDurationMins());
        pstmt.setDouble(3, workout.getDistanceKm());
        pstmt.setInt(4, workout.getCaloriesBurned());
        pstmt.setString(5, workout.getWorkoutDate());
        pstmt.setString(6, workout.getNotes());
        pstmt.setInt(7, workout.getWorkoutId());

        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error updating workout: " + e.getMessage());
        return false;
    }
}

  
}