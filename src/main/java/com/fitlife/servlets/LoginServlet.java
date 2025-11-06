package com.fitlife.servlets;


import com.fitlife.User;
import com.fitlife.dao.UserDAO;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
       
        userDAO = new UserDAO();
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

      
        String username = request.getParameter("username");
        String password = request.getParameter("password");

       
        User user = userDAO.loginUser(username, password);

        if (user != null) {
           
            HttpSession session = request.getSession(true);

            session.setAttribute("user", user); 

           
            response.sendRedirect("dashboard");

        } else {
         
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}