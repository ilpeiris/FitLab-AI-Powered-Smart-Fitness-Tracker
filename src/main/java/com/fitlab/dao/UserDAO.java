package com.fitlab.dao;


import com.fitlab.User;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.mindrot.jbcrypt.BCrypt;


public class UserDAO {

    public boolean registerUser(User user) {


        String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";

  
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));



        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword); // Save the HASHED password

  
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
          
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String plainTextPassword) {
        String sql = "SELECT * FROM Users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {

             
                if (rs.next()) {
                    
                    String storedHashedPassword = rs.getString("password");

                    if (BCrypt.checkpw(plainTextPassword, storedHashedPassword)) {

                        
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                      

                        return user; 
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }

      
        return null;
    }



private String getHashedPasswordByUserId(int userId) {
    String sql = "SELECT password FROM Users WHERE user_id = ?";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, userId);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("password");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error getting hashed password: " + e.getMessage());
    }
    return null;
}


public boolean updatePassword(int userId, String newPassword) {
    
    String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

    String sql = "UPDATE Users SET password = ? WHERE user_id = ?";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, newHashedPassword);
        pstmt.setInt(2, userId);

        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error updating password: " + e.getMessage());
        return false;
    }
}


public boolean verifyPassword(int userId, String plainTextPassword) {
    String storedHashedPassword = getHashedPasswordByUserId(userId);

    if (storedHashedPassword != null) {
        return BCrypt.checkpw(plainTextPassword, storedHashedPassword);
    }
    return false;
}


}