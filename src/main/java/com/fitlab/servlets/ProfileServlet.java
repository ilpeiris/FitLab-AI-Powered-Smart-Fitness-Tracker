package com.fitlab.servlets;

import com.fitlab.User;
import com.fitlab.dao.UserDAO;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

   
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

  
        request.getRequestDispatcher("profile.jsp").forward(request, response);
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
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New passwords do not match.");
            request.getRequestDispatcher("profile.jsp").forward(request, response);
            return;
        }

      
        if (!userDAO.verifyPassword(user.getUserId(), currentPassword)) {
            request.setAttribute("error", "Incorrect current password.");
            request.getRequestDispatcher("profile.jsp").forward(request, response);
            return;
        }

     
        boolean success = userDAO.updatePassword(user.getUserId(), newPassword);

        if (success) {
            request.setAttribute("success", "Password updated successfully!");
        } else {
            request.setAttribute("error", "An error occurred. Please try again.");
        }

      
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}