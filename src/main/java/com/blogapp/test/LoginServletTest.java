package com.blogapp.test;


import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.bcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.blogapp.servlets.LoginServlet;
import com.blogapp.utils.DbUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


class LoginServletTest {

	@InjectMocks
	private LoginServlet servlet = new LoginServlet();
	
	
	
	@Mock
	 private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;
    
    @BeforeEach
    public void setUp() {
    	MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testDoPost_SuccessfulAdminLogin() throws Exception{
    	
    	try(MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)) {//for request
    	when(request.getParameter("email")).thenReturn("admin@gmail.com");
    	when(request.getParameter("password")).thenReturn("Admin@123");
    	when(request.getSession()).thenReturn(session);
    	
    	//my dopost not working in test as eclips ask me to make dopost public which is not recc. practice
    	//im using servlet.service and passing method type as post
    	when(request.getMethod()).thenReturn("POST");
    	
    	
    	// data connect
    	
    	//when(DbUtils.connectDB()).thenReturn(connection);
    	mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
    	when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    	when(preparedStatement.executeQuery()).thenReturn(resultSet);
    	
    	
    	when(resultSet.next()).thenReturn(true); // User found
       
    	when(resultSet.getString("password")).thenReturn(BCrypt.hashpw("Admin@123", BCrypt.gensalt()));
        when(resultSet.getString("role")).thenReturn("Admin");
        when(resultSet.getString("name")).thenReturn("Admin User");
        when(resultSet.getInt("id")).thenReturn(1);
    	
        servlet.service(request, response);
        //servlet.doPost(request, response);
    	
        //verifying and redirecting
        verify(session).setAttribute("name", "Admin User");
        verify(session).setAttribute("role", "Admin");
        verify(session).setAttribute("email", "admin@gmail.com");
        verify(session).setAttribute("id", 1);
        verify(response).sendRedirect("admin-dashboard.jsp");} 
        
    }
    
    @Test
    public void testDoPost_InvalidPassword() throws Exception {
        try(MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)) {//  request 
        when(request.getParameter("email")).thenReturn("admin@example.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(request.getSession()).thenReturn(session);
        when(request.getMethod()).thenReturn("POST");

        // database connection and query
        //when(DbUtils.connectDB()).thenReturn(connection);
        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        
        // finding user likre if rs.next() true 
        when(resultSet.next()).thenReturn(true);
        
        when(resultSet.getString("password")).thenReturn(BCrypt.hashpw("admin123", BCrypt.gensalt()));

       
        servlet.service(request, response);
       // servlet.doPost(request, response);

      //verifying and redirecting
        
        verify(response).sendRedirect("login.jsp?error=Invalid Password");} 
    }
    
    @Test
    public void testDoPost_UserNotFound() throws Exception {
        try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
			//request parameters
			when(request.getParameter("email")).thenReturn("non@example.com");
			when(request.getParameter("password")).thenReturn("password");
			when(request.getSession()).thenReturn(session);
			when(request.getMethod()).thenReturn("POST");

			//database connection and query
			//when(DbUtils.connectDB()).thenReturn(connection);
			
			mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
			when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
			when(preparedStatement.executeQuery()).thenReturn(resultSet);
			when(resultSet.next()).thenReturn(false); 

			// sewrvice for dopost
			servlet.service(request, response);
			//servlet.doPost(request, response);

			// verify &7 redirect
			verify(response).sendRedirect("login.jsp?error= User not found");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    @Test
    public void testDoPost_DatabaseError() throws Exception {
        try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){//request parameters
        when(request.getParameter("email")).thenReturn("admin@example.com");
        when(request.getParameter("password")).thenReturn("admin123");
        when(request.getMethod()).thenReturn("POST");

        //database exception
       // when(DbUtils.connectDB()).thenThrow(new SQLException("Database connection error"));
        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);

      
       servlet.service(request, response);
        //servlet.doPost(request, response);

        // Verify redirection 
        verify(response).sendRedirect("login.jsp?error=Something went wrong");
        } 
    }
    
    
    

}
