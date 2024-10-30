package com.blogapp.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.blogapp.utils.DbUtils;


public class AdminDashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("Servlet called");

		HttpSession session  = request.getSession(false);//false removed causing problems
		String role = (String)session.getAttribute("role");
		if(session == null || !"Admin".equals(role)) {
			response.sendRedirect("login.jsp?error=Unauthorized");
			return;
		}
		
		 int adminId = (Integer) session.getAttribute("id");
//		 System.out.println(adminId);
	        if (adminId ==0) {
	            response.sendRedirect("login.jsp?error=SessionExpired");
	            return;
	        }
		
		 
		        List<List<Object>> blogsList = new ArrayList<>();
		        
		        Connection conn =null;
		        PreparedStatement ps =null;
		        ResultSet rs =null;

		        
		        try{
		        	
		        	 conn = DbUtils.connectDB();
		        	 
		            String sql = "select * FROM blogs";
		             ps = conn.prepareStatement(sql);
		             rs = ps.executeQuery();

		             //System.out.println(rs.getInt("id"));
		             
		             //System.out.println(rs.next());
		             
		            while (rs.next()) {
		                List<Object> blog = new ArrayList<>();
		                blog.add(rs.getInt("id"));                 // Blog ID
		                blog.add(rs.getString("title"));           // Blog Title
		                blog.add(rs.getTimestamp("created_at"));   // Created Date
		                blogsList.add(blog);                       // Add blog to list
		            }

		            //System.out.println(blogsList.isEmpty());
		           // System.out.println("Blogs retrieved: " + blogsList.size());

		            request.setAttribute("blogs", blogsList);
		            RequestDispatcher dispatcher = request.getRequestDispatcher("admin-dashboard.jsp");
		            
		            dispatcher.forward(request, response);

		        } catch (Exception e) {
		            e.printStackTrace();
		            response.sendRedirect("login.jsp?error=DatabaseError");
		        }finally {
					try {
						if(rs!=null) rs.close();
						if(ps!=null) ps.close();
						if(conn!=null) conn.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
		        
		    }
	}

	


