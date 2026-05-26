package com.fitlab.servlets;


import com.fitlab.User;
import com.fitlab.dao.UserDAO;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

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

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); 

        boolean success = userDAO.registerUser(newUser);

        if (success) {
           
            response.sendRedirect("login.jsp?success=true");
        } else {
            
            request.setAttribute("error", "Username already exists. Please try another.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}