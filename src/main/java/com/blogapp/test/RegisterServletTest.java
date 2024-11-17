package com.blogapp.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.blogapp.servlets.RegisterServlet;
import com.blogapp.utils.DbUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class RegisterServletTest{

	@InjectMocks
	RegisterServlet regi = new RegisterServlet();
	
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement ps;

    @Mock
    private ResultSet rs;


    private StringWriter responseWriter;

    
    @BeforeEach
    public void setUp() throws Exception{
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }
    
    @Test
    public void testSuccessfulRegistrationAsAdmin() throws Exception {
       
    	
    	
    	try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
    		//request
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john.doe@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("Admin");
        when(request.getMethod()).thenReturn("POST");

        // session role ==Admin
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("Admin");

        // database 
        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        // Execute 
        regi.service(request, response);

        // Verify
        verify(session).setAttribute("id", 1);
        verify(response).sendRedirect("admin-dashboard.jsp?meassge=New user registerd successfully");} 
    	}
    
    
    @Test
    public void testSuccessfulRegistrationAsViewer() throws Exception {
        
    	
    	
    	try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){//request parameters
        when(request.getParameter("name")).thenReturn("Jane Doe");
        when(request.getParameter("email")).thenReturn("jane.doe@example.com");
        when(request.getParameter("password")).thenReturn("securepassword");
        when(request.getParameter("role")).thenReturn("Viewer"); // User wants Admin role, but is a Viewer
        when(request.getMethod()).thenReturn("POST");

        // session role===non-admin
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("role")).thenReturn(null);

        //database
        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(2);

        // Execute 
        regi.service(request, response);

        // Verify 
        verify(session).setAttribute("id", 2);
        verify(response).sendRedirect("login.jsp?message=Registration successful, please log in");} 
    	}
    
    
    @Test
    public void testRegistrationFailure() throws Exception {
       
    	
    	
    	try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
    		
    		//request parameters
    		
        when(request.getParameter("name")).thenReturn("Fail User");
        when(request.getParameter("email")).thenReturn("fail.user@example.com");
        when(request.getParameter("password")).thenReturn("failpassword");
        when(request.getParameter("role")).thenReturn("Admin");
        when(request.getMethod()).thenReturn("POST");

        // session role===Admin
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("Admin");

        // database failure
        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenThrow(new RuntimeException("Database error"));

        // Execute
        regi.service(request, response);

        // Verify
        verify(response).sendRedirect("register.jsp?error=Something went wrong");} 
    }

    @Test
    public void testInvalidSession() throws Exception {
        try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
        	
        	//request parameters
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john.doe@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("Admin");
        when(request.getMethod()).thenReturn("POST");

        // null session
        when(request.getSession()).thenReturn(null);

        //database
        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        // Execute
        regi.service(request, response);

        // Verify redirect=== Viewer
        verify(response).sendRedirect("register.jsp?error=Something went wrong");} 
    }
}