package com.blogapp.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.blogapp.servlets.BlogServlet;
import com.blogapp.utils.DbUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class BlogServletTest {


	@InjectMocks
	    private BlogServlet blogServlet = new BlogServlet();

	    @Mock
	    private HttpServletRequest request;

	    @Mock
	    private HttpServletResponse response;

	    @Mock
	    private HttpSession session;

	    @Mock
	    private RequestDispatcher dispatcher;

	    @Mock
	    private Connection connection;

	    @Mock
	    private PreparedStatement ps;

	    @Mock
	    private ResultSet rs;

	    @BeforeEach
	    void setUp(){
	        MockitoAnnotations.openMocks(this);
	    }
	    
	    @Test
	    void testSearchActionWithTitle() throws Exception {
	        try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){// Mock request parameters
	        when(request.getParameter("action")).thenReturn("search");
	        when(request.getParameter("query")).thenReturn("Test Title");
	        when(request.getParameter("type")).thenReturn("Title");
	        when(request.getMethod()).thenReturn("GET");

	        //database conn
	        
	        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
	        when(connection.prepareStatement(anyString())).thenReturn(ps);
	        when(ps.executeQuery()).thenReturn(rs);
	        when(rs.next()).thenReturn(true);

	        //request forwarding
	        
	        when(request.getRequestDispatcher("viewer-home.jsp")).thenReturn(dispatcher);

	        // Execute 
	       
	        blogServlet.service(request, response);

	        // Verify 
	        
	        verify(request).setAttribute(eq("blogs"), any(ResultSet.class));
	        verify(dispatcher).forward(request, response);
	        verify(ps).close();
	        verify(connection).close();
	        }
	    }
	    
	    @Test
	    void testViewAction() throws Exception {
	        
	    	try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
	    	//request parameters
	        
	    	when(request.getParameter("action")).thenReturn("view");
	        when(request.getParameter("id")).thenReturn("1");
	        when(request.getMethod()).thenReturn("GET");

	        //database conn
	        
	        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
	        when(connection.prepareStatement(anyString())).thenReturn(ps);
	        when(ps.executeQuery()).thenReturn(rs);

	        //rs data
	        when(rs.next()).thenReturn(true);
	        when(rs.getString("title")).thenReturn("Sample Title");
	        when(rs.getString("content")).thenReturn("Sample Content");
	        when(rs.getString("created_at")).thenReturn("2024-11-17");

	        // request forwarding
	        when(request.getRequestDispatcher("view-blog.jsp")).thenReturn(dispatcher);

	        // execute
	        
	        blogServlet.service(request, response);

	        // Verify
	        
	        verify(request).setAttribute("title", "Sample Title");
	        verify(request).setAttribute("content", "Sample Content");
	        verify(request).setAttribute("created_at", "2024-11-17");
	        verify(dispatcher).forward(request, response);
	        verify(ps).close();
	        verify(connection).close();
	    }
	    }

	    @Test
	    void testDefaultAction() throws Exception {
	        
	    	try (MockedStatic<DbUtils> mockedDbUtils = Mockito.mockStatic(DbUtils.class)){
	    	
	    	// specific action
	        
	    	when(request.getParameter("action")).thenReturn(null);
	        when(request.getMethod()).thenReturn("GET");

	        //  database
	        mockedDbUtils.when(DbUtils::connectDB).thenReturn(connection);
	        when(connection.prepareStatement(anyString())).thenReturn(ps);
	        when(ps.executeQuery()).thenReturn(rs);
	        when(rs.next()).thenReturn(true);

	        // request forwarding
	        when(request.getRequestDispatcher("viewer-home.jsp")).thenReturn(dispatcher);

	        // execute
	        blogServlet.service(request, response);

	        // Verify
	        verify(request).setAttribute(eq("blogs"), any(ResultSet.class));
	        verify(dispatcher).forward(request, response);
	        verify(ps).close();
	        verify(connection).close();
	    }
	    }
	    

}
