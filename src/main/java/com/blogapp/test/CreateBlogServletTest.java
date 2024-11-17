package com.blogapp.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.blogapp.servlets.CreateBlogServlet;
import com.blogapp.utils.DbUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

class CreateBlogServletTest {

	    @InjectMocks
	    private CreateBlogServlet createBlog = new CreateBlogServlet();

	    @Mock
	    private HttpServletRequest request;

	    @Mock
	    private HttpServletResponse response;

	    @Mock
	    private HttpSession session;

	    @Mock
	    private Part imagePart;

	    @Mock
	    private Connection connection;

	    @Mock
	    private PreparedStatement ps;

	    @BeforeEach
	    public void setUp() throws Exception {
	        MockitoAnnotations.openMocks(this);
	        }

	    @Test
	    public void testSuccessfulBlogCreation() throws Exception {
	        try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
	        	
	        	//request parameters
	        when(request.getParameter("title")).thenReturn("Test Blog Title");
	        when(request.getParameter("content")).thenReturn("Test Blog Content");
	        when(request.getPart("image")).thenReturn(imagePart);
	        when(request.getMethod()).thenReturn("POST");

	        //image data
	        byte[] imageData = "fakeImageData".getBytes();
	        InputStream imageInputStream = new ByteArrayInputStream(imageData);
	        when(imagePart.getSize()).thenReturn((long) imageData.length);
	        when(imagePart.getInputStream()).thenReturn(imageInputStream);

	        // session
	        when(request.getSession(false)).thenReturn(session);
	        when(session.getAttribute("role")).thenReturn("Admin");
	        when(session.getAttribute("id")).thenReturn(1);

	        //  database 
	        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
	        when(connection.prepareStatement(anyString())).thenReturn(ps);

	        // Execute the servlet
	        createBlog.service(request, response);

	        // Verify database operations
	        verify(ps).setString(1, "Test Blog Title");
	        verify(ps).setString(2, "Test Blog Content");
	        verify(ps).setBlob(3, imageInputStream);
	        verify(ps).setInt(4, 1);
	        verify(ps).executeUpdate();

	        // Verify redirect
	        verify(response).sendRedirect("admin-dashboard.jsp?message=Blog post Added successfully");} 
	    }

	 


	    @Test
	    public void testNoImageProvided() throws Exception {
	        try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
	        	
	        	//request parameters
	        when(request.getParameter("title")).thenReturn("Test Blog Without Image");
	        when(request.getParameter("content")).thenReturn("Test Content Without Image");
	        when(request.getPart("image")).thenReturn(null);
	        when(request.getMethod()).thenReturn("POST");

	        // session
	        when(request.getSession(false)).thenReturn(session);
	        when(session.getAttribute("role")).thenReturn("Admin");
	        when(session.getAttribute("id")).thenReturn(1);

	        //database 
	        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
	        when(connection.prepareStatement(anyString())).thenReturn(ps);

	        // Execute
	        createBlog.service(request, response);

	        // Verify database 
	        verify(ps).setString(1, "Test Blog Without Image");
	        verify(ps).setString(2, "Test Content Without Image");
	        verify(ps).setNull(3, java.sql.Types.BLOB);
	        verify(ps).setInt(4, 1);
	        verify(ps).executeUpdate();

	        // Verify redirect
	        verify(response).sendRedirect("admin-dashboard.jsp?message=Blog post Added successfully");} 
	    }

	    @Test
	    public void testDatabaseError() throws Exception {
	        try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
	        	
	        	//request parameters
	        when(request.getParameter("title")).thenReturn("Test Blog");
	        when(request.getParameter("content")).thenReturn("Test Content");
	        when(request.getPart("image")).thenReturn(imagePart);
	        when(request.getMethod()).thenReturn("POST");

	        //image data
	        byte[] imageData = "fakeImageData".getBytes();
	        InputStream imageInputStream = new ByteArrayInputStream(imageData);
	        when(imagePart.getSize()).thenReturn((long) imageData.length);
	        when(imagePart.getInputStream()).thenReturn(imageInputStream);

	        // session
	        when(request.getSession(false)).thenReturn(session);
	        when(session.getAttribute("role")).thenReturn("Admin");
	        when(session.getAttribute("id")).thenReturn(1);

	        // database 
	        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
	        when(connection.prepareStatement(anyString())).thenThrow(new RuntimeException("Database error"));

	        // Execute
	        createBlog.service(request, response);

	        // Verify
	        verify(response).sendRedirect("create-blog.jsp?error=Someting went wrong"); 
	    }
	}

}